(ns todo.frontend.core
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [reagent.dom.client :as rdom]))

(defonce app-state
  (r/atom {:next-id 3
           :todos [{:id 1
                    :title "Estudar Clojure"
                    :description "Revisar conceitos de imutabilidade e atoms"
                    :completed true}
                   {:id 2
                    :title "Criar interface com Reagent"
                    :description "Implementar componentes reativos com estado local"
                    :completed false}]}))

(defn add-todo! [title description]
  (swap! app-state
         (fn [state]
           (let [id (:next-id state)
                 new-todo {:id id
                           :title title
                           :description description
                           :completed false}]
             (-> state
                 (update :todos conj new-todo)
                 (update :next-id inc))))))

(defn toggle-todo! [id]
  (swap! app-state update :todos
         (fn [todos]
            (mapv (fn [todo]
                    (if (= (:id todo) id)
                      (update todo :completed not)
                      todo))
                  todos))))

(defn todo-form []
  (let [form-state (r/atom {:title "" :description ""})]
    (fn []
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
         :on-change #(swap! form-state assoc :title (.. % -target -value))}]
       [:textarea.todo-textarea
        {:placeholder "Descrição (opcional)..."
         :value (:description @form-state)
         :on-change #(swap! form-state assoc :description (.. % -target -value))}]
       [:button.todo-button {:type "submit"} "Adicionar Tarefa"]])))

(defn todo-item [{:keys [id title description completed]}]
  [:li.todo-item {:class (when completed "completed")}
   [:input {:type "checkbox"
            :checked completed
            :on-change #(toggle-todo! id)}]
   [:div.todo-content
    [:div.todo-title title]
    (when-not (str/blank? description)
      [:div.todo-description description])]])

(defn todo-list []
  (let [todos (:todos @app-state)]
    (if (empty? todos)
      [:p.empty-state "Nenhuma tarefa cadastrada."]
      [:ul.todo-list
       (for [todo todos]
         ^{:key (:id todo)}
         [todo-item todo])])))

(defn app []
  [:div.app-container
   [:h1 "Lista de Tarefas"]
   [todo-form]
   [todo-list]])

(defonce root
  (delay (rdom/create-root (.getElementById js/document "app"))))

(defn ^:dev/after-load reload! []
  (rdom/render @root [app]))

(defn init! []
  (reload!))
