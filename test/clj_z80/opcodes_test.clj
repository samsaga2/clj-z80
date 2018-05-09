(ns clj-z80.opcodes-test
  (:require [clj-z80.opcodes :as o]
            [clojure.test :refer :all]))

(deftest assemble-instr-test
  (testing "nop"
    (is (= (o/assemble-instr [:nop])
           [0])))
  (testing "ld hl,0x1234"
    (is (= (o/assemble-instr [:ld :hl 0x1234])
           [0x21 0x34 0x12])))
  (testing "ld ix,0x1234"
    (is (= (o/assemble-instr [:ld :ix 0x1234])
           [0xdd 0x21 0x34 0x12])))
  (testing "ld (hl),a"
    (is (= (o/assemble-instr [:ld [:hl] :a])
           [0x77])))
  (testing "jp label"
    (is (= (o/assemble-instr [:jp :label])
           [0xc3 [:low-word :label] [:high-word :label]])))
  (testing "jr label"
    (is (= (o/assemble-instr [:jr :label])
           [0x18 [:displacement :label]])))
  (testing "ret"
    (is (= (o/assemble-instr [:ret])
           [0xc9])))
  (testing "db 0x12"
    (is (= (o/assemble-instr [:db 0x12])
           [0x12])))
  (testing "db 0x12,0x23,0x34"
    (is (= (o/assemble-instr [:db [0x12 0x23 0x34]])
           [0x12 0x23 0x34])))
  (testing "forbidden label"
    (is (thrown? Exception (o/assemble-instr [:jp :a])))))


(deftest assemble-instrs-test
  (testing "ok code"
    (is (= (o/assemble-instrs [[:nop]
                               [:ld :hl 0x1234]
                               [:jp :label]])
           [0x00
            0x21 0x34 0x12
            0xc3 [:low-word :label] [:high-word :label]])))
  (testing "invalid code"
    (is (thrown? Exception (o/assemble-instrs [[:nop]
                                               [:jp :a]])))))
