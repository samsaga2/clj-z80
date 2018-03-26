(ns clj-z80.test
  (:require [clj-z80.asm :refer :all :refer-macros :all]
            [clj-z80.msx-bios :as bios]))

(defasmproc :entry {:page 0}
  [:ld :a 0]
  [:call bios/CHGMOD]
  [:ld :hl :hello-world]
  [:call :print]
  (label :loop
         [:jr :loop])
  (label :hello-world
         (db "Hello world!" 13 10)
         (db "by Victor M." 13 10)
         (db "<samsaga2@gmail.com>" 0)))

(defasmproc :print {:page 0}
  (label :loop
         [:ld :a [:hl]]
         [:or :a]
         [:ret :z]
         [:call bios/CHPUT]
         [:inc :hl]
         [:jr :loop]))

(build-asm-image-file "test.rom" :msx-rom32k)
