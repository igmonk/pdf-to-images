(ns pdf-to-images.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [pdf-to-images.core :refer :all])
  (:import [javax.imageio ImageIO]
           (java.io ByteArrayOutputStream)))

(deftest core-all
  (testing "From PDF file to image"
    (let [path  "test/pdf_to_images/assets/"
          image (first (pdf-to-images nil image-to-image :pathname (str path "dummy.pdf")))
          img-2 (ImageIO/read (io/file (str path "dummy.png")))
          baos1 (ByteArrayOutputStream.)
          baos2 (ByteArrayOutputStream.)]
      (ImageIO/write image "png" baos1)
      (.flush baos1)
      (ImageIO/write img-2 "png" baos2)
      (.flush baos2)
      (is (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2))))))
