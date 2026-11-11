(ns todo.backend.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def ^:private datasource
  (jdbc/get-datasource {:dbtype "sqlite" :dbname "prod.db"}))

(def ^:private query-options
  {:builder-fn rs/as-unqualified-kebab-maps})

(defn initialize-database! []
  (jdbc/execute! datasource
                 ["CREATE TABLE IF NOT EXISTS todos (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                     title TEXT NOT NULL,
                     description TEXT,
                     completed INTEGER NOT NULL DEFAULT 0 CHECK (completed IN (0, 1)),
                     created_at TEXT NOT NULL
                   )"]))

(defn- todo-response [row]
  (update row :completed #(= 1 %)))

(defn get-all-todos []
  (mapv todo-response
        (jdbc/execute! datasource
                       ["SELECT id, title, description, completed, created_at
                         FROM todos ORDER BY id DESC"]
                       query-options)))

(defn create-todo [{:keys [title description]}]
  (todo-response
   (jdbc/execute-one! datasource
                      ["INSERT INTO todos (title, description, created_at)
                        VALUES (?, ?, ?)
                        RETURNING id, title, description, completed, created_at"
                       title description (str (java.time.Instant/now))]
                      query-options)))
