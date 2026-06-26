(ns top.kzre.krro.brush-color.core-test
  (:require [clojure.test :refer :all]
            [top.kzre.krro.brush.protocol :as p]
            [top.kzre.krro.brush-color.core :as core]))

(deftest test-mix-colors-basic
  (let [fg [1.0 0.0 0.0 1.0]
        bg [0.0 0.0 0.0 1.0]
        result (p/mix-colors core/default-mixer fg bg 0.5 :normal)]
    (is (every? #(<= 0.0 % 1.0) result))
    (is (= 4 (count result)))
    (is (> (first result) 0.4))
    (is (< (first result) 0.6))))

(deftest test-mix-colors-opacity
  (let [fg [0.0 0.0 1.0 1.0]
        bg [1.0 1.0 1.0 1.0]
        result (p/mix-colors core/default-mixer fg bg 0.0 :normal)]
    (is (= [1.0 1.0 1.0 1.0] result))))

(deftest test-mix-colors-blend-mode
  (let [fg [1.0 0.0 0.0 1.0]
        bg [0.0 1.0 0.0 1.0]
        result-multiply (p/mix-colors core/default-mixer fg bg 1.0 :multiply)
        result-screen   (p/mix-colors core/default-mixer fg bg 1.0 :screen)]
    (is (every? #(<= 0.0 % 1.0) result-multiply))
    (is (every? #(<= 0.0 % 1.0) result-screen))
    (is (< (first result-multiply) 0.1))
    (is (> (first result-screen) 0.9))))

(deftest test-mix-pigments
  (let [result (p/mix-pigments core/default-mixer
                               [:titanium-white :cadmium-red]
                               [0.5 0.5])]
    (is (vector? result))
    (is (= 3 (count result)))
    (is (every? #(<= 0.0 % 1.0) result))
    ;; 白色+红色混合结果：红色通道 > 0.3，绿色通道 < 0.8
    (is (> (first result) 0.3))
    (is (< (second result) 0.8))))

(deftest test-mix-pigments-empty
  (let [result (p/mix-pigments core/default-mixer [] [])]
    (is (= [0.0 0.0 0.0] result))))