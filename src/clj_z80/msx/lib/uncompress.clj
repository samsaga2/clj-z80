(ns clj-z80.msx.lib.uncompress
  (:require [clj-z80.asm :refer :all]))

(defasmvar buffer 512)

(defasmproc uncompress-lz77-to-vram {:page :code}
  ;; bc=token count
  [:ld :c [:hl]]
  [:inc :hl]
  [:ld :b [:hl]]
  [:inc :hl]

  ;; de=dest (256 bytes align)
  [:push :hl]
  [:ld :hl buffer]
  [:ld :de 256]
  [:add :hl :de]
  [:ld :l 0]
  [:ex :de :hl]
  [:pop :hl]

  ;; uncompress loop
  (label :loop
         ;; a=len
         [:ld :a [:hl]]
         [:inc :hl]
         [:or :a]
         [:jp :z :next-byte]

         [:push :bc]
         [:ld :b :a]                  ; b=back reference len
         [:ld :c [:hl]]               ; c=back reference offset
         [:inc :hl]
         [:push :hl]
         ;; hl=offset=(de-backoffset) mod 256
         [:ld :a :e]
         [:sub :c]
         [:ld :l :a]
         [:ld :h :d]

         ;; copy back reference
         (label :loop-back-reference
                [:ld :a [:hl]]
                [:inc :l]                   ; hl=hl mod 256
                [:ld [:de] :a]
                [:inc :e]                   ; de=de mod 256
                [:out [0x98] :a]
                [:djnz :loop-back-reference])
         [:pop :hl]
         [:pop :bc]

         (label :next-byte
                ;; a=next char
                [:ld :a [:hl]]
                [:inc :hl]

                ;; write nextchar
                [:out [0x98] :a]
                [:ld [:de] :a]
                [:inc :e])

         ;; end?
         [:dec :bc]
         [:ld :a :c]
         [:or :b]
         [:jp :nz :loop])
  [:ret])
