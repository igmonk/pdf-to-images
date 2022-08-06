(ns pdf-to-images.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [pdf-to-images.core :refer :all])
  (:import [org.apache.pdfbox.tools.imageio ImageIOUtil]
           [javax.imageio ImageIO]
           (java.io ByteArrayOutputStream)))

(deftest core-all
  (testing "Image to image"
    (let [path  "test/pdf_to_images/assets/"
          image (first (pdf-to-images nil image-to-image :pathname (str path "dummy.pdf")))
          img-2 (ImageIO/read (io/file (str path "dummy.png")))
          baos1 (ByteArrayOutputStream.)
          baos2 (ByteArrayOutputStream.)]
      (ImageIO/write image "png" baos1)
      (.flush baos1)
      (ImageIO/write img-2 "png" baos2)
      (.flush baos2)
      (is (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2)))))

  (testing "Image to byte array"
    (let [path  "test/pdf_to_images/assets/"
          byrr  (first (pdf-to-images nil image-to-byte-array :pathname (str path "dummy.pdf")))
          img   (ImageIO/read (io/file (str path "dummy.png")))
          baos  (ByteArrayOutputStream.)]
      (ImageIOUtil/writeImage img "png" baos 300)
      (.flush baos)
      (is (java.util.Arrays/equals (.toByteArray baos) byrr))))

  (testing "Image to file"
    (let [path  "test/pdf_to_images/assets/"
          ipath (first (pdf-to-images nil image-to-file :pathname (str path "dummy.pdf")))
          image (ImageIO/read (io/file ipath))
          img-2 (ImageIO/read (io/file (str path "dummy.png")))
          baos1 (ByteArrayOutputStream.)
          baos2 (ByteArrayOutputStream.)]
      (io/delete-file ipath)
      (ImageIO/write image "png" baos1)
      (.flush baos1)
      (ImageIO/write img-2 "png" baos2)
      (.flush baos2)
      (is (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2))))))
