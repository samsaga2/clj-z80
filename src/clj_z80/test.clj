(ns clj-z80.test
  (:require [clj-z80.asm :refer :all :refer-macros :all]
            [clj-z80.msx.bios :as bios]
            [clojure.java.shell :refer [sh]]
            clj-z80.msx.image))

(defasmbyte :index)

(defasmproc print {:page 0}
  (label :loop
         [:ld :a [:hl]]
         [:or :a]
         [:ret :z]
         [:call bios/CHPUT]
         [:inc :hl]
         [:jr :loop]))

(defasmproc entry {:page 0 :include-always true}
  [:ld :a 0]
  [:call bios/CHGMOD]
  [:ld :hl :hello-world]
  [:call print]
  (label :loop
         [:ld :a 13]
         [:call bios/CHPUT]
         [:ld :a [:index]]
         [:inc :a]
         [:and 2r1111]
         [:ld [:index] :a]
         [:add (int \A)]
         [:call bios/CHPUT]
         [:jr :loop])
  (label :hello-world
         (db "Hello world!" 13 10)
         (db "by Victor M." 13 10)
         (db "<samsaga2@gmail.com>" 13 10 0)))

(defasmproc unused-proc {:page 0}
  [:ret])

(build-asm-image-file "test.rom" :msx-rom32k)
(sh "openmsx" "-carta" "test.rom")
