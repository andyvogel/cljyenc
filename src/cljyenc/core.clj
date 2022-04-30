(ns cljyenc.core
  (:require [clojure.string :as string]))

;; Everything in this file is low level. It should never be called by
;; the user.

(defn yenc-is-critical? [b]
  (or (= b 0)
      (= b 9)
      (= b 10)
      (= b 13)
      (= b 61)))

(defn yenc-find-prologue [char-seq]
  (loop [residue char-seq
         verify-header-mode false]
    (cond (empty? residue) nil
          verify-header-mode (let [possible-header (apply str (take 6 residue))]
                               (if (= possible-header "ybegin")
                                 (drop 6 residue)
                                 (recur (drop 6 residue) false)))
          (= (first residue) \=) (recur (rest residue) true))))


(defn yenc-find-epilogue [char-seq]
  (loop [residue char-seq
         verify-header-mode false]
    (cond (empty? residue) nil
          verify-header-mode (let [possible-header (apply str (take 6 residue))]
                               (if (= possible-header "ybegin")
                                 true (drop 6 residue)))
          (= (first residue) \=) (recur (rest residue) true))))

(defn yenc-encode-byte-to-character [b]
  (let [shifted-character (mod (+ (Byte/toUnsignedInt (byte b)) 42) 256)]
    (if (yenc-is-critical? shifted-character)
      (str \= (char (mod (+ shifted-character 64) 256)))
      (char shifted-character))))

(defn yenc-encode-byte-to-character-seq [byte-seq]
  (seq (apply str (for [b byte-seq] (yenc-encode-byte-to-character b)))))

(defn yenc-decode-character-to-byte-seq [character-seq]
  (loop [remain character-seq
         char-accum (transient [])
         critical false]
    (let [c (first remain)]
      (cond (empty? remain) (persistent! char-accum)
            critical (recur (rest remain) (conj! char-accum (.byteValue (mod (- (int c) 42 64) 256))) false)
            (= c \=) (recur (rest remain) char-accum true)
            :else (recur (rest remain) (conj! char-accum (.byteValue (mod (- (int c) 42) 256))) false)))))

(defn yenc-encode-byte-seq-to-string [bseq]
  (apply str (yenc-encode-byte-to-character-seq bseq)))

(defn yenc-decode-string-to-byte-seq [s]
  (yenc-decode-character-to-byte-seq (seq s)))