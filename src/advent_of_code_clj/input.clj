(ns advent-of-code-clj.input
  (:require [taoensso.nippy :as nippy]
            [babashka.http-client :as http])
  (:import [io.github.cdimascio.dotenv Dotenv]))

(def dotenv (.. (Dotenv/configure)
                ignoreIfMissing
                load))

(defn get-input [year day]
  (let [password (.get dotenv "INPUT_SALT")]
    (assert (some? password)
            "Failed to get password for decrypting inputs")
    (nippy/thaw-from-file
     (str "input/" year "/input" day ".nippy")
     {:password [:salted password]})))

(defn save-input [year day data]
  (let [password (.get dotenv "INPUT_SALT")]
    (nippy/freeze-to-file
     (str "input/" year "/input" day ".nippy")
     data
     {:password [:salted password]})))

(defn pull-input [year day]
  (->> (http/request {:method :get
                      :uri (str "https://adventofcode.com/" year "/day/" day "/input")
                      :headers {"cookie" (str "session=" (.get dotenv "SESSION"))}})
       :body
       (save-input year day)))

(comment
  (pull-input 2024 9))
