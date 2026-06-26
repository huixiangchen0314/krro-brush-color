(ns top.kzre.krro.brush-color.core
  "krro-brush-color 核心：将 krro-color 的颜色混合功能适配为 krro-brush 的 IColorMixer 协议。
   提供混合模式、颜料混合等全部能力。"
  (:require [top.kzre.krro.brush.protocol :as p]      ;; 通过 require 获取协议
            [top.kzre.krro.color.blend :as blend]
            [top.kzre.krro.color.composite :as comp]
            [top.kzre.krro.color.pigment :as pigment]
            [top.kzre.krro.color.util :as util]))

(defrecord KrroColorMixer []
  p/IColorMixer                                        ;; 使用别名引用协议
  (mix-colors [_ fg bg opacity blend-mode]
    (let [fg-rgba (if (== 4 (count fg)) (vec fg) (conj (vec fg) 1.0))
          bg-rgba (if (== 4 (count bg)) (vec bg) (conj (vec bg) 1.0))
          ;; 1. 先进行 RGB 颜色混合（使用 krro-color 的标准混合模式）
          blended-rgb (blend/blend (subvec bg-rgba 0 3) (subvec fg-rgba 0 3) blend-mode)
          ;; 2. 计算最终需要合成的前景色（考虑笔刷不透明度）
          fg-a (peek fg-rgba)
          eff-fg-a (* fg-a opacity)
          ;; 3. 使用 Porter-Duff over 合成，输出正确的带 alpha 结果
          result (comp/over bg-rgba (conj (vec blended-rgb) eff-fg-a))]
      (mapv #(util/clamp 0.0 1.0 %) result)))

  (mix-pigments [_ pigment-keys ratios]
    (if (empty? pigment-keys)
      [0.0 0.0 0.0]
      (let [pairs (mapv vector pigment-keys ratios)]
        (pigment/kubelka-munk-mix-multiple pairs)))))

(def default-mixer (->KrroColorMixer))