(ns clj-z80.test
  (:require [clj-z80.asm :refer :all :refer-macros :all]))

(defasmproc :entry {:page 0}
  [:ld :hl :hello-world]
  [:call :print]
  (label :loop
         [:jr :loop])
  (label :hello-world
         (db "Hello world!" 0)))

(defasmproc :print {:page 0}
  (label :loop
         [:ld :a [:hl]]
         [:or :a]
         [:ret :z]
         [:call 0xa2]
         [:inc :hl]
         [:jr :loop]))

(build-asm-image-file "test.rom" :msx-rom32k)
