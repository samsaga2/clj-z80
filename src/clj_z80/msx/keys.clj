(ns clj-z80.msx.keys
  (:require [clj-z80.msx.sysvars :as sysvars]))

;; MSX international keyboard
;; http://map.grauw.nl/articles/keymatrix.php
(def keys
  {"7"      {:row 0 :col 7}
   "6"      {:row 0 :col 6}
   "5"      {:row 0 :col 5}
   "4"      {:row 0 :col 4}
   "3"      {:row 0 :col 3}
   "2"      {:row 0 :col 2}
   "1"      {:row 0 :col 1}
   "0"      {:row 0 :col 0}
   ";"      {:row 1 :col 7}
   "]"      {:row 1 :col 6}
   "["      {:row 1 :col 5}
   "\\"     {:row 1 :col 4}
   "="      {:row 1 :col 3}
   "-"      {:row 1 :col 2}
   "9"      {:row 1 :col 1}
   "8"      {:row 1 :col 0}
   "B"      {:row 2 :col 7}
   "A"      {:row 2 :col 6}
   "DEAD"   {:row 2 :col 5}
   "/"      {:row 2 :col 4}
   "."      {:row 2 :col 3}
   ","      {:row 2 :col 2}
   "`"      {:row 2 :col 1}
   "'"      {:row 2 :col 0}
   "J"      {:row 3 :col 7}
   "I"      {:row 3 :col 6}
   "H"      {:row 3 :col 5}
   "G"      {:row 3 :col 4}
   "F"      {:row 3 :col 3}
   "E"      {:row 3 :col 2}
   "D"      {:row 3 :col 1}
   "C"      {:row 3 :col 0}
   "R"      {:row 4 :col 7}
   "Q"      {:row 4 :col 6}
   "P"      {:row 4 :col 5}
   "O"      {:row 4 :col 4}
   "N"      {:row 4 :col 3}
   "M"      {:row 4 :col 2}
   "L"      {:row 4 :col 1}
   "K"      {:row 4 :col 0}
   "Z"      {:row 5 :col 7}
   "Y"      {:row 5 :col 6}
   "X"      {:row 5 :col 5}
   "W"      {:row 5 :col 4}
   "V"      {:row 5 :col 3}
   "U"      {:row 5 :col 2}
   "T"      {:row 5 :col 1}
   "S"      {:row 5 :col 0}
   "F3"     {:row 6 :col 7}
   "F2"     {:row 6 :col 6}
   "F1"     {:row 6 :col 5}
   "CODE"   {:row 6 :col 4}
   "CAPS"   {:row 6 :col 3}
   "GRAPH"  {:row 6 :col 2}
   "CTRL"   {:row 6 :col 1}
   "SHIFT"  {:row 6 :col 0}
   "RET"    {:row 7 :col 7}
   "SELECT" {:row 7 :col 6}
   "BS"     {:row 7 :col 5}
   "STOP"   {:row 7 :col 4}
   "TAB"    {:row 7 :col 3}
   "ESC"    {:row 7 :col 2}
   "F5"     {:row 7 :col 1}
   "F4"     {:row 7 :col 0}
   "RIGHT"  {:row 8 :col 7}
   "DOWN"   {:row 8 :col 6}
   "UP"     {:row 8 :col 5}
   "LEFT"   {:row 8 :col 4}
   "DEL"    {:row 8 :col 3}
   "INS"    {:row 8 :col 2}
   "HOME"   {:row 8 :col 1}
   "SPACE"  {:row 8 :col 0}
   "NUM4"   {:row 9 :col 7}
   "NUM3"   {:row 9 :col 6}
   "NUM2"   {:row 9 :col 5}
   "NUM1"   {:row 9 :col 4}
   "NUM0"   {:row 9 :col 3}
   "NUM/"   {:row 9 :col 2}
   "NUM+"   {:row 9 :col 1}
   "NUM*"   {:row 9 :col 0}
   "NUM."   {:row 10 :col 7}
   "NUM,"   {:row 10 :col 6}
   "NUM-"   {:row 10 :col 5}
   "NUM9"   {:row 10 :col 4}
   "NUM8"   {:row 10 :col 3}
   "NUM7"   {:row 10 :col 2}
   "NUM6"   {:row 10 :col 1}
   "NUM5"   {:row 10 :col 0}})

(defn ld-a-key-row
  [keyname]
  (if-let [{:keys [row _]} (get keys keyname)]
    [:ld :a [(+ sysvars/NEWKEY row)]]
    (throw (Exception. "Unknown key"))))

(defn bit-key-pressed
  [keyname]
  (if-let [{:keys [_ col]} (get keys keyname)]
    [:bit col :a]
    (throw (Exception. "Unknown key"))))

(defn key-pressed?
  [keyname]
  [(ld-a-key-row keyname)
   (bit-key-pressed keyname)])
