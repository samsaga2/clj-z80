(ns clj-z80.asm-header)

(defmulti setup-image-header! (fn [image-type] image-type))
