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

(defn- parse-id [id-str]
  (try
    (when id-str
      (Long/parseLong (str id-str)))
    (catch Exception _ nil)))

(defn get-todo-handler [request]
  (if-let [id (parse-id (get-in request [:path-params :id]))]
    (if-let [todo (db/get-todo-by-id id)]
      {:status 200
       :body todo}
      {:status 404
       :body {:error "Tarefa não encontrada"}})
    {:status 400
     :body {:error "ID inválido"}}))

(defn update-todo-handler [{:keys [body] :as request}]
  (if-let [id (parse-id (get-in request [:path-params :id]))]
    (let [{:keys [title description]} body]
      (cond
        (and (contains? body :title)
             (or (not (string? title)) (str/blank? title)))
        {:status 400
         :body {:error "O 'título' (title) não pode ser vazio"}}

        (and (contains? body :description)
             (not (or (nil? description) (string? description))))
        {:status 400
         :body {:error "A descrição (description) deve ser um texto"}}

        :else
        (if-let [updated (db/update-todo id body)]
          {:status 200
           :body updated}
          {:status 404
           :body {:error "Tarefa não encontrada"}})))
    {:status 400
     :body {:error "ID inválido"}}))

(defn toggle-todo-handler [request]
  (if-let [id (parse-id (get-in request [:path-params :id]))]
    (if-let [updated (db/toggle-todo id)]
      {:status 200
       :body updated}
      {:status 404
       :body {:error "Tarefa não encontrada"}})
    {:status 400
     :body {:error "ID inválido"}}))

(defn delete-todo-handler [request]
  (if-let [id (parse-id (get-in request [:path-params :id]))]
    (if (db/delete-todo id)
      {:status 200
       :body {:message "Tarefa removida com sucesso" :id id}}
      {:status 404
       :body {:error "Tarefa não encontrada"}})
    {:status 400
     :body {:error "ID inválido"}}))
