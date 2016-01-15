(ns cmr.access-control.services.group-service
    "Provides functions for creating, updating, deleting, retrieving, and finding groups."
    (:require [cmr.transmit.metadata-db2 :as mdb]
              [cmr.transmit.echo.tokens :as tokens]
              [cmr.common.concepts :as concepts]
              [cmr.common.services.errors :as errors]
              [cmr.common.mime-types :as mt]
              [cmr.common.validations.core :as v]
              [cmr.access-control.services.group-service-messages :as msg]
              [clojure.edn :as edn]))

(defn- context->user-id
  "Returns user id of the token in the context. Throws an error if no token is provided"
  [context]
  (if-let [token (:token context)]
    (tokens/get-user-id context (:token context))
    (errors/throw-service-error :unauthorized msg/token-required-for-group-modification)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Metadata DB Concept Map Manipulation

(defn- group->mdb-provider-id
  "Returns the provider id to use in metadata db for the group"
  [group]
  (get group :provider-id "CMR"))

(defn- group->new-concept
  "Converts a group into a new concept that can be persisted in metadata db."
  [context group]
  {:concept-type :access-group
   :native-id (:name group)
   ;; Provider id is optional in group. If it is a system level group then it's owned by the CMR.
   :provider-id (group->mdb-provider-id group)
   :metadata (pr-str group)
   :user-id (context->user-id context)
   ;; The first version of a group should always be revision id 1. We always specify a revision id
   ;; when saving groups to help avoid conflicts
   :revision-id 1
   :format mt/edn})

(defn- fetch-group-concept
  "Fetches the latest version of a group concept by concept id"
  [context concept-id]
  (let [{:keys [concept-type provider-id]} (concepts/parse-concept-id concept-id)]
    (when (not= :access-group concept-type)
      (errors/throw-service-error :bad-request (msg/bad-group-concept-id concept-id))))

  (if-let [concept (mdb/get-latest-concept context concept-id false)]
    (if (:deleted concept)
      (errors/throw-service-error :not-found (msg/group-deleted concept-id))
      concept)
    (errors/throw-service-error :not-found (msg/group-does-not-exist concept-id))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Validations

(defn validate-group-provider-exists
  "Validates that the groups provider exists."
  [context {:keys [provider-id]}]
  (when (and provider-id
             (not (some #{provider-id} (map :provider-id (mdb/get-providers context)))))
    (errors/throw-service-error :not-found (msg/provider-does-not-exist provider-id))))

(def ^:private update-group-validations
  "Service level validations when updating a group."
  [(v/field-cannot-be-changed :name)
   (v/field-cannot-be-changed :provider-id)
   (v/field-cannot-be-changed :legacy-guid)])

(defn- validate-update-group
  "Validates a group update."
  [existing-group updated-group]
  (v/validate! update-group-validations (assoc updated-group :existing existing-group)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Service level functions

(defn create-group
  "Creates the group by saving it to Metadata DB. Returns a map of the concept id and revision id of
  the created group."
  [context group]
  (validate-group-provider-exists context group)
  ;; Check if the group already exists
  (if-let [concept-id (mdb/get-concept-id context
                                          :access-group
                                          (group->mdb-provider-id group)
                                          (:name group))]

    ;; The group exists. Check if its latest revision is a tombstone
    (let [concept (mdb/get-latest-concept context concept-id)]
      (if (:deleted concept)
        ;; The group exists but was previously deleted.
        (mdb/save-concept
         context
         (-> concept
             (assoc :metadata (pr-str group)
                    :deleted false
                    :user-id (context->user-id context))
             (dissoc :revision-date)
             (update-in [:revision-id] inc)))

        ;; The group exists and was not deleted. Reject this.
        (errors/throw-service-error :conflict (msg/group-already-exists group concept-id))))

    ;; The group doesn't exist
    (mdb/save-concept context (group->new-concept context group))))

(defn update-group
  "Updates an existing group with the given concept id"
  [context concept-id updated-group]
  (let [existing-concept (fetch-group-concept context concept-id)
        existing-group (edn/read-string (:metadata existing-concept))]
    (validate-update-group existing-group updated-group)
    (mdb/save-concept
      context
      (-> existing-concept
          (assoc :metadata (pr-str updated-group)
                 :user-id (context->user-id context))
          (dissoc :revision-date)
          (update-in [:revision-id] inc)))))

(defn get-group
  "Retrieves a group with the given concept id."
  [context concept-id]
  (edn/read-string (:metadata (fetch-group-concept context concept-id))))

(defn delete-group
  "Deletes a group with the given concept id"
  [context concept-id]
  (let [existing-concept (fetch-group-concept context concept-id)]
    (mdb/save-concept
      context
      (-> existing-concept
          ;; Remove fields not allowed when creating a tombstone.
          (dissoc :metadata :format :provider-id :native-id)
          (assoc :deleted true
                 :user-id (context->user-id context))
          (dissoc :revision-date)
          (update-in [:revision-id] inc)))))
