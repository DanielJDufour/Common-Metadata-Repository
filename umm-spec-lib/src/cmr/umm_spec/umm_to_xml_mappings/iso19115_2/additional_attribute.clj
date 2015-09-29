(ns cmr.umm-spec.umm-to-xml-mappings.iso19115-2.additional-attribute
  "Functions for generating ISO19115-2 XML elements from UMM additional attribute records."
  (:require [cmr.umm-spec.xml.gen :refer :all]
            [cmr.umm-spec.iso19115-2-util :as iso]))

(def additional-attr-name->type
  "Defines the additional attribute name to type mapping for ISO19115-2"
  {"AcquisitionDate" "citation.date"
   "GenerationDateandTime" "citation.date"
   "LatestAcquisitionDate" "citation.date"
   "EarliestAcquisitionDate" "citation.date"
   "SEQUENCE" "citation.identifier"
   "FRAME_ID" "citation.identifier"
   "GRANULENUMBER" "citation.identifier"
   "MAX_FRAME_ID" "citation.identifier"
   "MIN_FRAME_ID" "citation.identifier"
   "OrderingId" "citation.identifier"
   "ASTERGRANULEID" "citation.identifier"
   "FileNumber" "citation.identifier"
   "ScienceTeamWebSite" "citation.pointOfContact"
   "QAPERCENTGOODQUALITY" "contentInformation"
   "QAPERCENTOTHERQUALITY" "contentInformation"
   "QAPERCENTNOTPRODUCEDCLOUD" "contentInformation"
   "QAPERCENTNOTPRODUCEDOTHER" "contentInformation"
   "ClearPct250m" "contentInformation"
   "CloudCoverPct250m" "contentInformation"
   "HighConfidentClearPct" "contentInformation"
   "LowConfidentClearPct" "contentInformation"
   "SuccessCloudOptPropRtrPct_VIS" "contentInformation"
   "SuccessCloudPhaseRtrPct_IR" "contentInformation"
   "SuccessCloudTopPropRtrPct_IR" "contentInformation"
   "SuccessfulRetrievalPct" "contentInformation"
   "SuccessfulRetrievalPct_IR" "contentInformation"
   "SuccessfulRetrievalPct_Land" "contentInformation"
   "SuccessfulRetrievalPct_NIR" "contentInformation"
   "SuccessfulRetrievalPct_Ocean" "contentInformation"
   "VeryHighConfidentClearPct" "contentInformation"
   "CloudCoverFractionPct_VIS" "contentInformation"
   "IceCloudDetectedPct_VIS" "contentInformation"
   "PercentGroundHit" "contentInformation"
   "SNOWCOVERPERCENT" "contentInformation"
   "AVERAGENUMBEROBS" "contentInformation"
   "MOPEventDesc" "contentInformation"
   "MOPHotCalPost1" "contentInformation"
   "MOPHotCalPost2" "contentInformation"
   "MOPHotCalPost3" "contentInformation"
   "MOPHotCalPost4" "contentInformation"
   "MOPHotCalPre1" "contentInformation"
   "MOPHotCalPre2" "contentInformation"
   "MOPHotCalPre3" "contentInformation"
   "MOPHotCalPre4" "contentInformation"
   "PERCENTLANDINTILE" "contentInformation"
   "PERCENTNEWBRDFS" "contentInformation"
   "PERCENTPROCESSEDINTILE" "contentInformation"
   "PERCENTSHAPEFIXEDBRDFS" "contentInformation"
   "QAPERCENTPOOROUTPUT250MBAND1" "contentInformation"
   "QAPERCENTPOOROUTPUT250MBAND2" "contentInformation"
   "QAPERCENTPOOROUTPUT500MBAND3" "contentInformation"
   "QAPERCENTPOOROUTPUT500MBAND4" "contentInformation"
   "QAPERCENTPOOROUTPUT500MBAND5" "contentInformation"
   "QAPERCENTPOOROUTPUT500MBAND6" "contentInformation"
   "QAPERCENTPOOROUTPUT500MBAND7" "contentInformation"
   "FIREPIXELS" "contentInformation"
   "SEAICEPERCENT" "contentInformation"
   "MOPCldTopPrDiff" "contentInformation"
   "MOPConvIterAvg" "contentInformation"
   "MOPConvIterMax" "contentInformation"
   "MOPFGRad5DiffThr" "contentInformation"
   "MOPMaxCO" "contentInformation"
   "MOPMaxCOMixRat150" "contentInformation"
   "MOPMaxCOMixRat250" "contentInformation"
   "MOPMaxCOMixRat350" "contentInformation"
   "MOPMaxCOMixRat500" "contentInformation"
   "MOPMaxCOMixRat700" "contentInformation"
   "MOPMaxCOMixRat850" "contentInformation"
   "MOPMaxCOMixRatSfc" "contentInformation"
   "MOPMinCO" "contentInformation"
   "MOPMinCOMixRat150" "contentInformation"
   "MOPMinCOMixRat250" "contentInformation"
   "MOPMinCOMixRat350" "contentInformation"
   "MOPMinCOMixRat500" "contentInformation"
   "MOPMinCOMixRat700" "contentInformation"
   "MOPMinCOMixRat850" "contentInformation"
   "MOPMinCOMixRatSfc" "contentInformation"
   "MOPNStarD" "contentInformation"
   "MOPNStarN" "contentInformation"
   "MOPOvrcstOpacity" "contentInformation"
   "MOPOvrcstUniformity" "contentInformation"
   "MOPPercentCloudCleared" "contentInformation"
   "MOPRad5FGRatioThr" "contentInformation"
   "MOPRad5FGRatioThr2" "contentInformation"
   "MOPRad5FGRatioThrN" "contentInformation"
   "MOPRad6FGDiffThr" "contentInformation"
   "MOPRad6FGRatioThr" "contentInformation"
   "MOPUncertCOSfc" "contentInformation"
   "EVICMG16DAYQCLASSPERCENTAGE" "contentInformation"
   "LowerLeftQuadCloudCoverage" "contentInformation"
   "LowerRightQuadCloudCoverage" "contentInformation"
   "NDVICMG16DAYQCLASSPERCENTAGE" "contentInformation"
   "NUMBERGRIDCELLSCONTAININGFIRE" "contentInformation"
   "QAPERCENTEMPIRICALMODEL" "contentInformation"
   "QAPERCENTGOODFPAR" "contentInformation"
   "QAPERCENTGOODLAI" "contentInformation"
   "QAPERCENTGOODNPP" "contentInformation"
   "QAPERCENTGOODPSN" "contentInformation"
   "QAPERCENTMAINMETHOD" "contentInformation"
   "QAPERCENTPOOROUTPUT500MBAND1" "contentInformation"
   "QAPERCENTPOOROUTPUT500MBAND2" "contentInformation"
   "SceneCloudCoverage" "contentInformation"
   "UpperLeftQuadCloudCoverage" "contentInformation"
   "UpperRightQuadCloudCoverage" "contentInformation"
   "CloudCoverQuadLowerLeft" "contentInformation"
   "CloudCoverQuadLowerRight" "contentInformation"
   "CloudCoverQuadUpperLeft" "contentInformation"
   "CloudCoverQuadUpperRight" "contentInformation"
   "MOPExtraStare" "contentInformation"
   "EVI1KM16DAYQCLASSPERCENTAGE" "contentInformation"
   "EVI1KMMONTHQCLASSPERCENTAGE" "contentInformation"
   "EVI250M16DAYQCLASSPERCENTAGE" "contentInformation"
   "EVI500M16DAYQCLASSPERCENTAGE" "contentInformation"
   "EVICMGMONTHQCLASSPERCENTAGE" "contentInformation"
   "NDVI1KM16DAYQCLASSPERCENTAGE" "contentInformation"
   "NDVI1KMMONTHQCLASSPERCENTAGE" "contentInformation"
   "NDVI250M16DAYQCLASSPERCENTAGE" "contentInformation"
   "NDVI500M16DAYQCLASSPERCENTAGE" "contentInformation"
   "NDVICMGMONTHQCLASSPERCENTAGE" "contentInformation"
   "Percent1064to532" "contentInformation"
   "PercentFullRate" "contentInformation"
   "PercentHighRate" "contentInformation"
   "PercentLowRate" "contentInformation"
   "PercentMediumRate" "contentInformation"
   "MOPMaxCH4B1" "contentInformation"
   "MOPMaxCH4B2" "contentInformation"
   "MOPMinCH4B1" "contentInformation"
   "MOPMinCH4B2" "contentInformation"
   "MOPUncertCH4" "contentInformation"
   "PERCENTBARE" "contentInformation"
   "PERCENTBAREGROUND" "contentInformation"
   "PERCENTBROADLEAF" "contentInformation"
   "PERCENTCHANGEDPIXELS" "contentInformation"
   "PERCENTCROPS" "contentInformation"
   "PERCENTDECIDUOUS" "contentInformation"
   "PERCENTEVERGREEN" "contentInformation"
   "PERCENTICE" "contentInformation"
   "PercentLandData" "contentInformation"
   "PERCENTNEEDLELEAF" "contentInformation"
   "PERCENTNONTREEVEGETATION" "contentInformation"
   "PERCENTOTHERHERBACEOUS" "contentInformation"
   "PercentRetrievals" "contentInformation"
   "PercentRetrievalsWithinBounds" "contentInformation"
   "PERCENTSHRUBS" "contentInformation"
   "PERCENTTREECOVER" "contentInformation"
   "ASCENDING_DESCENDING" "descriptiveKeyword"
   "AscendingDescendingFlg" "descriptiveKeyword"
   "OPeNDAPServer" "distribution.url"
   "DigitalFileSize" "distributionInformation"
   "ArchiveLocation" "distributionInformation"
   "HORIZONTALTILENUMBER" "geographicIdentifier"
   "TileID" "geographicIdentifier"
   "VERTICALTILENUMBER" "geographicIdentifier"
   "Cycle" "geographicIdentifier"
   "Instance" "geographicIdentifier"
   "ReferenceOrbit" "geographicIdentifier"
   "Track" "geographicIdentifier"
   "EndingPolygonNumber" "geographicIdentifier"
   "NominalPassIndex" "geographicIdentifier"
   "StartingPolygonNumber" "geographicIdentifier"
   "Track_Segment" "geographicIdentifier"
   "AIRSAR_FLIGHT_LINE" "geographicIdentifier"
   "Path" "geographicIdentifier"
   "CENTER_FRAME_ID" "geographicIdentifier"
   "PATH_NUMBER" "geographicIdentifier"
   "FlightPath" "geographicIdentifier"
   "SP_AM_PATH_NO" "geographicIdentifier"
   "SP_ICE_GLAS_EndBlock" "geographicIdentifier"
   "SP_ICE_GLAS_StartBlock" "geographicIdentifier"
   "SP_ICE_PATH_NO" "geographicIdentifier"
   "SP_AM_MISR_EndBlock" "geographicIdentifier"
   "SP_AM_MISR_StartBlock" "geographicIdentifier"
   "Row" "geographicIdentifier"
   "WRSPath" "geographicIdentifier"
   "WRSRow" "geographicIdentifier"
   "RowCount" "geographicIdentifier"
   "RowStart" "geographicIdentifier"
   "Instrument_State" "instrumentInformation"
   "Instrument_State_Date" "instrumentInformation"
   "Instrument_State_Time" "instrumentInformation"
   "VNIR1_ObservationMode" "instrumentInformation"
   "VNIR2_ObservationMode" "instrumentInformation"
   "GainBand1" "instrumentInformation"
   "GainBand2" "instrumentInformation"
   "GainBand3" "instrumentInformation"
   "GainBand4" "instrumentInformation"
   "GainBand5" "instrumentInformation"
   "GainBand7" "instrumentInformation"
   "GainBand8" "instrumentInformation"
   "GainBand6" "instrumentInformation"
   "GainBand6Vcid1" "instrumentInformation"
   "GainBand6Vcid2" "instrumentInformation"
   "GainChangeBand1" "instrumentInformation"
   "GainChangeBand2" "instrumentInformation"
   "GainChangeBand3" "instrumentInformation"
   "GainChangeBand4" "instrumentInformation"
   "GainChangeBand5" "instrumentInformation"
   "GainChangeBand6Vcid1" "instrumentInformation"
   "GainChangeBand6Vcid2" "instrumentInformation"
   "GainChangeBand7" "instrumentInformation"
   "GainChangeBand8" "instrumentInformation"
   "ALBEDOFILEID" "lineage"
   "BRDFCODEID" "lineage"
   "BRDFDATABASEVERSION" "lineage"
   "ClimateSeedSource" "lineage"
   "INPUTPRODUCTRESOLUTION" "lineage"
   "PDS_ID" "lineage"
   "INPUTFILERESOLUTION" "lineage"
   "PROCESSVERSION" "processingInformation"
   "Timing_Bias" "processingInformation"
   "Timing_Bias_Date" "processingInformation"
   "Timing_Bias_Time" "processingInformation"
   "Timing_Drift" "processingInformation"
   "Timing_Drift_Date" "processingInformation"
   "Timing_Drift_Time" "processingInformation"
   "Range_Bias" "processingInformation"
   "Range_Bias_Date" "processingInformation"
   "Range_Bias_Time" "processingInformation"
   "ASTERProcessingCenter" "processStep"
   "QAFRACTIONGOODQUALITY" "qualityInformation"
   "QAFRACTIONNOTPRODUCEDCLOUD" "qualityInformation"
   "QAFRACTIONNOTPRODUCEDOTHER" "qualityInformation"
   "QAFRACTIONOTHERQUALITY" "qualityInformation"
   "OrbitQuality" "qualityInformation"
   "NDAYS_COMPOSITED" "qualityInformation"
   "PERCENTSUBSTITUTEBRDFS" "qualityInformation"
   "Band1_Available" "qualityInformation"
   "Band10_Available" "qualityInformation"
   "Band11_Available" "qualityInformation"
   "Band12_Available" "qualityInformation"
   "Band13_Available" "qualityInformation"
   "Band14_Available" "qualityInformation"
   "Band2_Available" "qualityInformation"
   "Band3B_Available" "qualityInformation"
   "Band3N_Available" "qualityInformation"
   "Band4_Available" "qualityInformation"
   "Band5_Available" "qualityInformation"
   "Band6_Available" "qualityInformation"
   "Band7_Available" "qualityInformation"
   "Band8_Available" "qualityInformation"
   "Band9_Available" "qualityInformation"
   "AquisitionQuality" "qualityInformation"
   "QualityBand1" "qualityInformation"
   "QualityBand2" "qualityInformation"
   "QualityBand3" "qualityInformation"
   "QualityBand4" "qualityInformation"
   "QualityBand5" "qualityInformation"
   "QualityBand6" "qualityInformation"
   "QualityBand7" "qualityInformation"
   "Band6Missing" "qualityInformation"
   "ImageQualityVcid1" "qualityInformation"
   "ImageQualityVcid2" "qualityInformation"
   "QualityBand8" "qualityInformation"
   "FileFormat" "resourceFormat"
   "identifier_product_doi" "citation.identifier"
   "identifier_product_doi_authority" "citation.identifier"})

