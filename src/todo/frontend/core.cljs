(ns todo.frontend.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure.string :as str]
            [reagent.core :as r]
            [reagent.dom.client :as rdom]))

(def api-base-url "http://localhost:3000/api/todos")

(defonce app-state
  (r/atom {:todos []
           :loading? false
           :error nil}))

(defn fetch-todos! []
  (swap! app-state assoc :loading? true :error nil)
  (go
    (let [response (<! (http/get api-base-url {:with-credentials? false}))]
      (if (:success response)
        (swap! app-state assoc
               :todos (vec (get-in response [:body :todos] []))
               :loading? false
               :error nil)
        (swap! app-state assoc
               :loading? false
               :error "Erro ao carregar tarefas da API.")))))

(defn add-todo! [title description]
  (swap! app-state assoc :loading? true :error nil)
  (go
    (let [response (<! (http/post api-base-url
                                  {:json-params {:title title
                                                 :description description}
                                   :with-credentials? false}))]
      (if (:success response)
        (let [new-todo (:body response)]
          (swap! app-state (fn [state]
                             (-> state
                                 (update :todos conj new-todo)
                                 (assoc :loading? false :error nil)))))
        (swap! app-state assoc
               :loading? false
               :error "Erro ao salvar tarefa na API.")))))

(defn toggle-todo! [id]
  (swap! app-state update :todos
         (fn [todos]
           (mapv (fn [todo]
                   (if (= (:id todo) id)
                     (update todo :completed not)
                     todo))
                 todos))))

(defn header []
  (let [{:keys [loading?]} @app-state]
    [:div.header
     [:h1 "Lista de Tarefas"]
     [:button.reload-button
      {:on-click #(fetch-todos!)
       :disabled loading?}
      (if loading? "Carregando..." "Atualizar")]]))

(defn alert-message []
  (let [{:keys [error]} @app-state]
    (when error
      [:div.alert.alert-error
       [:span error]
       [:button.alert-close {:on-click #(swap! app-state assoc :error nil)} "×"]])))

(defn todo-form []
  (let [form-state (r/atom {:title "" :description ""})]
    (fn []
      (let [{:keys [loading?]} @app-state]
        [:form.todo-form
         {:on-submit (fn [e]
                       (.preventDefault e)
                       (let [{:keys [title description]} @form-state]
                         (when-not (str/blank? title)
                           (add-todo! (str/trim title) (str/trim description))
                           (reset! form-state {:title "" :description ""}))))}
         [:input.todo-input
          {:type "text"
           :placeholder "Título da tarefa..."
           :value (:title @form-state)
           :disabled loading?
           :on-change #(swap! form-state assoc :title (.. % -target -value))}]
         [:textarea.todo-textarea
          {:placeholder "Descrição (opcional)..."
           :value (:description @form-state)
           :disabled loading?
           :on-change #(swap! form-state assoc :description (.. % -target -value))}]
         [:button.todo-button
          {:type "submit"
           :disabled loading?}
          (if loading? "Enviando..." "Adicionar Tarefa")]]))))

(defn todo-item [{:keys [id title description completed]}]
  [:li.todo-item {:class (when completed "completed")}
   [:input {:type "checkbox"
            :checked (boolean completed)
            :on-change #(toggle-todo! id)}]
   [:div.todo-content
    [:div.todo-title title]
    (when-not (str/blank? description)
      [:div.todo-description description])]])

(defn todo-list []
  (let [{:keys [todos loading?]} @app-state]
    (cond
      (and loading? (empty? todos))
      [:p.empty-state "Carregando tarefas da API..."]

      (empty? todos)
      [:p.empty-state "Nenhuma tarefa encontrada no backend."]

      :else
      [:ul.todo-list
       (for [todo todos]
         ^{:key (or (:id todo) (gensym "todo-"))}
         [todo-item todo])])))

(defn app []
  [:div.app-container
   [header]
   [alert-message]
   [todo-form]
   [todo-list]])

(defonce root
  (delay (rdom/create-root (.getElementById js/document "app"))))

(defn ^:dev/after-load reload! []
  (rdom/render @root [app]))

(defn init! []
  (fetch-todos!)
  (reload!))
