;; --------------------------------------------------
;; System Administration and Status check
;;
;; - return service status response
;; --------------------------------------------------


(ns practicalli.gameboard.api.system-admin
  "Gameboard API system administration handlers"
  (:require [ring.util.response :refer [response]]))


;; --------------------------------------------------
;; Status of Service

(def status
  "Simple status report for external monitoring services, e.g. Pingdom
  Return:
  - `constantly` returns an anonymous function that returns a ring response hash-map"
  (constantly (response {:application "practicalli gameboard Service" :status "Alive"})))

;; --------------------------------------------------


;; --------------------------------------------------
;; Router

(defn routes
  "Reitit route configuration for system-admin endpoint"
  []
  ["/system-admin"
   {:swagger {:tags ["Application Support"]}}
   ["/status"
    {:get {:summary "Status of practicalli gameboard service"
           :description "Ping practicalli gameboard service to see if is responding to a simple request and therefore alive"
           :responses {200 {:body {:application string? :status string?}}}
           :handler status}}]])

;; --------------------------------------------------
