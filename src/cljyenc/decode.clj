(ns cljyenc.decode
  (:require [cljyenc.core :refer [yenc-decode-string-to-byte-seq
                                  yenc-find-prologue
                                  yenc-find-epilogue]]))

(defn string->bytes 
  "Converts a YEnc string into a sequence of bytes. 
   s is a string containing Yenc data
   header is true to seek a =ybegin and =yend header
   in the file."
  [s header]
  (let [enc-start  (yenc-find-prologue (seq s))
        found-epi  (yenc-find-epilogue (seq s))]
    (if header (if (and enc-start found-epi)
        ;; Using drop-last to remove extra bytes caused by the =yend epilogue
                 (drop-last 4 (yenc-decode-string-to-byte-seq enc-start))
                 (throw (Exception. "YEnc prologue or epilogue is missing during decoding!")))
        (if found-epi (drop-last 4 (yenc-decode-string-to-byte-seq enc-start))
            (yenc-decode-string-to-byte-seq enc-start)))))

(defn file->bytes 
  "Reads a YEncoded file and reads a sequence of bytes into memory. Always
   assumes yenc files have a header in them"
  [filename]
  (string->bytes (slurp filename) true))