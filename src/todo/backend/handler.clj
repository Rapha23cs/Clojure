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
  (let [{:keys [title description]} body]
    (cond
      (not (and (string? title) (not (str/blank? title))))
      {:status 400
       :body {:error "O 'título' (title) é obrigatório"}}

      (not (or (nil? description) (string? description)))
      {:status 400
       :body {:error "A descrição (description) deve ser um texto"}}

      :else
      {:status 201
       :body (db/create-todo body)})))
