(ns pdf-to-images.core
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:import [org.apache.pdfbox.pdmodel PDDocument]
           [org.apache.pdfbox.rendering PDFRenderer]
           [org.apache.pdfbox.rendering ImageType]
           [org.apache.pdfbox.tools.imageio ImageIOUtil]))

(defn- range-intersection-for-border-pairs
  [range-border-pairs]
  (let [intersection-vec (reduce
                           (fn [[start1 end1] [start2 end2]]
                             [(max start1 start2) (min end1 end2)])
                           (first range-border-pairs)
                           (rest range-border-pairs))]
    (range (first intersection-vec)
           (last intersection-vec))))

(defn pdf-to-images
  [filename & {:keys [start-page end-page dpi ext]
               :or {start-page 0
                    end-page (Integer/MAX_VALUE)
                    dpi 300 ext "png"}}]
  (let [pd-document (PDDocument/load (io/file filename))
        pdf-renderer (PDFRenderer. pd-document)
        pages (vec (.getPages pd-document))
        pages-count (count pages)
        page-range (range-intersection-for-border-pairs [[0 pages-count]
                                                         [start-page end-page]])]
    (try
      (doall
        (map
          (fn [page-index]
            (let [image (.renderImageWithDPI pdf-renderer page-index dpi ImageType/RGB)
                  image-filename (str filename "-" page-index "." ext)]
              (ImageIOUtil/writeImage image image-filename dpi)
              image-filename))
          page-range))
      (finally
        (if (not= pd-document nil) (.close pd-document))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (prn (pdf-to-images (first args) :start-page 0 :end-page 1 :dpi 100 :ext "jpg")))
