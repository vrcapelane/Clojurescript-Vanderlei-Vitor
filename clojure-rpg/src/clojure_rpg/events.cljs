(ns clojure-rpg.events
  (:require
   [re-frame.core :as re-frame]
   [clojure-rpg.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::update-form
 (fn [db [_ id val]]
   (assoc-in db [:form id] val)))

(re-frame/reg-event-db
 ::save-form
 (fn [db]
   (let [form-data (:form db)
         characters (get db :characters [])
         updated-characters (conj characters form-data)]
     (->
      db
      (assoc :characters updated-characters)
      (dissoc :form)))))

(re-frame/reg-event-db
 ::remove-character
 (fn [db]
   (let [form-data (:form db)
         char-data (:characters db)]
     (-> db
         (assoc :characters (vec (remove #(= (:character-name %) (:character-name form-data)) char-data)))))))