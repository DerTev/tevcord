(ns tevcord.bot
  (:require [clojure.core.async :as async]
            [discljord.connections :as connections]
            [discljord.messaging :as messaging]
            [dotenv :as env]
            [tevcord.scheduler :as scheduler]

            [tevcord.commands :as cmds]
            [tevcord.commands.clojure :as clj-cmds]))

(defn start-bot
  "Start bot"
  [token]
  (println "Start bot...")
  (let [event-channel (async/chan 100)
        connection-channel (connections/connect-bot! token
                                                     event-channel
                                                     :intents #{:guilds :guild-messages})
        message-channel (messaging/start-connection! token)]

    (println "Register schedulers...")
    (scheduler/schedule-every-x (* 1000 60 60 24) #(clj-cmds/clojure-func {:msg-conn message-channel
                                                                           :data     {:channel-id (-> :CLOJURE_CHANNEL
                                                                                                      env/env
                                                                                                      Long/parseLong)}}))

    (println "Update presence...")
    (connections/status-update! connection-channel :activity
                                (connections/create-activity :type (-> :ACTIVITY_TYPE
                                                                       env/env
                                                                       keyword)
                                                             :name (env/env :ACTIVITY_NAME)))

    (println "Start listening to events...")
    (try
      (loop []
        (let [[event-type event-data] (async/<!! event-channel)]
          (-> :PREFIX
              env/env
              (cmds/parse-command event-type event-data message-channel)
              cmds/run-command)
          (recur)))
      (finally
        (messaging/stop-connection! message-channel)
        (connections/disconnect-bot! connection-channel)
        (async/close! event-channel)))))
