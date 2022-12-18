(ns tevcord.commands.clojure
  (:require [hato.client :as http-client]
            [dotenv :as env]
            [discljord.messaging :as msg]
            [clojure.data.json :as json]))


(defn clojure-func
  "Sends random clojure function"
  [cmd]
  (let [rand-func (-> :CLOJURE_FUNC_API
                      env/env
                      (http-client/get {:as :json})
                      (get :body)
                      (json/read-json true))]
    (msg/create-message! (get cmd :msg-conn)
                         (get-in cmd [:data :channel-id])
                         :embed {:title  "Here is another Clojure function!"
                                 :fields [{:name "Name" :value (str "``" (get rand-func :name) "``")}
                                          (if (nil? (get rand-func :description))
                                            {:name "Description" :value "There is no description."}
                                            {:name "Description" :value (get rand-func :description)})
                                          (if (nil? (get rand-func :args))
                                            {:name "Arguments" :value "There are no arguments."}
                                            {:name "Arguments" :value (pr-str (get rand-func :args))})]})))