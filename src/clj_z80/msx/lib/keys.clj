(ns clj-z80.msx.lib.keys
  (:require [clj-z80.msx.lib.sysvars :as sysvars]
            [clj-z80.asm :refer :all]))

;; MSX international keyboard
;; http://map.grauw.nl/articles/keymatrix.php
(def key-codes
  (let [b7 (int (Math/pow 2 7))
        b6 (int (Math/pow 2 6))
        b5 (int (Math/pow 2 5))
        b4 (int (Math/pow 2 4))
        b3 (int (Math/pow 2 3))
        b2 (int (Math/pow 2 2))
        b1 (int (Math/pow 2 1))
        b0 (int (Math/pow 2 0))]
    {"7"      {:row 0 :col 7 :bit b7}
     "6"      {:row 0 :col 6 :bit b6}
     "5"      {:row 0 :col 5 :bit b5}
     "4"      {:row 0 :col 4 :bit b4}
     "3"      {:row 0 :col 3 :bit b3}
     "2"      {:row 0 :col 2 :bit b2}
     "1"      {:row 0 :col 1 :bit b1}
     "0"      {:row 0 :col 0 :bit b0}
     ";"      {:row 1 :col 7 :bit b7}
     "]"      {:row 1 :col 6 :bit b6}
     "["      {:row 1 :col 5 :bit b5}
     "\\"     {:row 1 :col 4 :bit b4}
     "="      {:row 1 :col 3 :bit b3}
     "-"      {:row 1 :col 2 :bit b2}
     "9"      {:row 1 :col 1 :bit b1}
     "8"      {:row 1 :col 0 :bit b0}
     "B"      {:row 2 :col 7 :bit b7}
     "A"      {:row 2 :col 6 :bit b6}
     "DEAD"   {:row 2 :col 5 :bit b5}
     "/"      {:row 2 :col 4 :bit b4}
     "."      {:row 2 :col 3 :bit b3}
     ","      {:row 2 :col 2 :bit b2}
     "`"      {:row 2 :col 1 :bit b1}
     "'"      {:row 2 :col 0 :bit b0}
     "J"      {:row 3 :col 7 :bit b7}
     "I"      {:row 3 :col 6 :bit b6}
     "H"      {:row 3 :col 5 :bit b5}
     "G"      {:row 3 :col 4 :bit b4}
     "F"      {:row 3 :col 3 :bit b3}
     "E"      {:row 3 :col 2 :bit b2}
     "D"      {:row 3 :col 1 :bit b1}
     "C"      {:row 3 :col 0 :bit b0}
     "R"      {:row 4 :col 7 :bit b7}
     "Q"      {:row 4 :col 6 :bit b6}
     "P"      {:row 4 :col 5 :bit b5}
     "O"      {:row 4 :col 4 :bit b4}
     "N"      {:row 4 :col 3 :bit b3}
     "M"      {:row 4 :col 2 :bit b2}
     "L"      {:row 4 :col 1 :bit b1}
     "K"      {:row 4 :col 0 :bit b0}
     "Z"      {:row 5 :col 7 :bit b7}
     "Y"      {:row 5 :col 6 :bit b6}
     "X"      {:row 5 :col 5 :bit b5}
     "W"      {:row 5 :col 4 :bit b4}
     "V"      {:row 5 :col 3 :bit b3}
     "U"      {:row 5 :col 2 :bit b2}
     "T"      {:row 5 :col 1 :bit b1}
     "S"      {:row 5 :col 0 :bit b0}
     "F3"     {:row 6 :col 7 :bit b7}
     "F2"     {:row 6 :col 6 :bit b6}
     "F1"     {:row 6 :col 5 :bit b5}
     "CODE"   {:row 6 :col 4 :bit b4}
     "CAPS"   {:row 6 :col 3 :bit b3}
     "GRAPH"  {:row 6 :col 2 :bit b2}
     "CTRL"   {:row 6 :col 1 :bit b1}
     "SHIFT"  {:row 6 :col 0 :bit b0}
     "RET"    {:row 7 :col 7 :bit b7}
     "SELECT" {:row 7 :col 6 :bit b6}
     "BS"     {:row 7 :col 5 :bit b5}
     "STOP"   {:row 7 :col 4 :bit b4}
     "TAB"    {:row 7 :col 3 :bit b3}
     "ESC"    {:row 7 :col 2 :bit b2}
     "F5"     {:row 7 :col 1 :bit b1}
     "F4"     {:row 7 :col 0 :bit b0}
     "RIGHT"  {:row 8 :col 7 :bit b7}
     "DOWN"   {:row 8 :col 6 :bit b6}
     "UP"     {:row 8 :col 5 :bit b5}
     "LEFT"   {:row 8 :col 4 :bit b4}
     "DEL"    {:row 8 :col 3 :bit b3}
     "INS"    {:row 8 :col 2 :bit b2}
     "HOME"   {:row 8 :col 1 :bit b1}
     "SPACE"  {:row 8 :col 0 :bit b0}
     "NUM4"   {:row 9 :col 7 :bit b7}
     "NUM3"   {:row 9 :col 6 :bit b6}
     "NUM2"   {:row 9 :col 5 :bit b5}
     "NUM1"   {:row 9 :col 4 :bit b4}
     "NUM0"   {:row 9 :col 3 :bit b3}
     "NUM/"   {:row 9 :col 2 :bit b2}
     "NUM+"   {:row 9 :col 1 :bit b1}
     "NUM*"   {:row 9 :col 0 :bit b0}
     "NUM."   {:row 10 :col 7 :bit b7}
     "NUM,"   {:row 10 :col 6 :bit b6}
     "NUM-"   {:row 10 :col 5 :bit b5}
     "NUM9"   {:row 10 :col 4 :bit b4}
     "NUM8"   {:row 10 :col 3 :bit b3}
     "NUM7"   {:row 10 :col 2 :bit b2}
     "NUM6"   {:row 10 :col 1 :bit b1}
     "NUM5"   {:row 10 :col 0 :bit b0}}))

(defn ld-a-key-row
  [keyname]
  (if-let [{:keys [row _]} (get key-codes keyname)]
    [:ld :a [(+ sysvars/NEWKEY row)]]
    (throw (Exception. (str "Unknown key " keyname)))))

(defn bit-key-down
  [keyname]
  (if-let [{:keys [_ col]} (get key-codes keyname)]
    [:bit col :a]
    (throw (Exception. (str "Unknown key " keyname)))))

(defn key-down?
  [keyname]
  [(ld-a-key-row keyname)
   (bit-key-down keyname)])

;; key pressed

(defasmvar new-keys 11)
(defasmvar old-keys 11)

(defasmproc init-keys {:page :code}
  [:ld :hl old-keys]
  [:ld [:hl] 0]
  [:ld :de old-keys]
  [:inc :de]
  [:ld :bc 10]
  [:ldir]
  [:ret])

(defasmproc update-keys {:page :code}
  [:ld :hl new-keys]
  [:ld :de old-keys]
  [:ld :bc 11]
  [:ldir]

  [:ld :hl sysvars/NEWKEY]
  [:ld :de new-keys]
  [:ld :bc 11]
  [:ldir]
  [:ret])

(defasmproc key-pressed? {:page :code}
  ;; e = key line
  ;; c = key bit
  [:ld :hl new-keys]
  [:ld :d 0]
  [:add :hl :de]
  [:ld :a [:hl]]
  [:and :c]
  [:ret :nz]

  [:ld :hl old-keys]
  [:ld :d 0]
  [:add :hl :de]
  [:ld :a [:hl]]
  [:cpl]
  [:and :c]
	[:ret])
