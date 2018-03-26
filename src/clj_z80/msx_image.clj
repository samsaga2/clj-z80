(ns clj-z80.msx-image
  (:require [clj-z80.asm-header :refer [setup-image-header! variables-origin]]
            [clj-z80.bytes :as b]
            [clj-z80.image :refer :all]
            [clj-z80.opcodes :refer :all]))

(defn- setup-msx-rom-header
  [entry-label]
  (let [rom-header [0x41 0x42 (b/lw entry-label) (b/hw entry-label) 0 0 0 0 0 0]]
    (with-page 0
      (emit-bytes rom-header))))

(defmethod setup-image-header! :msx-rom16k
  [_]
  (reset! variables-origin 0xc000)
  (defpage 0 0x4000 0x2000)
  (setup-msx-rom-header :entry))


(defn- emit-find-rom-page-2
  []
  (let [RSLREG 0x138
        ENASLT 0x24]
    (emit-bytes
     (assemble-instrs
      [[:call RSLREG]
       [:rrca]
       [:rrca]
       [:and 3]
       [:ld :c :a]
       [:add 0xc1]
       [:ld :l :a]
       [:ld :h 0xfc]
       [:ld :a [:hl]]
       [:and 0x80]
       [:or :c]
       [:ld :c :a]
       [:inc :l]
       [:inc :l]
       [:inc :l]
       [:inc :l]
       [:ld :a [:hl]]
       [:and 0x0c]
       [:or :c]
       [:ld :h 0x80]
       [:call ENASLT]
       [:jp :entry]]))))

(defmethod setup-image-header! :msx-rom32k
  [_]
  (reset! variables-origin 0xc000)
  (defpage 0 0x4000 0x2000)
  (defpage 1 0x6000 0x2000)
  (setup-msx-rom-header :_start)
  (with-page 0
    (set-label! :_start)
    (emit-find-rom-page-2)))
