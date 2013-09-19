(ns	^{:doc "Actually runs the application server."}
	chaperone.core
	(:require [chaperone.persistence.core :as persist]
			  [chaperone.web.core :as web])
	(:gen-class))

(defn create-system
	"Create the system context, but don't start it"
	[]
	(let [context {}]
		(-> context persist/create-sub-system web/create-sub-system)))

(defn start
	"Starts the system"
	[system]
	(-> system persist/start))

(defn -main
	"I don't do a whole lot ... yet."
	[& args]
	(println "Hello, World!" args))