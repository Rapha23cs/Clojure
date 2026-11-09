(ns todo.backend.handler
  (:require [clojure.string :as str]
            [todo.backend.db :as db]))

(defn hello-handler [_request]
  {:status 200
   :body "Hello, World!"})

(defn list-todos-handler [_request]
  {:status 200
   :body {:todos (db/get-all-todos)}})

(defn create-todo-handler [{:keys [body]}]
  (let [title (:title body)]
    (if (and (string? title) (not (str/blank? title)))
      {:status 201
       :body (db/create-todo (select-keys body [:title :description]))}
      {:status 400
       :body {:error "O 'título' (title) é obrigatório"}})))
