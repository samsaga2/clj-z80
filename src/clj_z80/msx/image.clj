(ns clj-z80.msx.image
  (:require [clj-z80.asm-header :refer [setup-image-header! variables-origin]]
            [clj-z80.bytes :as b]
            [clj-z80.image :refer :all]
            [clj-z80.msx.lib.bios :as bios]
            [clj-z80.msx.lib.sysvars :as sysvars]
            [clj-z80.opcodes :refer :all]))


;; rom 16k

(defn- setup-msx-rom-header
  [entry-label]
  (let [rom-header [0x41 0x42
                    (b/lw entry-label)
                    (b/hw entry-label)
                    0 0 0 0 0 0]]
    (with-page 0
      (emit-bytes rom-header))))

(defmethod setup-image-header! :msx-rom16k
  [_]
  (reset! variables-origin 0xc000)
  (defpage 0 0x4000 0x2000 :code)
  (setup-msx-rom-header :entry))


;; rom 32k

(defn- emit-find-rom-page-2
  []
  (emit-bytes
   (assemble-instrs
    [[:call bios/RSLREG]
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
     [:call bios/ENASLT]
     [:jp :entry]])))

(defmethod setup-image-header! :msx-rom32k
  [_]
  (reset! variables-origin 0xc000)
  (defpage 0 0x4000 0x2000 :code)
  (defpage 1 0x6000 0x2000 :code)
  (setup-msx-rom-header :_start)
  (with-page 0
    (set-label! :_start)
    (emit-find-rom-page-2)))


;; rom konami5 scc 512kb

(defn set-konami5-page
  [slot-index page-index]
  (let [addr (case slot-index
               0 0x5000
               1 0x7000
               2 0x9000
               3 0xb000)]
    (if (= page-index :a)
      [[:ld [addr] :a]]
      [[:ld :a page-index]
       [:ld [addr] :a]])))

(defn- emit-find-konami5-pages
  []
  (emit-bytes
   (assemble-instrs
    (concat
     [[:call bios/RSLREG]
      [:rrca]
      [:rrca]
      [:and 0x03]
      [:ld :c :a]
      [:ld :b 0]
      [:ld :hl sysvars/EXPTBL]
      [:add :hl :bc]
      [:or [:hl]]
      [:ld :b :a]
      [:inc :hl]
      [:inc :hl]
      [:inc :hl]
      [:inc :hl]
      [:ld :a [:hl]]
      [:and 0x0c]
      [:or :b]
      [:ld :h 0x80]
      [:call bios/ENASLT]]
     (set-konami5-page 0 0)
     (set-konami5-page 1 1)
     (set-konami5-page 2 2)
     (set-konami5-page 3 3)
     [[:jp :entry]]))))

(defmethod setup-image-header! :msx-konami5
  [_]
  (reset! variables-origin 0xc000)
  (defpage 0 0x4000 0x2000 :code)
  (defpage 1 0x6000 0x2000 :code)
  (defpage 2 0x8000 0x2000 :code)
  (doseq [i (range 3 64)]
    (defpage i 0xa000 0x2000))
  (setup-msx-rom-header :_start)
  (with-page 0
    (set-label! :_start)
    (emit-find-konami5-pages)))
