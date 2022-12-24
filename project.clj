(defproject tevcord "0.2.0"
  :description "Just a bot for my Discord-Server"
  :url "https://github.com/dertev/tevcord"
  :license {:name "MIT License"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.github.discljord/discljord "1.3.1"]
                 [lynxeyes/dotenv "1.1.0"]
                 [hato "0.9.0"]
                 [overtone/at-at "1.2.0"]]
  :main ^:skip-aot tevcord.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
