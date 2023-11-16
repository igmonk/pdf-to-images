(defproject org.clojars.roboli/pdf-2-images "0.1.2"

  :description "Clojure wrapper for the PDFBox that converts a page range of a PDF document to images."

  :url "https://github.com/roboli/pdf-2-images"

  :license {:name "Eclipse Public License version 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.pdfbox/pdfbox "2.0.0"]
                 [org.apache.pdfbox/pdfbox-tools "2.0.0"]]

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
