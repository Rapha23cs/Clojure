(ns todo.backend.core
  (:require [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]
            [todo.backend.handler :as handler])
  (:gen-class))

(def app-routes
  (ring/router
   [["/api/hello" {:get {:handler handler/hello-handler}}]]))

(def app
  (ring/ring-handler app-routes (ring/create-default-handler)))

(defn start-server [port]
  (let [server (jetty/run-jetty #'app {:host "127.0.0.1" :port port :join? false})]
    (println "Servidor iniciado na porta" port)
    server))

(defn -main [& args]
  (start-server (Integer/parseInt (or (first args) "3000"))))
