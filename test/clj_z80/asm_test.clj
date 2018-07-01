(ns clj-z80.asm-test
  (:require [clj-z80.asm :as a]
            [clj-z80.image :as i]
            [clj-z80.asm-header :as h]
            [clojure.test :refer :all]))

(defmethod h/setup-image-header! :small-raw
  [_]
  (reset! h/variables-origin 0xf000)
  (i/defpage 0 0x1000 16 :code))

(defn- with-resource
  [f expected]
  (a/reset-asm!)
  (f)
  (is (= (a/build-asm-image :small-raw)
         (->> (concat expected (repeat 16 0))
              (take 16)
              vec))))

(deftest asm-test
  (testing "short proc"
    (with-resource
      #(a/make-proc :short-proc 0
                    [[:nop]
                     [:ld :hl 0x1234]
                     [:ret]])
      [0x00
       0x21 0x34 0x12
       0xc9]))
  (testing "jp label proc"
    (with-resource
      #(a/make-proc :short-proc 0
                    [[:jp :label]
                     [:local-label :label]
                     [:nop]
                     [:ret]])
      [0xc3 0x03 0x10
       0x00
       0xc9]))
  (testing "jr label proc"
    (with-resource
      #(a/make-proc :short-proc 0
                    [[:jr :label]
                     [:local-label :label]
                     [:nop]
                     [:ret]])
      [0x18 0x00
       0x00
       0xc9]))
  (testing "jr forward label proc"
    (with-resource
      #(a/make-proc :short-proc 0
                    [[:jr :label]
                     [:xor :a]
                     [:local-label :label]
                     [:nop]
                     [:ret]])
      [0x18 0x01
       0xaf
       0x00
       0xc9]))
  (testing "jr backward label proc"
    (with-resource
      #(a/make-proc :short-proc 0
                    [[:local-label :label]
                     [:xor :a]
                     [:jr :label]])
      [0xaf
       0x18 0xfd]))
  (testing "calculated label"
    (with-resource
      #(do (a/make-proc :short-proc 0
                        [[:ld :hl (fn [] (:address (i/get-label :short-proc2)))]])
           (a/make-proc :short-proc2 0
                        [[:ret]]))
      [0x21 0x03 0x10 0xc9]))
  (testing "db 1"
    (with-resource
      #(a/make-proc :short-proc2 0
                    [(a/db 1)
                     [:ret]])
      [0x01 0xc9]))
  (testing "db 1, 2, 3"
    (with-resource
      #(a/make-proc :short-proc2 0
                    [(a/db 1 2 3)
                     [:ret]])
      [0x01 0x02 0x03 0xc9]))
  (testing "dw 0x1234"
    (with-resource
      #(a/make-proc :short-proc2 0
                    [(a/dw 0x1234)
                     [:ret]])
      [0x34 0x12 0xc9]))
  (testing "dw 0x1234, 0x2345"
    (with-resource
      #(a/make-proc :short-proc2 0
                    [(a/dw 0x1234 0x2345)
                     [:ret]])
      [0x34 0x12 0x45 0x23 0xc9])))
