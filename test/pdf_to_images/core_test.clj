(ns pdf-to-images.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [pdf-to-images.core :refer :all])
  (:import [org.apache.pdfbox.tools.imageio ImageIOUtil]
           [javax.imageio ImageIO]
           (java.io ByteArrayOutputStream)))

(deftest image-to-image-test
  (testing "Convert single page PDF"
    (let [path    "test/pdf_to_images/assets/"
          results (pdf-to-images nil image-to-image :pathname (str path "dummy.pdf"))
          image   (first results)
          img-2   (ImageIO/read (io/file (str path "dummy.png")))
          baos1   (ByteArrayOutputStream.)
          baos2   (ByteArrayOutputStream.)]
      (ImageIO/write image "png" baos1)
      (.flush baos1)
      (ImageIO/write img-2 "png" baos2)
      (.flush baos2)
      (is (= (count results) 1))
      (is (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2)))))

  (testing "Convert second page from PDF"
    (let [path    "test/pdf_to_images/assets/"
          results (pdf-to-images nil image-to-image :pathname (str path "dummy_many.pdf") :start-page 1 :end-page 2)
          image   (first results)
          img-2   (ImageIO/read (io/file (str path "dummy_many_p2.png")))
          baos1   (ByteArrayOutputStream.)
          baos2   (ByteArrayOutputStream.)]
      (ImageIO/write image "png" baos1)
      (.flush baos1)
      (ImageIO/write img-2 "png" baos2)
      (.flush baos2)
      (is (= (count results) 1))
      (is (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2)))))

  (testing "Convert multiple pages from PDF"
    (let [path     "test/pdf_to_images/assets/"
          images   (pdf-to-images nil image-to-image :pathname (str path "dummy_many.pdf") :start-page 0 :end-page 2)
          imgs-idx (map-indexed vector images)
          results  (map (fn [item]
                          (let [pos   (first item)
                                img   (last item)
                                png   (ImageIO/read (io/file (str path "dummy_many_p" (inc pos) ".png")))
                                baos1 (ByteArrayOutputStream.)
                                baos2 (ByteArrayOutputStream.)]
                            (ImageIO/write img "png" baos1)
                            (.flush baos1)
                            (ImageIO/write png "png" baos2)
                            (.flush baos2)
                            (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2))))
                        imgs-idx)]
      (is (= (count images) 2))
      (is (every? identity results) true)))

  (testing "Convert all pages from PDF"
    (let [path     "test/pdf_to_images/assets/"
          images   (pdf-to-images nil image-to-image :pathname (str path "dummy_many.pdf"))
          imgs-idx (map-indexed vector images)
          results  (map (fn [item]
                          (let [pos   (first item)
                                img   (last item)
                                png   (ImageIO/read (io/file (str path "dummy_many_p" (inc pos) ".png")))
                                baos1 (ByteArrayOutputStream.)
                                baos2 (ByteArrayOutputStream.)]
                            (ImageIO/write img "png" baos1)
                            (.flush baos1)
                            (ImageIO/write png "png" baos2)
                            (.flush baos2)
                            (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2))))
                        imgs-idx)]
      (is (= (count images) 3))
      (is (every? identity results) true))))

(deftest image-to-byte-array-test
  (testing "Convert single page PDF"
    (let [path "test/pdf_to_images/assets/"
          byrr (first (pdf-to-images nil image-to-byte-array :pathname (str path "dummy.pdf")))
          img  (ImageIO/read (io/file (str path "dummy.png")))
          baos (ByteArrayOutputStream.)]
      (ImageIOUtil/writeImage img "png" baos 300)
      (.flush baos)
      (is (java.util.Arrays/equals (.toByteArray baos) byrr))))

  (testing "Convert first page from PDF"
    (let [path "test/pdf_to_images/assets/"
          byrr (first (pdf-to-images nil image-to-byte-array :pathname (str path "dummy_many.pdf")))
          img  (ImageIO/read (io/file (str path "dummy_many_p1.png")))
          baos (ByteArrayOutputStream.)]
      (ImageIOUtil/writeImage img "png" baos 300)
      (.flush baos)
      (is (java.util.Arrays/equals (.toByteArray baos) byrr))))

  (testing "Convert second page from PDF"
    (let [path "test/pdf_to_images/assets/"
          byrr (first (pdf-to-images nil image-to-byte-array :pathname (str path "dummy_many.pdf") :start-page 1 :end-page 2))
          img  (ImageIO/read (io/file (str path "dummy_many_p2.png")))
          baos (ByteArrayOutputStream.)]
      (ImageIOUtil/writeImage img "png" baos 300)
      (.flush baos)
      (is (java.util.Arrays/equals (.toByteArray baos) byrr))))

  (testing "Convert multiple pages from PDF"
    (let [path     "test/pdf_to_images/assets/"
          byrrs     (pdf-to-images nil image-to-byte-array :pathname (str path "dummy_many.pdf") :start-page 0 :end-page 3)
          byrrs-idx (map-indexed vector byrrs)
          results   (map (fn [item]
                           (let [pos  (first item)
                                 byrr (last item)
                                 png  (ImageIO/read (io/file (str path "dummy_many_p" (inc pos) ".png")))
                                 baos (ByteArrayOutputStream.)]
                             (ImageIOUtil/writeImage png "png" baos 300)
                             (.flush baos)
                             (is (java.util.Arrays/equals (.toByteArray baos) byrr))))
                         byrrs-idx)]
      (is (every? identity results) true))))

(deftest image-to-file-test
  (testing "Single page PDF"
    (let [path  "test/pdf_to_images/assets/"
          ipath (first (pdf-to-images nil image-to-file :pathname (str path "dummy.pdf")))
          image (ImageIO/read (io/file ipath))
          img-2 (ImageIO/read (io/file (str path "dummy.png")))
          baos1 (ByteArrayOutputStream.)
          baos2 (ByteArrayOutputStream.)]
      (try
        (ImageIO/write image "png" baos1)
        (.flush baos1)
        (ImageIO/write img-2 "png" baos2)
        (.flush baos2)
        (is (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2)))
        (finally
          (io/delete-file ipath))))))
