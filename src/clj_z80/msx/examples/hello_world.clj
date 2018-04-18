(ns clj-z80.msx.examples.hello-world
  (:require [clj-z80.asm :refer :all :refer-macros :all]
            [clj-z80.msx.lib.bios :as bios]
            [clojure.java.shell :refer [sh]]
            clj-z80.msx.image))

(defasmbyte index)

(defasmproc print {:page :code}
  (label :loop
         [:ld :a [:hl]]
         [:or :a]
         [:ret :z]
         [:call bios/CHPUT]
         [:inc :hl]
         [:jr :loop]))

(defasmproc hello-world {}
  [:ld :hl :text]
  [:jp print]
  (label :text
         (db "Hello world!" 13 10)
         (db "by Victor M." 13 10)
         (db "<samsaga2@gmail.com>" 13 10 0)))

(defasmproc char-loop {}
  (label :loop
         [:ld :a 13]
         [:call bios/CHPUT]
         [:ld :a [index]]
         [:inc :a]
         [:and 2r1111]
         [:ld [index] :a]
         [:add (int \A)]
         [:call bios/CHPUT]
         [:jr :loop]))

(defasmproc entry {:page 0 :include-always true :label :entry}
  [:xor :a]
  [:call bios/CHGMOD]
  [:call hello-world]
  [:jp char-loop])

(defn -main
  [& args]
  (build-asm-image-file "test.rom" :msx-rom32k)
  (sh "openmsx" "-carta" "test.rom"))
