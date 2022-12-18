(ns tevcord.core
  (:gen-class)
  (:require [dotenv :as env]
            [tevcord.bot :as bot]))

(defn -main [& args]
  (bot/start-bot (env/env :TOKEN)))
