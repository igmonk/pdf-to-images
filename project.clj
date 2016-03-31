(defproject pdf-to-images "0.1.0-SNAPSHOT"

  :description "Clojure wrapper for the PDFBox that converts a given page range of a PDF document to bitmap images."

  :url "https://github.com/igor-moiseyenko/pdf-to-images"

  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :dependencies [[org.clojure/clojure "1.8.0"]]

  :main ^:skip-aot pdf-to-images.core

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