(defn group-by-iso-type
  "Returns the partitioned additional attributes as a map of iso types to additional attributes."
  [aas]
  (group-by (fn [aa]
              (additional-attr-name->type (:Name aa)))
            aas))

(defn generate-content-info
  "Returns the content generator instructions for generating ISO19115 contentInformation additional
  attributes for the given list of additional attributes."
  [aas]
  (when (seq aas)
    [:gmd:contentInfo
     [:gmd:MD_CoverageDescription
      [:gmd:attributeDescription {:gco:nilReason "missing"}]
      [:gmd:contentType
       [:gmd:MD_CoverageContentTypeCode
        {:codeList (str (:ngdc iso/code-lists) "#MD_CoverageContentTypeCode")
         :codeListValue "physicalMeasurement"} "physicalMeasurement"]]
      [:gmd:dimension
       [:gmd:MD_Band
        iso/gmd-echo-attributes-info
        [:gmd:otherProperty
         [:gco:Record
          [:eos:AdditionalAttributes
           (for [aa aas]
             [:eos:AdditionalAttribute
              [:eos:reference
               [:eos:EOS_AdditionalAttributeDescription
                [:eos:type
                 [:eos:EOS_AdditionalAttributeTypeCode
                  {:codeList (str (:earthdata iso/code-lists) "#EOS_AdditionalAttributeTypeCode")
                   :codeListValue "contentInformation"} "contentInformation"]]
                (when-let [group (:Group aa)]
                  [:eos:identifier
                   [:gmd:MD_Identifier
                    [:gmd:code
                     (char-string group)]]])
                [:eos:name
                 (char-string (:Name aa))]
                [:eos:description
                 (char-string (:Description aa))]
                [:eos:dataType
                 [:eos:EOS_AdditionalAttributeDataTypeCode
                  {:codeList (str (:earthdata iso/code-lists) "#EOS_AdditionalAttributeDataTypeCode")
                   :codeListValue (:DataType aa)} (:DataType aa)]]
                [:eos:measurementResolution
                 (char-string (:MeasurementResolution aa))]
                [:eos:parameterRangeBegin
                 (char-string (:ParameterRangeBegin aa))]
                [:eos:parameterRangeEnd
                 (char-string (:ParameterRangeEnd aa))]
                [:eos:parameterUnitsOfMeasure
                 (char-string (:ParameterUnitsOfMeasure aa))]
                [:eos:parameterValueAccuracy
                 (char-string (:ParameterValueAccuracy aa))]
                [:eos:valueAccuracyExplanation
                 (char-string (:ValueAccuracyExplanation aa))]]]
              (when-let [value (:Value aa)]
                [:eos:value
                 (char-string value)])])]]]]]]]))


