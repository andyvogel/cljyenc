(ns cljyenc.encode
  (:require [cljyenc.core :refer [yenc-encode-byte-seq-to-string]]))

(defn bytes->string 
  "Takes a sequence of bytes and converts it to a YEncoded string
   Set header is true to generate a =ybegin and =yend prologue 
   and epilogue, false to not"
  [bytes header]
  (if header 
    (str "=ybegin" (yenc-encode-byte-seq-to-string bytes) "=yend")
    (yenc-encode-byte-seq-to-string bytes)))

(defn bytes->file
  "Takes a sequence of bytes, YEncodes and saves it to filename
   Always enerates an epilogue and prologue for files"
  [bytes filename]
  (spit filename (bytes->string bytes true)))