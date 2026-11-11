(ns todo.backend.core
  (:require [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :as response]
            [todo.backend.db :as db]
            [todo.backend.handler :as handler])
  (:gen-class))

(def app-routes
  (ring/router
   ["/api"
    ["/hello" {:get {:handler handler/hello-handler}}]
    ["/todos" {:get {:handler handler/list-todos-handler}
               :post {:handler handler/create-todo-handler}}]
    ["/todos/:id" {:get {:handler handler/get-todo-handler}
                   :put {:handler handler/update-todo-handler}
                   :patch {:handler handler/update-todo-handler}
                   :delete {:handler handler/delete-todo-handler}}]
    ["/todos/:id/toggle" {:patch {:handler handler/toggle-todo-handler}
                          :put {:handler handler/toggle-todo-handler}}]]))

(def app
  (-> (ring/ring-handler
       app-routes
       (ring/routes
        (ring/create-resource-handler {:path "/"})
        (ring/create-default-handler
         {:not-found (fn [_]
                       (or (response/resource-response "index.html" {:root "public"})
                           {:status 404 :body "Not Found"}))}))
       {:middleware [wrap-json-response
                     [wrap-json-body {:keywords? true}]
                     wrap-params
                     wrap-keyword-params]})
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete :patch :options]
                 :access-control-allow-headers ["Content-Type" "Authorization"])))

(defn start-server [port]
  (db/initialize-database!)
  (let [server (jetty/run-jetty #'app {:host "127.0.0.1" :port port :join? false})]
    (println "Servidor iniciado na porta" port)
    server))

(defn -main [& args]
  (start-server (Integer/parseInt (or (first args) "3000"))))
