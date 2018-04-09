(ns clj-z80.image
  (:require [clj-z80.bytes :as b]
            [clojure.string :as str]))


;; pages

(def pages (atom {}))

(def ^:dynamic *current-page* nil)

(defn reset-pages!
  []
  (reset! pages {}))

(defn- make-page
  [page-index origin-address length name]
  {:page    page-index
   :name    name
   :origin  origin-address
   :address 0
   :image   (atom (vec (replicate length 0)))})

(defn defpage
  [page-index origin-address length & [name]]
  (let [page (make-page page-index origin-address length name)]
    (swap! pages assoc page-index page)))

(defn get-page
  [page-index]
  (get @pages page-index))

(defn assert-page
  [page-index]
  (when (nil? (get-page page-index))
    (throw (Exception. (str "Undefined page " page-index)))))

(defmacro with-page
  [page-index & body]
  `(let [p# ~page-index]
     (assert-page p#)
     (binding [*current-page* p#]
       ~@body)))

(defn get-current-page
  []
  (get-page *current-page*))

(defn find-page
  [needed-space & [candidates]]
  (->> (if candidates
         (select-keys @pages candidates)
         @pages)
       (sort-by first)
       vals
       (remove (fn [{:keys [address image]}]
                 (> (+ address needed-space) (count @image))))
       first
       :page))

(defn get-pages-by-name
  [name]
  (->> @pages
       vals
       (filter #(= (:name %) name))
       (mapv :page)))


;; ns

(def ^:dynamic *current-ns* [])

(defmacro with-ns
  [ns & body]
  `(let [ns# ~ns]
     (binding [*current-ns* ns#]
       ~@body)))


;; labels

(def labels (atom {}))

(defn reset-labels!
  []
  (reset! labels {}))

(defn get-label
  [id]
  (or
   (get @labels (keyword (str/join "/" (map name (conj *current-ns* id)))))
   (get @labels id)
   (throw (Exception. (str "Unknown label " id)))))

(defn assert-undeclared-label
  [id]
  (when-not (nil? (get-label id))
    (throw (Exception. (str "Label already exists " id)))))

(defn set-label!
  ([id address]
   (let [label {:address address}]
     (swap! labels assoc id label)))
  ([id]
   (assert-page *current-page*)
   (let [page  (get-page *current-page*)
         label {:page    *current-page*
                :address (+ (:address page)
                            (:origin page))}]
     (swap! labels assoc id label))))


;; emit bytes

(defn emit-byte
  [b]
  (assert-page *current-page*)
  (let [page    (get-current-page)
        address (:address page)
        image   (:image page)]
    (when (>= address (count @image))
      (throw (Exception. (str "Page overflow " (:page page)))))
    (swap! image assoc address b)
    (swap! pages update-in [*current-page* :address] inc)))

(defn emit-bytes
  [bytes]
  (dorun
   (for [b bytes]
     (if (and (vector? b) (= (first b) :label))
       (set-label! (second b))
       (emit-byte b)))))


;; build image

(defn- build-image-page
  [{:keys [image origin]}]
  (letfn [(check-type [b] (assert (number? b)) b)]
    (vec
     (reduce (fn [out b]
               (->> (cond (number? b)               b
                          (b/byte-label? b)         (-> b second get-label :address b/b)
                          (b/low-word-label? b)     (-> b second get-label :address b/lw)
                          (b/high-word-label? b)    (-> b second get-label :address b/hw)
                          (b/displacement-label? b) (let [label        (-> b second get-label :address)
                                                          current      (+ (count out) origin)
                                                          instr-opcode 1]
                                                      (b/e (- label current instr-opcode))))
                    check-type
                    (conj out)))
             []
             @image))))

(defn build-image
  []
  (->> @pages
       vals
       (sort-by :page)
       (map build-image-page)
       (apply concat)
       (into [])))
