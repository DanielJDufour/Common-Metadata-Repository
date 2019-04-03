(ns cmr.umm-spec.umm-to-xml-mappings.iso-shared.archive-and-dist-info
  (:require
   [clojure.string :as string]
   [cmr.common.xml.gen :refer :all]
   [cmr.umm-spec.iso19115-2-util :as iso]
   [cmr.umm-spec.util :refer [char-string]]))

(defn generate-specification-string
  "Parse FileArchiveInformation values out of the specification string."
  [archive]
  (let [{:keys [FormatType AverageFileSize AverageFileSizeUnit TotalCollectionFileSize
                TotalCollectionFileSizeUnit Description]} archive
        format-str (when FormatType
                     (str "FormatType: " FormatType " "))
        avg-file-size-str (when AverageFileSize
                            (str "AverageFileSize: " AverageFileSize " "))
        avg-file-size-unit-str (when AverageFileSizeUnit
                                 (str "AverageFileSizeUnit: " AverageFileSizeUnit " "))
        col-file-size-str (when TotalCollectionFileSize
                            (str "TotalCollectionFileSize: " TotalCollectionFileSize " "))
        col-file-size-unit-str (when TotalCollectionFileSizeUnit
                                 (str "TotalCollectionFileSizeUnit: " TotalCollectionFileSizeUnit " "))
        description-str (when Description
                          (str "Description: " Description " "))]
    (when (or format-str avg-file-size-str avg-file-size-unit-str col-file-size-str
              col-file-size-unit-str description-str)
     (string/trim (str format-str avg-file-size-str avg-file-size-unit-str
                       col-file-size-str col-file-size-unit-str description-str)))))

(defn generate-file-archive-info
  "Generate XML for each FileArchiveInformation in ArchiveAndDistributionInformation."
  [c]
  (for [archive (get-in c [:ArchiveAndDistributionInformation :FileArchiveInformation])
        :let [specification (generate-specification-string archive)
              format (:Format archive)]]
    [:gmd:resourceFormat
     [:gmd:MD_Format
      [:gmd:name (char-string format)]
      [:gmd:version {:gco:nilReason "unknown"}]
      [:gmd:specification (char-string specification)]]]))

(defn- generate-format
  "Generate Format and FormatType xml for FileDistributionInformation.
   The xlink:href is used to associate FileDistributionInformation using the block-id."
  [indexed-dist]
  (let [[id dist] indexed-dist
        {:keys [Format FormatType]} dist]
    (when (or Format FormatType)
      [:gmd:distributionFormat {:xlink:href (str "FileDistributionInformation_" id)}
       [:gmd:MD_Format
        [:gmd:name
         (char-string (or Format ""))]
        [:gmd:version {:gco:nilReason "unknown"}]
        [:gmd:specification
         (char-string (or FormatType ""))]]])))

(defn- generate-distributor
  "Generate Fees and Description xml for FileDistributionInformation.
   The xlink:href is used to associate FileDistributionInformation using the block-id."
  [indexed-dist]
  (let [[id dist] indexed-dist
        {:keys [Fees Description]} dist]
    (when (or Fees Description)
      [:gmd:distributor {:xlink:href (str "FileDistributionInformation_" id)}
       [:gmd:MD_Distributor
        [:gmd:distributorContact {:gco:nilReason "unknown"}]
        [:gmd:distributionOrderProcess
         [:gmd:MD_StandardOrderProcess
          [:gmd:fees
           (char-string (or Fees ""))]
          [:gmd:orderingInstructions
           (char-string (or Description ""))]]]]])))

(defn- generate-average-file-size
  "Generate AverageFileSize and AverageFileSizeUnit xml for FileDistributionInformation.
   The xlink:href is used to associate FileDistributionInformation using the block-id."
  [indexed-dist]
  (let [[id dist] indexed-dist
        {:keys [AverageFileSize AverageFileSizeUnit]} dist]
    (when AverageFileSize
      [:gmd:transferOptions {:xlink:href (str "FileDistributionInformation_AverageFileSize_" id)}
       [:gmd:MD_DigitalTransferOptions
        [:gmd:unitsOfDistribution
         (char-string AverageFileSizeUnit)]
        [:gmd:transferSize
         [:gco:Real AverageFileSize]]]])))

(defn- generate-total-coll-size
  "Generate TotalCollectionFileSize and TotalCollectionFileSizeUnit xml for FileDistributionInformation.
   The xlink:href is used to associate FileDistributionInformation using the block-id."
  [indexed-dist]
  (let [[id dist] indexed-dist
        {:keys [TotalCollectionFileSize
                TotalCollectionFileSizeUnit]} dist]
    (when TotalCollectionFileSize
      [:gmd:transferOptions {:xlink:href (str "FileDistributionInformation_TotalCollectionFileSize_" id)}
       [:gmd:MD_DigitalTransferOptions
        [:gmd:unitsOfDistribution
         (char-string TotalCollectionFileSizeUnit)]
        [:gmd:transferSize
         [:gco:Real TotalCollectionFileSize]]]])))

(defn- generate-media
  "Generate Media for FileDistributionInformation.
   The xlink:href is used to associate FileDistributionInformation using the block-id."
  [indexed-dist]
  (let [[id dist] indexed-dist
        {:keys [Media]} dist]
    (when Media
      [:gmd:transferOptions {:xlink:href (str "FileDistributionInformation_Media_" id)}
       [:gmd:MD_DigitalTransferOptions
        [:gmd:offLine
         [:gmd:MD_Medium
          [:gmd:name
           [:gmd:MD_MediumNameCode
            {:codeList "https://cdn.earthdata.nasa.gov/iso/resources/Codelist/gmxCodelists.xml#MD_MediumNameCode"
             :codeListValue (first Media)} (first Media)]]]]]])))

(defn generate-file-dist-info-formats
  [c]
  (map generate-format
       (map-indexed vector (get-in c [:ArchiveAndDistributionInformation :FileDistributionInformation]))))

(defn generate-file-dist-info-average-file-sizes
  [c]
  (map generate-average-file-size
       (map-indexed vector (get-in c [:ArchiveAndDistributionInformation :FileDistributionInformation]))))

(defn generate-file-dist-info-total-coll-sizes
  [c]
  (map generate-total-coll-size
       (map-indexed vector (get-in c [:ArchiveAndDistributionInformation :FileDistributionInformation]))))

(defn generate-file-dist-info-medias
  [c]
  (map generate-media
       (map-indexed vector (get-in c [:ArchiveAndDistributionInformation :FileDistributionInformation]))))

(defn generate-file-dist-info-distributors
  [c]
  (map generate-distributor
       (map-indexed vector (get-in c [:ArchiveAndDistributionInformation :FileDistributionInformation]))))
