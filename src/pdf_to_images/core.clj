(ns pdf-to-images.core
  (:require [clojure.java.io :as io])
  (:import [org.apache.pdfbox.pdmodel PDDocument]
           [org.apache.pdfbox.rendering PDFRenderer]
           [org.apache.pdfbox.rendering ImageType]
           [org.apache.pdfbox.tools.imageio ImageIOUtil]
           (java.io ByteArrayOutputStream)))

(defn- range-intersection-for-border-pairs
  [range-border-pairs]
  (let [intersection-vec (reduce
                           (fn [[start1 end1] [start2 end2]]
                             [(max start1 start2) (min end1 end2)])
                           (first range-border-pairs)
                           (rest range-border-pairs))]
    (range (first intersection-vec)
           (last intersection-vec))))

(defn image-to-image [{image :image}] image)

(defn image-to-byte-array
  [{image :image ext :ext dpi :dpi quality :quality}]
  (let [baos (ByteArrayOutputStream.)]
    (try
      (ImageIOUtil/writeImage image ext baos dpi quality)
      (.flush baos)
      (.toByteArray baos)
      (finally
        (if (not= baos nil) (.close baos))))))

(defn image-to-file
  [{image :image image-index :image-index ext :ext dpi :dpi quality :quality base-path :base-path}]
  (let [image-pathname (str base-path "-" image-index "." ext)]
    (ImageIOUtil/writeImage image image-pathname dpi quality)
    image-pathname))

(defn pdf-to-images
  "Converts a page range of a PDF document to images using one of the defined image handlers
  (image to image, image to byte array or image to file) or the custom one.
  Returns a sequence consisting of the images, byte arrays or pathnames depending on the
  selected image handler.

  Options are key-value pairs and may be one of:
    :start-page - The start page, defaults to 0
    :end-page   - The end page, defaults to Integer/MAX_VALUE
    :dpi        - Screen resolution, defaults to 300
    :quality    - Quality to be used when compressing the image (0 < quality < 1), defaults to 1
    :ext        - The target file format, defaults to png
    :pathname   - Path to the PDF file, used if pdf-file is not specified (= nil)"
  [pdf-file image-handler & {:keys [start-page end-page dpi quality ext pathname]
                             :or {start-page 0
                                  end-page (Integer/MAX_VALUE)
                                  dpi 300
                                  quality 1
                                  ext "png"}}]

  (let [pdf-file (if pdf-file pdf-file (io/file pathname))
        pd-document (PDDocument/load pdf-file)
        pdf-renderer (PDFRenderer. pd-document)
        pages (vec (.getPages pd-document))
        page-range (range-intersection-for-border-pairs [[0 (count pages)]
                                                         [start-page end-page]])]

    (try
      (doall
        (map
          (fn [page-index]
            (let [image (.renderImageWithDPI pdf-renderer page-index dpi ImageType/RGB)]
              (image-handler {:image image
                              :image-index page-index
                              :ext ext
                              :dpi dpi
                              :quality quality
                              :base-path (.getAbsolutePath pdf-file)})))
          page-range))
      (finally
        (if (not= pd-document nil) (.close pd-document))))))
