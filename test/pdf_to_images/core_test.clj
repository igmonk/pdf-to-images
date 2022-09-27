(ns pdf-to-images.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [pdf-to-images.core :refer :all])
  (:import [org.apache.pdfbox.tools.imageio ImageIOUtil]
           [javax.imageio ImageIO]
           (java.io ByteArrayOutputStream)))

(def path "test/pdf_to_images/assets/")
(def dpi 72)
(def quality 0.5)

(deftest image-to-image-test
  (testing "Convert single page PDF"
    (let [results (pdf-to-images nil
                                 image-to-image
                                 :pathname (str path "dummy.pdf")
                                 :dpi dpi)
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
    (let [results (pdf-to-images nil
                                 image-to-image
                                 :pathname (str path "dummy_many.pdf")
                                 :start-page 1
                                 :end-page 2
                                 :dpi dpi)
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
    (let [images   (pdf-to-images nil
                                  image-to-image
                                  :pathname (str path "dummy_many.pdf")
                                  :start-page 0
                                  :end-page 2
                                  :dpi dpi)
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
    (let [images   (pdf-to-images nil
                                  image-to-image
                                  :pathname (str path "dummy_many.pdf")
                                  :dpi dpi)
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
    (let [results (pdf-to-images nil
                                 image-to-byte-array
                                 :pathname (str path "dummy.pdf")
                                 :dpi dpi
                                 :quality quality)
          byrr    (first results)
          img     (ImageIO/read (io/file (str path "dummy.png")))
          baos    (ByteArrayOutputStream.)]
      (ImageIOUtil/writeImage img "png" baos dpi quality)
      (.flush baos)
      (is (= (count results) 1))
      (is (java.util.Arrays/equals (.toByteArray baos) byrr))))

  (testing "Convert second page from PDF"
    (let [results (pdf-to-images nil
                                 image-to-byte-array
                                 :pathname (str path "dummy_many.pdf")
                                 :start-page 1
                                 :end-page 2
                                 :dpi dpi
                                 :quality quality)
          byrr    (first results)
          img     (ImageIO/read (io/file (str path "dummy_many_p2.png")))
          baos    (ByteArrayOutputStream.)]
      (ImageIOUtil/writeImage img "png" baos dpi quality)
      (.flush baos)
      (is (= (count results) 1))
      (is (java.util.Arrays/equals (.toByteArray baos) byrr))))

  (testing "Convert multiple pages from PDF"
    (let [byrrs     (pdf-to-images nil
                                   image-to-byte-array
                                   :pathname (str path "dummy_many.pdf")
                                   :start-page 0
                                   :end-page 2
                                   :dpi dpi
                                   :quality quality)
          byrrs-idx (map-indexed vector byrrs)
          results   (map (fn [item]
                           (let [pos  (first item)
                                 byrr (last item)
                                 png  (ImageIO/read (io/file (str path "dummy_many_p" (inc pos) ".png")))
                                 baos (ByteArrayOutputStream.)]
                             (ImageIOUtil/writeImage png "png" baos dpi quality)
                             (.flush baos)
                             (is (java.util.Arrays/equals (.toByteArray baos) byrr))))
                         byrrs-idx)]
      (is (= (count byrrs) 2))
      (is (every? identity results) true)))

  (testing "Convert all pages from PDF"
    (let [byrrs     (pdf-to-images nil
                                   image-to-byte-array
                                   :pathname (str path "dummy_many.pdf")
                                   :dpi dpi
                                   :quality quality)
          byrrs-idx (map-indexed vector byrrs)
          results   (map (fn [item]
                           (let [pos  (first item)
                                 byrr (last item)
                                 png  (ImageIO/read (io/file (str path "dummy_many_p" (inc pos) ".png")))
                                 baos (ByteArrayOutputStream.)]
                             (ImageIOUtil/writeImage png "png" baos dpi quality)
                             (.flush baos)
                             (is (java.util.Arrays/equals (.toByteArray baos) byrr))))
                         byrrs-idx)]
      (is (= (count byrrs) 3))
      (is (every? identity results) true))))

(deftest image-to-file-test
  (testing "Convert single page PDF"
    (let [ipath (first (pdf-to-images nil
                                      image-to-file
                                      :pathname (str path "dummy.pdf")
                                      :dpi dpi))
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
          (io/delete-file ipath)))))

  (testing "Convert second page from PDF"
    (let [ipath (first (pdf-to-images nil
                                      image-to-file
                                      :pathname (str path "dummy_many.pdf")
                                      :start-page 1
                                      :end-page 2
                                      :dpi dpi))
          image (ImageIO/read (io/file ipath))
          img-2 (ImageIO/read (io/file (str path "dummy_many_p2.png")))
          baos1 (ByteArrayOutputStream.)
          baos2 (ByteArrayOutputStream.)]
      (try
        (ImageIO/write image "png" baos1)
        (.flush baos1)
        (ImageIO/write img-2 "png" baos2)
        (.flush baos2)
        (is (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2)))
        (finally
          (io/delete-file ipath)))))

  (testing "Convert multiple pages from PDF"
    (let [paths     (pdf-to-images nil
                                   image-to-file
                                   :pathname (str path "dummy_many.pdf")
                                   :start-page 0
                                   :end-page 2
                                   :dpi dpi)
          paths-idx (map-indexed vector paths)
          results   (map (fn [item]
                           (let [pos   (first item)
                                 ipath (last item)
                                 image (ImageIO/read (io/file ipath))
                                 png   (ImageIO/read (io/file (str path "dummy_many_p" (inc pos) ".png")))
                                 baos1 (ByteArrayOutputStream.)
                                 baos2 (ByteArrayOutputStream.)]
                             (try
                               (ImageIO/write image "png" baos1)
                               (.flush baos1)
                               (ImageIO/write png "png" baos2)
                               (.flush baos2)
                               (is (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2)))
                               (finally
                                 (io/delete-file ipath)))))
                         paths-idx)]
      (is (= (count paths) 2))
      (is (every? identity results) true)))

  (testing "Convert all pages from PDF"
    (let [paths     (pdf-to-images nil
                                   image-to-file
                                   :pathname (str path "dummy_many.pdf")
                                   :dpi dpi)
          paths-idx (map-indexed vector paths)
          results   (map (fn [item]
                           (let [pos   (first item)
                                 ipath (last item)
                                 image (ImageIO/read (io/file ipath))
                                 png   (ImageIO/read (io/file (str path "dummy_many_p" (inc pos) ".png")))
                                 baos1 (ByteArrayOutputStream.)
                                 baos2 (ByteArrayOutputStream.)]
                             (try
                               (ImageIO/write image "png" baos1)
                               (.flush baos1)
                               (ImageIO/write png "png" baos2)
                               (.flush baos2)
                               (is (java.util.Arrays/equals (.toByteArray baos1) (.toByteArray baos2)))
                               (finally
                                 (io/delete-file ipath)))))
                         paths-idx)]
      (is (= (count paths) 3))
      (is (every? identity results) true))))
