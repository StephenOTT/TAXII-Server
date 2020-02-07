package com.github.stephenott.stix.taxii.controller

object MediaTypes{
    const val APPLCATION_JSON_TAXII: String = "application/taxii+json"

    const val TAXII_VERSION_2_1: String = "2.1" //@TODO move into its own space
    const val TAXII_VERSION_2_0: String = "2.0" //@TODO move into its own space
    const val APPLICATION_JSON_TAXII_2_1: String = "$APPLCATION_JSON_TAXII;version=$TAXII_VERSION_2_1"
    const val APPLICATION_JSON_TAXII_VERSION_2_0: String = "$APPLCATION_JSON_TAXII;version=$TAXII_VERSION_2_0"


    const val APPLCATION_JSON_STIX: String = "application/stix+json"

    const val STIX_VERSION_2_1: String = "2.1" //@TODO move into its own space
    const val STIX_VERSION_2_0: String = "2.0" //@TODO move into its own space
    const val APPLICATION_JSON_STIX_VERSION_2_1: String = "$APPLCATION_JSON_STIX;version=$STIX_VERSION_2_1"
    const val APPLICATION_JSON_STIX_VERSION_2_0: String = "$APPLCATION_JSON_STIX;version=$STIX_VERSION_2_0"

}