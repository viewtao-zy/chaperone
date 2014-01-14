(ns ^{:doc "Management of the application web sessions"}
    chaperone.web.session-test
    (:use [midje.sweet]
          [chaperone.web.session])
    (:require [test-helper :as test]))

(defn- setup!
    "Provides setup for the tests. Has side effects"
    []
    (test/stop)
    (test/create))

(namespace-state-changes (before :facts (setup!)))

(fact "Cookie should have an identified if it doesn't have one already"
      (-> {} manage-session-cookies :sid) => truthy
      (let [cookies (manage-session-cookies {})
            sid (:sid cookies)]
          (-> cookies manage-session-cookies :sid) => sid))