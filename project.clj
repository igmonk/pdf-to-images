(defproject pdf-to-images "0.1.0"

  :description "Clojure wrapper for the PDFBox that converts a given page range of a PDF document to bitmap images."

  :url "https://github.com/igor-moiseyenko/pdf-to-images"

  :license {:name "Eclipse Public License version 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.pdfbox/pdfbox "2.0.0"]
                 [org.apache.pdfbox/pdfbox-tools "2.0.0"]]

  :main ^:skip-aot pdf-to-images.core

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
