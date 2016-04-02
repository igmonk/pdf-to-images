# pdf-to-images

Clojure wrapper for the PDFBox that converts a given page range of a PDF document to bitmap images.

## Installation

Add the following dependency to your `project.clj` file:

    [pdf-to-images "0.1.0"]

## Usage

Import namespace example:

```clojure
(:require [pdf-to-images :refer :all])
```

Basic usage example:

```clojure
(let [image-paths (pdf-to-images "path-to-pdf")]
  (prn (str "Images count: " (count image-paths)))
  (map prn image-paths))

;; "Images count: n"
;; "path-to-image-0"
;; "path-to-image-1"
;; ...
;; "path-to-image-n"
```

Options using example:

```clojure
(let [image-paths (pdf-to-images "path-to-pdf" :start-page 0 :end-page 1 :dpi 100 :ext jpg)]
  (prn (str "Images count: " (count images-paths)))
  (map prn image-paths))

;; "Images count: 1"
;; "path-to-image-0"
```

## License

Copyright © 2016 Igor Moiseyenko

Distributed under the Eclipse Public License version 1.0.
