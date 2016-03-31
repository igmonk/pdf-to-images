(ns pdf-to-images.core
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:import [org.apache.pdfbox.pdmodel PDDocument]
           [org.apache.pdfbox.rendering PDFRenderer]
           [org.apache.pdfbox.rendering ImageType]
           [org.apache.pdfbox.tools.imageio ImageIOUtil]))

(defn pdf-to-images
  [filename & opts]
  (let [pd-document (PDDocument/load (io/file filename))
        pdf-renderer (PDFRenderer. pd-document)
        pages (vec (.getPages pd-document))]
    (doall
      (map-indexed
        (fn [index _]
          (let [image (.renderImageWithDPI pdf-renderer index 100 ImageType/RGB)]
            (ImageIOUtil/writeImage image (str filename "-" index ".png") 100)))
        pages))
    (.close pd-document)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (str "ARGS:: " args))
  (pdf-to-images (first args)))
