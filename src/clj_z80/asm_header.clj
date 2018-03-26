(ns clj-z80.asm-header)

(def variables-origin (atom nil))

(defmulti setup-image-header! (fn [image-type] image-type))
