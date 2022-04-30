(ns cljyenc.core-test
  (:require [clojure.test :refer :all]
            [cljyenc.core :refer :all]
            [cljyenc.encode :as yenc]
            [cljyenc.decode :as ydec])
  (:import [java.util.random RandomGenerator RandomGeneratorFactory]))

(def test-global-rng-factory (RandomGeneratorFactory/of "L128X1024MixRandom"))
(def test-global-rng (.create test-global-rng-factory))
(def max-string-size 200000)
(def max-file-size 200000)
(def number-of-tests 10000)

(defn yenc-decode-matching-encode? [headers-input headers-output]
  (let [local-rng (.create test-global-rng-factory)
        random-bytes (byte-array max-string-size)]
    (.nextBytes local-rng random-bytes)
    (every? true? (map #(= %1 %2) (seq random-bytes) (ydec/string->bytes (yenc/bytes->string (seq random-bytes) headers-output) headers-input)))))

;; Test to make sure YEnc input = decoded output
(deftest test-yenc-decode-encode
  (testing "Testing YEnc: Every encoded and decoded byte must match the original byte..."
    (is (and
         ;; Should decode fine with or without =ybegin and =yend
         (yenc-decode-matching-encode? true true)
         (yenc-decode-matching-encode? false false)
         ;; Should decode fine for the decode to ignore =ybegin and =yend
         (yenc-decode-matching-encode? false true)))))

;; Test for exception if we don't get a header when we expect one.
(deftest test-yenc-decode-encode-fail-no-header
  (testing "Testing YEnc: Should fail if input doesn't have a header when expecting it..."
    (is (try (yenc-decode-matching-encode? true false) false (catch Exception _ true)))))

