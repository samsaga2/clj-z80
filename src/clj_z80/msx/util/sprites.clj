(ns clj-z80.msx.util.sprites
  (:require [mikera.image.core :refer :all]
            [clojure.string :as str]))

(defn convert-sprite-16x16
  [filename]
  (let [image   (load-image filename)
        w       (width image)
        h       (height image)
        convert (fn [x-offset]
                  (map (fn [y]
                         (let [row (map #(if (zero? (get-pixel image (+ % x-offset) y)) 0 1)
                                        (range 8))]
                           (BigInteger. (apply str row) 2)))
                       (range h)))]
    (assert (= w 16))
    (assert (= h 16))
    (concat (convert 0) (convert 8))))
