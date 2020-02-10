package com.github.stephenott.stix.taxii.controller

import io.micronaut.http.MediaType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "TaxiiMediaType", description = "A Media Type that is for TAXII content such as application/taxii+json;version=2.1")
interface TaxiiMediaType{
    companion object {
        const val APPLCATION_JSON_TAXII: String = "application/taxii+json"
        const val TAXII_VERSION_2_0: String = "2.0"
        const val TAXII_VERSION_2_1: String = "2.1"
        const val APPLCATION_JSON_TAXII_VERSION_2_0: String = "$APPLCATION_JSON_TAXII;version=$TAXII_VERSION_2_0"
        const val APPLCATION_JSON_TAXII_VERSION_2_1: String = "$APPLCATION_JSON_TAXII;version=$TAXII_VERSION_2_1"

        val taxii_2_1: TaxiiMediaType = TaxiiMedia(TAXII_VERSION_2_1)
        val taxii_2_0: TaxiiMediaType = TaxiiMedia(TAXII_VERSION_2_0)
    }
}
class TaxiiMedia(version: String): MediaType("${TaxiiMediaType.APPLCATION_JSON_TAXII};version=${version}"), TaxiiMediaType

@Schema(name = "StixMediaType", description = "A Media Type that is for STIX content such as application/stix+json;version=2.1")
interface StixMediaType{
    companion object{
        const val APPLCATION_JSON_STIX: String = "application/stix+json"
        const val STIX_VERSION_2_0: String = "2.0"
        const val STIX_VERSION_2_1: String = "2.1"
        const val APPLCATION_JSON_STIX_VERSION_2_0: String = "$APPLCATION_JSON_STIX;version=$STIX_VERSION_2_0"
        const val APPLCATION_JSON_STIX_VERSION_2_1: String = "$APPLCATION_JSON_STIX;version=$STIX_VERSION_2_1"

        val stix_2_1: StixMediaType = StixMedia(STIX_VERSION_2_1)
        val stix_2_0: StixMediaType = StixMedia(STIX_VERSION_2_0)
    }
}
class StixMedia(version: String): MediaType("${TaxiiMediaType.APPLCATION_JSON_TAXII};version=${version}"), StixMediaType




