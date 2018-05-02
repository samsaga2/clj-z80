(ns clj-z80.bytes)


;; math

(defn byte-two-complement
  [n]
  (if (or (> n 255)
          (< n -127))
    (throw (Exception. (str "Overflow byte " n)))
    (if (neg? n)
      (inc (bit-xor 0xff (- n)))
      n)))

(defn word-two-complement
  [n]
  (if (or (> n 65535)
          (< n -32767))
    (throw (Exception. (str "Overflow word " n)))
    (if (neg?  n)
      (inc (bit-xor 0xffff (- n)))
      n)))

(defn lw
  [n]
  (cond (number? n)  (bit-and (word-two-complement n) 255)
        (keyword? n) [:low-word n]
        (fn? n)      [:low-word n]
        :else        (throw (Exception. (str "Word expected " n)))))

(defn hw
  [n]
  (cond (number? n)  (-> n
                         word-two-complement
                         (bit-shift-right 8)
                         (bit-and 255))
        (keyword? n) [:high-word n]
        (fn? n)      [:high-word n]
        :else        (throw (Exception. (str "Word expected " n)))))

(defn b
  [n]
  (cond (number? n)  (byte-two-complement n)
        (keyword? n) [:byte n]
        (fn? n)      [:byte n]
        (and (coll? n)
             (= (count n) 2)
             (contains? #{:low-word :high-word :displacement} (first n)))
        n
        :else        (throw (Exception. (str "Byte expected " n)))))

(defn e
  [n]
  (cond (number? n)  (byte-two-complement n)
        (keyword? n) [:displacement n]
        (fn? n)      [:displacement n]
        :else        (throw (Exception. (str "Byte expected " n)))))


;; checks

(defn byte-label? [b]
  (and (vector? b) (= (first b) :byte)))

(defn low-word-label? [b]
  (and (vector? b) (= (first b) :low-word)))

(defn high-word-label? [b]
  (and (vector? b) (= (first b) :high-word)))

(defn displacement-label? [b]
  (and (vector? b) (= (first b) :displacement)))
