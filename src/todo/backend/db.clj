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
  (when row
    (update row :completed #(= 1 %))))

(defn get-all-todos []
  (mapv todo-response
        (jdbc/execute! datasource
                       ["SELECT id, title, description, completed, created_at
                         FROM todos ORDER BY id DESC"]
                       query-options)))

(defn get-todo-by-id [id]
  (some-> (jdbc/execute-one! datasource
                             ["SELECT id, title, description, completed, created_at
                               FROM todos WHERE id = ?" id]
                             query-options)
          todo-response))

(defn create-todo [{:keys [title description]}]
  (todo-response
   (jdbc/execute-one! datasource
                      ["INSERT INTO todos (title, description, created_at)
                        VALUES (?, ?, ?)
                        RETURNING id, title, description, completed, created_at"
                       title description (str (java.time.Instant/now))]
                      query-options)))

(defn update-todo [id {:keys [title description completed]}]
  (when-let [current (get-todo-by-id id)]
    (let [new-title (if (some? title) title (:title current))
          new-desc  (if (some? description) description (:description current))
          new-comp  (if (some? completed) (if completed 1 0) (if (:completed current) 1 0))]
      (some-> (jdbc/execute-one! datasource
                                 ["UPDATE todos
                                   SET title = ?, description = ?, completed = ?
                                   WHERE id = ?
                                   RETURNING id, title, description, completed, created_at"
                                  new-title new-desc new-comp id]
                                 query-options)
              todo-response))))

(defn toggle-todo [id]
  (some-> (jdbc/execute-one! datasource
                             ["UPDATE todos
                               SET completed = CASE WHEN completed = 1 THEN 0 ELSE 1 END
                               WHERE id = ?
                               RETURNING id, title, description, completed, created_at"
                              id]
                             query-options)
          todo-response))

(defn delete-todo [id]
  (some-> (jdbc/execute-one! datasource
                             ["DELETE FROM todos WHERE id = ? RETURNING id" id]
                             query-options)
          :id))
