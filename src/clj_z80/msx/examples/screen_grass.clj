(ns clj-z80.msx.examples.screen-grass
  (:require [clj-z80.asm :refer :all :refer-macros :all]
            [clj-z80.msx.lib.bios :as bios]
            [clj-z80.msx.lib.sysvars :as sysvars]
            [clj-z80.msx.lib.uncompress :refer [uncompress-lz77-to-vram]]
            [clj-z80.msx.util.graphics :refer [convert-screen2]]
            [clj-z80.msx.util.compress :refer [compress-lz77]]
            [clojure.java.shell :refer [sh]]
            clj-z80.msx.image))

(let [[colors patterns] (convert-screen2 "resources/screens/grass.png" :colors)
      patterns          (compress-lz77 patterns)
      colors            (compress-lz77 colors)]
  (defasmproc screen-patterns {:page 1}
    [:db patterns])
  (defasmproc screen-colors {:page 1}
    [:db colors]))

(defasmproc init {}
  ;; screen 2,2
  [:xor :a]
  [:ld [sysvars/FORCLR] :a]
  [:ld [sysvars/BAKCLR] :a]
  [:ld [sysvars/BDRCLR] :a]
  [:ld :a 2]
  [:jp bios/CHGMOD])

(defasmproc load-screen {}
  [:di]
  ;; uncompress tile patterns
  [:ld :hl 0]
  [:call bios/SETWRT]
  [:ld :hl screen-patterns]
  [:call uncompress-lz77-to-vram]
  ;; uncompress tile colors
  [:ld :hl 0x2000]
  [:call bios/SETWRT]
  [:ld :hl screen-colors]
  [:call uncompress-lz77-to-vram]
  [:ei]
  [:ret])

(defasmproc entry {:page 0 :include-always true :label :entry}
  [:call init]
  [:call load-screen]
  (label :loop [:jr :loop]))

(defn -main
  [& args]
  (build-asm-image-file "test.rom" :msx-rom32k)
  (sh "openmsx" "-carta" "test.rom"))
