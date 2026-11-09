(ns todo.backend.db)

(def ^:private todos-db (atom {}))
(def ^:private next-id (atom 0))

(defn get-all-todos []
  (vec (vals @todos-db)))

(defn create-todo [todo-data]
  (let [id (swap! next-id inc)
        todo (assoc todo-data
                    :id id
                    :completed false
                    :created-at (str (java.time.Instant/now)))]
    (swap! todos-db assoc id todo)
    todo))
