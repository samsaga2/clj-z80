(ns clj-z80.asm
  (:require [clj-z80.asm-header :refer [setup-image-header! variables-origin]]
            [clj-z80.bytes :as b]
            [clj-z80.image :refer :all]
            [clj-z80.opcodes :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            clj-z80.msx-image))


;; asm procedures

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

(defn- make-proc
  [id page instrs]
  {:id      id
   :params  {:page page}
   :opcodes (format-local-labels [id] (asm [id] instrs))})

(defn defasmproc
  [id {:keys [page]} & instrs]
  (when (nil? page)
    (throw (Exception. (str "Proc missing page " id))))
  (let [proc (make-proc id page instrs)]
    (swap! procedures assoc id proc)))


;; asm variables

(def variables (atom {}))

(defn defasmvar
  [id len]
  (let [var {:id  id
             :len len}]
    (swap! variables assoc id var)))

(defn defasmbyte
  [id]
  (defasmvar id 1))

(defn defasmword
  [id]
  (defasmvar id 2))

(defn- declare-vars!
  []
  (let [address (atom @variables-origin)]
    (dorun
     (for [v (vals @variables)]
       #dbg
       (do (set-label! (:id v) @address)
           (reset! address (+ @address (:len v))))))))


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


;; asm image

(defn reset-asm!
  []
  (reset! procedures {})
  (reset! variables {})
  (reset! variables-origin nil))

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
  (declare-vars!)
  (emit-procs-bytes!)
  (build-image))

(defn build-asm-image-file
  [filename image-type]
  (let [image (byte-array (build-asm-image image-type))]
    (with-open [out (io/output-stream (io/file filename))]
      (.write out image))))
