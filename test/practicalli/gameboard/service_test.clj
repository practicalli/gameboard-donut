;; ---------------------------------------------------------
;; practicalli.gameboard.service-test
;;
;; Example unit tests for practicalli.gameboard
;;
;; - `deftest` - test a specific function
;; - `testing` logically group assertions within a function test
;; - `is` assertion:  expected value then function call
;; ---------------------------------------------------------

(ns practicalli.gameboard.service-test
  (:require [clojure.test :refer [deftest is testing]]
            [practicalli.gameboard.service :as gameboard]))

(deftest service-test
  (testing "TODO: Start with a failing test, make it pass, then refactor"

    ;; TODO: fix greet function to pass test
    (is (= "practicalli gameboard service developed by the secret engineering team"
           (gameboard/greet)))

    ;; TODO: fix test by calling greet with {:team-name "Practicalli Engineering"}
    (is (= (gameboard/greet "Practicalli Engineering")
           "practicalli gameboard service developed by the Practicalli Engineering team"))))
