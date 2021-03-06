(ns clj-z80.msx.examples.sprite-skeleton
  (:require [clj-z80.asm :refer :all :refer-macros :all]
            [clj-z80.msx.lib.bios :as bios]
            [clj-z80.msx.lib.sysvars :as sysvars]
            [clj-z80.msx.lib.keys :as keys]
            [clj-z80.msx.lib.sprites :as spr]
            [clj-z80.msx.util.graphics :refer [convert-sprite-16x16]]
            [clojure.java.shell :refer [sh]]
            clj-z80.msx.image))

(let [sprite (convert-sprite-16x16 "resources/sprites/skeleton.png")]
  (assert (= (count (:colors sprite)) 1))
  (defasmproc skeleton {:page 1}
    (apply db (first (:patterns sprite))))
  (def skeleton-color (first (:colors sprite))))

(defasmproc init {}
  ;; screen 2,2
  [:xor :a]
  [:ld [sysvars/FORCLR] :a]
  [:ld [sysvars/BAKCLR] :a]
  [:ld [sysvars/BDRCLR] :a]
  [:ld :a 2]
  [:call bios/CHGMOD]
  [:call spr/enable-sprites-16]

  ;; load sprite pattern
  [:xor :a]
  [:ld :hl skeleton]
  [:call spr/write-pattern]

  ;; setup sprite attribute
  [:call spr/clear-attributes]
  (spr/ld-ix-attributes 0)
  (spr/write-ix-attributes :y 20 :x 20 :pattern 0 :color skeleton-color)
  [:jp spr/write-attributes])

(defasmproc move {}
  (keys/ld-a-key-row "RIGHT")
  (keys/bit-key-down "RIGHT") [:call :z :right]
  (keys/bit-key-down "LEFT")  [:call :z :left]
  (keys/bit-key-down "UP")    [:call :z :up]
  (keys/bit-key-down "DOWN")  [:call :z :down]
  [:ret]
  (label :right [:inc [:ix spr/+attribute-x+]] [:ret])
  (label :left  [:dec [:ix spr/+attribute-x+]] [:ret])
  (label :up    [:dec [:ix spr/+attribute-y+]] [:ret])
  (label :down  [:inc [:ix spr/+attribute-y+]] [:ret]))

(defasmproc main-loop {}
  (label :loop
         [:call move]
         [:call spr/write-attributes]
         [:halt]
         [:jr :loop]))

(defasmproc entry {:page 0 :include-always true :label :entry}
  [:call init]
  [:jp main-loop])

(defn -main
  [& args]
  (build-asm-image-file "test.rom" :msx-rom32k)
  (sh "openmsx" "-carta" "test.rom"))
