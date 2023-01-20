(ns tevcord.commands
  (:require [clojure.string :as string]

            [tevcord.commands.clojure :as cclj]
            [tevcord.commands.status :as cstatus]))

(def commands {"clojure-func" cclj/clojure-func
               "status" cstatus/status})

(defn parse-command
  "Parse discord text message for [[run-command]]"
  [cmd-prefix event-type event-data conn]
  (if (and (= event-type :message-create) (-> event-data
                                              (get :content)
                                              (string/starts-with? cmd-prefix)))
    {:command (nth (string/split (string/replace-first (get event-data :content)
                                                       (re-pattern cmd-prefix) "")
                                 #" ") 0)
     :args    (let [args (-> event-data
                             (get :content)
                             (string/split #" "))]
                (if (> (count args) 1)
                  (drop 1 args)
                  []))
     :data    event-data
     :msg-conn    conn}
    nil))

(defn run-command
  "Run discord-text-command"
  [cmd]
  (if (not (nil? cmd))
    (let [command-name (get cmd :command)]
      (if (contains? commands command-name)
        ((get commands command-name) cmd)
        (println (str "Command not found: " command-name))))))