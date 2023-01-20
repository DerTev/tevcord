(ns tevcord.commands.status
  (:require [discljord.messaging :as msg]
            [hato.client :as http-client]
            [dotenv :as env]
            [clojure.string :as cstr]))

(def services (delay (apply assoc {} (flatten (-> :SERVICES
                                                  env/env
                                                  (cstr/split #"[,:](?!\/\/)"))))))

(defn- http-error
  "Check if ``code`` is an error-code"
  [code]
  (or (cstr/starts-with? (str code) "4")
      (cstr/starts-with? (str code) "5")))

(defn- http-status [url]
  (try
    (:status (http-client/get url {:throw-exceptions? false}))
    (catch Exception _
      503)))

(defn status [cmd]
  (let [states (for [service (-> @services
                                 count
                                 range)
                     :let [key (nth (keys @services) service)
                           val (nth (vals @services) service)]]
                 [key (http-status val)])]
    (msg/create-message! (get cmd :msg-conn)
                         (get-in cmd [:data :channel-id])
                         :embed {:title  "Status"
                                 :fields (vec (for [state states]
                                                {:name  (nth state 0)
                                                 :value (str (if (http-error (nth state 1))
                                                               "\uD83D\uDD34 - Offline"
                                                               "\uD83D\uDFE2 - Online")
                                                             " (" (nth state 1) ")")}))})))