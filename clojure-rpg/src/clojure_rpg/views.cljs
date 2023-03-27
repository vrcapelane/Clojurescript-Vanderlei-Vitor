(ns clojure-rpg.views
  (:require
   [re-frame.core :as re-frame]
   [clojure-rpg.subs :as subs]
   [clojure-rpg.events :as events]))

(def character-races ["Elf", "Orc", "Dwarf", "Human", "Halfling"])

(def character-classes ["Mage", "Bard", "Priest", "Archer", "Warrior"])

(defn text-input [id label]
  (let [value (re-frame/subscribe [::subs/form id])]
    [:div.field
     [:label.label label]
     [:div.control
      [:input.input {:value @value
                     :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])
                     :type "text" :placeholder "Name of Character"}]]]))

(defn select-input [id label options]
  (let [value (re-frame/subscribe [::subs/form id])]
    [:div.field
     [:label.label label]
     [:div.control
      [:div.select
       [:select {:value @value
                 :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])}
        [:option {:value ""} "---"]
        (map (fn [o] [:option {:key o :value o} o]) options)]]]]))

(defn button-input [disabled-f on-click-event label]
  [:button.button {:disabled (not disabled-f)
                   :on-click #(re-frame/dispatch [on-click-event])} label])

(defn create-list-with-radio [content-id label]
  (let [characters-list @(re-frame/subscribe [content-id])]
    [:ul
     [:label.label label]
     (map (fn [{:keys [character-class character-race character-name]}]
            [:li.p-1
             {:key character-name}
             [:div.control
              [:label.radio
               [:input {:type "radio" :name "edit-radio" :on-click #(re-frame/dispatch [nil])}]
               [:text (str " " character-race " " character-name " " character-class)]]]]) characters-list)]))

(defn main-panel []
  (let [is-valid? @(re-frame/subscribe [::subs/form-is-valid? [:character-name :character-race :character-class]])
        is-removable? @(re-frame/subscribe [::subs/is-removable?])
        is-updatable? @(re-frame/subscribe [::subs/is-updatable? [:character-name :character-race :character-class]])]
    [:div.conteiner.has-background-white-ter
     [:div.section
      [:div
       [:div.level.level-item
        [:figure.image.is-128x128
         [:img {:src "https://cdn.pixabay.com/photo/2021/02/16/16/22/d-d-6021557_1280.png"}]]]
       [:div
        [:p.title.has-text-centered.has-text-info.has-text-weight-bold "Clojure - Rpg"]
        [:p.subtitle.has-text-centered.has-text-success.has-text-weight-semibold "Basic registry for Rpg!"]]]]
     [:div.section
      [:div.card.p-3
       [text-input :character-name "Character Name"]
       [select-input :character-race "Character Race" character-races]
       [select-input :character-class "Character Class" character-classes]
       [:div.level.level-left
        [:div.pr-2
         [button-input is-valid? ::events/save-form "Save"]]
        [:div.pr-2
         [:button.button {:disabled (not is-updatable?)
                          :on-click #(do (re-frame/dispatch [::events/remove-character])
                                         (re-frame/dispatch [::events/save-form]))} "Update"]]
        [:div.pr-2
         [button-input is-removable? ::events/remove-character "Exclude"]]]
       [create-list-with-radio ::subs/characters "Player's Characters"]]]]))