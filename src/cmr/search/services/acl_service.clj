(ns cmr.search.services.acl-service
  "Performs ACL related tasks for the search application"
  (:require [cmr.search.services.acls.acl-helper :as acl-helper]))

(defmulti add-acl-conditions-to-query
  "Adds conditions to the query to enforce ACLs."
  (fn [context query]
    (:concept-type query)))

(defmulti acls-match-concept?
  "Returns true if any of the acls match the concept."
  (fn [context acls concept]
    (:concept-type concept)))

(def concept-type->applicable-field
  "A mapping of concept type to the field in the ACL indicating if it is collection or granule applicable."
  {:granule :granule-applicable
   :collection :collection-applicable})

(defn filter-concepts
  "Filters out the concepts that the current user does not have access to. Concepts are the maps
  of concept metadata as returned by the metadata db."
  [context concepts]
  (when (seq concepts)
    (let [acls (acl-helper/get-acls-applicable-to-token context)
          applicable-field (-> concepts first :concept-type concept-type->applicable-field)
          applicable-acls (filter (comp applicable-field :catalog-item-identity) acls)]
      (filter (partial acls-match-concept? context applicable-acls) concepts))))


