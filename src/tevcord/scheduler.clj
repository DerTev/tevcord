(ns tevcord.scheduler
  (:require [overtone.at-at :as at-at]))

(def pool (at-at/mk-pool))

(defn schedule-every-x
  "Register a scheduler running every `x` milliseconds function `func`"
  [x func]
  (at-at/every x func pool))