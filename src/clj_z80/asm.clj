(ns clj-z80.asm
  (:require [clj-z80.asm-header :refer [setup-image-header!]]
            [clj-z80.bytes :as b]
            [clj-z80.image :refer :all]
            [clj-z80.opcodes :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            clj-z80.msx-image))

;; asm

(defn asm
  [ns instrs]
  (->> instrs
       (reduce (fn [out instr]
                 (concat out
                         (cond (and (vector? instr) (= (first instr) :local-label))
                               [instr]

                               (vector? (first instr))
                               (asm ns instr)

                               :else
                               (assemble-instr instr))))
               [])
       (into [])))

(def procedures (atom {}))

(defn reset-asm!
  []
  (reset! procedures {}))

(defn- format-local-labels
  [ns instrs]
  (let [locals (->> instrs
                    (filter vector?)
                    (filter #(= (first %) :local-label))
                    (map second)
                    (into #{}))]
    (mapv (fn [i]
            (if (and (vector? i)
                     (contains? locals (second i)))
              (let [id (keyword (str/join "/" (map name (conj ns (second i)))))]
                (if (= (first i) :local-label)
                  [:label id]
                  [(first i) id]))
              i))
          instrs)))

(defn defasmproc
  [id {:keys [page]} & instrs]
  (when (nil? page)
    (throw (Exception. (str "Proc missing page " id))))
  (swap! procedures assoc id {:id      id
                              :params  {:page page}
                              :opcodes (format-local-labels [id] (asm [id] instrs))}))


;; asm utils

(defn label
  [id & instrs]
  (vec (cons [:local-label id]
             instrs)))

(defn db
  [& bytes]
  (vec
   (mapcat (fn [n]
             (cond (string? n)
                   (mapv (fn [c] [:db (int c)]) n)

                   (number? n)
                   [[:db (b/b n)]]))
           bytes)))

(defn dw
  [& words]
  (vec
   (mapcat (fn [w]
             [(b/lw w) (b/hw w)])
           words)))

(defn ds
  ([len value]
   (repeat len [:db (b/b value)]))
  ([len]
   (ds len 0)))


;; image

(defn- emit-procs-bytes!
  []
  (->> @procedures
       vals
       (map (fn [proc]
              (with-ns [(:id proc)]
                (with-page (:page (:params proc))
                  (set-label! (:id proc))
                  (emit-bytes (:opcodes proc))))))
       dorun))

(defn build-asm-image
  [image-type]
  (reset-pages!)
  (reset-labels!)
  (setup-image-header! image-type)
  (emit-procs-bytes!)
  (build-image))

(defn build-asm-image-file
  [filename image-type]
  (let [image (byte-array (build-asm-image image-type))]
    (with-open [out (io/output-stream (io/file filename))]
      (.write out image))))
