;; --------------------------------------------------
;; practicalli.gameboard router
;;
;; Each section of the API is defined in its own routes namespace
;; with an accompanying handler namespace
;;
;; reitit - routing and middleware
;; ring - request / response management
;; muuntaja - coercion (data transformation)
;; --------------------------------------------------


(ns practicalli.gameboard.router
  "API global request routing"
  (:require
   ;; Core Web Application Libraries
   [reitit.ring   :as ring]
   [muuntaja.core :as muuntaja]

   ;; Routing middleware
   [reitit.ring.middleware.muuntaja   :as middleware-muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   ;; [reitit.ring.middleware.exception  :as exception]

   ;; Service middleware
   [practicalli.gameboard.middleware :as middleware-service]

   ;; Service Routing
   [practicalli.gameboard.api.system-admin :as system-admin]
   [practicalli.gameboard.api.scoreboard   :as scoreboard]

   ;; Self-documenting API
   [reitit.swagger    :as api-docs]
   [reitit.swagger-ui :as api-docs-ui]

   ;; Provide details of parameters to API documentation UI (swagger)
   [reitit.coercion.spec]
   [reitit.ring.coercion :as coercion]

   ;; Error handling
   [reitit.dev.pretty :as pretty]
   [com.brunobonacci.mulog :as mulog]  ; Event Logging
   ))

;; --------------------------------------------------
;; Open API documentation endpoint

(def open-api-docs
  "Open API docs general information about the service,
  https://practical.li/clojure-web-services/project/gameboard/
  keys composed of multiple names should use camelCase"
  ["/swagger.json"
   {:get {:no-doc  true
          :swagger {:info {:title "practicalli gameboard Service API"
                           :description "TODO: Provide a meaningful description of the project"
                           :version "0.1.0"
                           :termsOfService "https://net.clojars.practicalli/"
                           :contact {:name "Engineering Team"
                                     :url "https://net.clojars.practicalli/wiki/engineering-team"}
                           :license {:name "© Practicalli, 2024"
                                     :url "http://creativecommons.org/licenses/by-sa/4.0/"}
                           :x-logo {:url "./favicon.png"}}}
          :handler (api-docs/create-swagger-handler)}}])
;; --------------------------------------------------


;; --------------------------------------------------
;; Global route Configuration
;; - coersion and middleware applied to all routes

(def router-configuration
  "Reitit configuration of coercion, data format transformation and middleware for all routing"
  {:data {:coercion   reitit.coercion.spec/coercion
          :muuntaja   muuntaja/instance
          :middleware [;; swagger feature for OpenAPI documentation
                       api-docs/swagger-feature
                       ;; query-params & form-params
                       parameters/parameters-middleware
                       ;; content-negotiation
                       middleware-muuntaja/format-middleware
                       ;; coercing response bodys
                       coercion/coerce-response-middleware
                       ;; coercing request parameters
                       coercion/coerce-request-middleware
                       ;; Pretty print exceptions
                       coercion/coerce-exceptions-middleware
                       ;; logging with mulog
                       [middleware-service/wrap-trace-events :trace-events]]}
   ;; pretty-print reitit exceptions for human consumptions
   :exception pretty/exception})

;; --------------------------------------------------
;; Routing

(defn app
  "Router for all requests to the Gameboard and OpenAPI documentation,
  using `ring-handler` to manage HTTP request and responses.
  Arguments: `system-config containt Integrant configuration for the running system
  including persistence connection to store and retrieve data"
  [system-config]

  (mulog/log ::router-app :system-config system-config)

  (ring/ring-handler
   (ring/router
    [;; --------------------------------------------------
     ;; All routing for Gameboard service

     ;; OpenAPI Documentation routes
     open-api-docs

     ;; --------------------------------------------------
     ;; System routes & Status
     ;; - `/system-admin/status` for simple service healthcheck
     (system-admin/routes)

     ;; --------------------------------------------------
     ;; practicalli gameboard  API routes
     ["/api"
      ["/v1"
       (scoreboard/routes system-config)]]]

    ;; End of All routing for Gameboard service
    ;; --------------------------------------------------

    ;; --------------------------------------------------
    ;; Router configuration
    ;; - middleware, coersion & content negotiation
    router-configuration)

   ;; --------------------------------------------------
   ;; Default routes
   (ring/routes
    ;; Open API documentation as default route
    (api-docs-ui/create-swagger-ui-handler {:path "/"})

    ;; Respond to any other route - returns blank page
    ;; TODO: create page template for routes not recognised
    (ring/create-default-handler))))
