(ns clj-z80.msx.util.compress
  (:require [clj-z80.bytes :refer [hw lw]]))

(defn- get-max-length
  "get the max equal length between left&right bytes"
  [data left right]
  (loop [left   left
         right  right
         length 0]
    (if (and (< left right)
             (< right (count data))
             (= (nth data left) (nth data right))
             (< length 255))
      (recur (inc left) (inc right) (inc length))
      length)))

(defn- find-back-offset
  "get the max length of bytes between right-255 & right"
  [data right]
  (loop [max-length 0
         max-offset 0
         left       (Math/max 0 (- right 255))]
    (if (< left right)
      (let [length (get-max-length data left right)]
        (if (> length max-length)
          (recur length (- right left) (inc left))
          (recur max-length max-offset (inc left))))
      [max-length max-offset])))

(defn- compress-values
  [data]
  (loop [blocks []
         i      0]
    (if (< i (count data))
      (let [[back-len back-offset] (find-back-offset data i)
            i                      (+ i back-len)
            next-char              (nth data i 0)
            block                  (if (zero? back-len)
                                     [back-len next-char]
                                     [back-len back-offset next-char])
            blocks                 (doall (conj blocks block))]
        (recur blocks (inc i)))
      blocks)))

(defn compress-lz77
  "compress col of bytes using lz77 algorithm"
  [data]
  (let [data   (int-array data)
        blocks (compress-values data)
        size   (count blocks)
        header [(lw size) (hw size)]]
    (apply concat header blocks)))
