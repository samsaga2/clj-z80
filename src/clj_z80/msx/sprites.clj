(ns clj-z80.msx.sprites
  (:require [clj-z80.asm :refer :all]
            [clj-z80.msx.bios :as bios]))

(def +attribute-y+ 0)
(def +attribute-x+ 1)
(def +attribute-pattern+ 2)
(def +attribute-color+ 3)

(defasmvar spr-attributes (* 32 4))

(defasmproc enable-sprites-16 {:page :code}
  [:ld :b 98]
  [:ld :c 1]
  [:jp bios/WRTVDP])

(defasmproc clear-attributes {:page :code}
  [:ld :hl spr-attributes]
  [:ld :bc 32]
  [:ld :de 4]
  (label :loop
         [:ld [:hl] 212]
         [:add :hl :de]
         [:djnz :loop])
  [:ret])

(defasmproc write-attributes {:page :code}
  [:xor :a]
  [:call bios/CALATR]
  [:ex :de :hl]
  [:ld :bc (* 32 4)]
  [:ld :hl spr-attributes]
  [:jp bios/LDIRVM])

(defasmproc write-pattern {:page :code}
  ;; Input : A - Sprite number
  ;;         HL - Address pattern
  [:push :hl]
  [:call bios/CALPAT]
  [:ex :de :hl]
  [:ld :bc 32]
  [:pop :hl]
  [:jp bios/LDIRVM])

(defn ld-ix-attributes
  [sprite-index]
  (if (zero? sprite-index)
    [:ld :ix spr-attributes]
    [[:ld :ix spr-attributes]
     [:ld :de (+ sprite-index 4)]
     [:add :ix :de]]))

(defn write-ix-attributes
  [& {:keys [y x pattern color]}]
  [(when y [:ld [:ix +attribute-y+] y])
   (when x [:ld [:ix +attribute-x+] x])
   (when pattern [:ld [:ix +attribute-pattern+] pattern])
   (when color [:ld [:ix +attribute-color+] color])])
