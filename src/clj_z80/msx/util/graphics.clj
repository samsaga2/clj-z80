(ns clj-z80.msx.util.graphics
  (:require [clojure.string :as str]
            [mikera.image.core :refer :all]))


;; colors

(def msx1-colors
  (mapv (fn [[r g b]] (java.awt.Color. r g b 255))
        [[0 0 0]
         [0 0 0]
         [0 241 20]
         [68 249 86]
         [85 79 255]
         [128 111 255]
         [250 80 51]
         [12 255 255]
         [255 81 52]
         [255 115 86]
         [226 210 4]
         [242 217 71]
         [4 212 19]
         [231 80 229]
         [208 208 208]
         [255 255 255]]))

(defn- color-distance
  [c1 c2]
  (let [r1 (.getRed c1)
        r2 (.getRed c2)
        b1 (.getBlue c1)
        b2 (.getBlue c2)
        g1 (.getGreen c1)
        g2 (.getGreen c2)]
    (Math/sqrt (+ (Math/pow (- r1 r2) 2)
                  (Math/pow (- g1 g2) 2)
                  (Math/pow (- b1 b2) 2)))))

(defn get-msx1-color-index
  [color]
  (->> msx1-colors
       (map-indexed (fn [i c] [i (color-distance color c)]))
       (sort-by second)
       first
       first))

(get-msx1-color-index (java.awt.Color. 84 85 237))


;; image

(defn- split-image-into-tiles
  [image color-conversion]
  (let [w        (width image)
        h        (height image)
        get-tile (fn [start-x start-y]
                   (for [y (range 8)]
                     (for [x (range 8)]
                       (let [pixel (.getRGB image (int (+ start-x x)) (int (+ start-y y)))]
                         (color-conversion pixel)))))]
    (assert (= w 256))
    (assert (= h 192))
    (for [y (range 24)]
      (for [x (range 32)]
        (get-tile (* x 8) (* y 8))))))

(defn- extract-row-screen2-colors
  [row]
  (let [colors (into {} (map-indexed (fn [i c] [c i]) (sort (distinct row))))]
    (when (> (count (keys colors)) 2)
      (throw (Exception. "Too many colors")))
    colors))

(defn- convert-row-into-screen2-colors
  [row]
  (let [colors (-> row extract-row-screen2-colors keys vec)]
    (bit-or (bit-shift-left (get colors 1 0) 4)
            (get colors 0 0))))

(defn- convert-row-into-screen2-patterns
  [row]
  (let [colors (extract-row-screen2-colors row)]
    (BigInteger. (str/join (map #(str (get colors %)) row)) 2)))

(defn- extract-image-msx1-colors
  [image]
  (->> image get-pixels distinct
       (map (fn [c] [c (get-msx1-color-index (java.awt.Color. c))]))
       (into {})))

(defn convert-screen2
  [filename type]
  (let [image            (load-image filename)
        image-colors     (extract-image-msx1-colors image)
        color-conversion #(get image-colors % 0)
        tiles            (split-image-into-tiles image color-conversion)
        image-colors     (flatten
                          (map (fn [y]
                                 (map (fn [x]
                                        (map convert-row-into-screen2-colors x))
                                      y))
                               tiles))
        image-patterns   (flatten
                          (map (fn [y]
                                 (map (fn [x]
                                        (map convert-row-into-screen2-patterns x))
                                      y))
                               tiles))]
    [image-colors
     image-patterns]))
