(ns ^{:doc "Management of the application web sessions"}
    chaperone.web.session
    (:require [cljs-uuid.core :as uuid]
              [chaperone.user :as user]
              [chaperone.persistence.core :as pcore]))

;;; system tools
(defn create-sub-system
    "Create the persistence system. Takes the existing system details"
    [system]
    (let [sub-system {:websocket-clients (atom {})
                      :loggedin-users    (atom {})}]
        (assoc system :session sub-system)))

(defn sub-system
    "get the web system from the global"
    [system]
    (:session system))

(defn manage-session-cookies
    "Pass in the map for cookies, and passes back what is needed for session management"
    [cookies]
    (if (:sid cookies)
        cookies
        (assoc cookies :sid (uuid/make-random-string))))

(defn open-session!
    "starts a session for a websocket connection"
    [session cookies client]
    (let [sid (if (:sid cookies)
                  (:sid cookies)
                  (get-in cookies ["sid" :value]))]
        (if-not sid
            (throw (Exception. "SID not present in cookie")))
        (swap! (:websocket-clients session) assoc client sid)))

(defn close-session!
    "Closes an existing session"
    [session client]
    (swap! (:websocket-clients session) dissoc client))

(defn login!
    "Login to the system. Returns the user data if successful. nil if not."
    [system sid email password]
    (let [session (sub-system system)
          persistence (pcore/sub-system system)
          user (user/get-user-by-email persistence email)]
        (if (user/verify-user-password user password)
            (do (swap! (:loggedin-users session) assoc sid user)
                user)
            nil)))

(defn logout!
    "Logout the specific session."
    [session sid]
    (swap! (:loggedin-users session) dissoc sid))

(defn get-client-sid
    "gets the sid for a client"
    [session client]
    (-> session :websocket-clients (get client)))

(defn get-user-session
    "gets the user's session from a session ident"
    [session sid]
    (-> session :loggedin-users (get sid)))