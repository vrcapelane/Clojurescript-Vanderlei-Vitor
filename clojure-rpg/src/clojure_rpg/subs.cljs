(ns clojure-rpg.subs
  (:require
   [clojure.string :as s]
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::form
 (fn [db [_ id]]
   (get-in db [:form id] "")))

(re-frame/reg-sub
 ::characters
 (fn [db]
   (get db :characters [])))

(defn in? 
  [col val]
  (some #(= % val) col))

(defn not-in?
  [col val]
  (not (in? col val)))

(defn- has-values-and-not-blank?
  [db form-ids]
  (and (every? #(get-in db [:form %]) form-ids)
       (not (s/blank? (get-in db [:form :character-name])))))

(re-frame/reg-sub
 ::form-is-valid?
 (fn [db [_ form-ids]]
   (and (has-values-and-not-blank? db form-ids)
        (every? #(not-in? (get-in % [:character-name]) (get-in db [:form :character-name])) (get-in db [:characters])))))

(re-frame/reg-sub
 ::is-removable?
 (fn [db]
   (and (not (s/blank? (get-in db [:form :character-name])))
        (some #(= % (get-in db [:form :character-name])) (for [x (get-in db [:characters])] (x :character-name))))))

(re-frame/reg-sub
 ::is-updatable?
 (fn [db [_ form-ids]]
   (and (has-values-and-not-blank? db form-ids)
        (some #(= % (get-in db [:form :character-name])) (for [x (get-in db [:characters])] (x :character-name))))))