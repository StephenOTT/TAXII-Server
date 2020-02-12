package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.stephenott.stix.taxii.controller.TaxiiMediaType
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "api-root", description = "The \u200Bapi-root\u200B resource contains general information about the API Root, such as a human-readable title\u200B and \u200Bdescription\u200B, the TAXII \u200Bversions\u200B it supports, and the maximum size (\u200Bmax_content_length\u200B) of the content body it will accept in a PUT or POST request.")
data class ApiRoot(

        @field:Schema(description = "A human readable plain text name used to identify this API instance.")
        val title: String,

        @field:Schema(description = "A human readable plain text description for this API Root.")
        val description: String? = null,

        @field:ArraySchema(
                arraySchema = Schema(description = "The list of TAXII versions that this API Root is compatible with. The values listed in this property ​MUST match the media types defined in Section​ 1​ .6.8.1​ and MUST​ include the optional version parameter. A value of \"​application/taxii+json;version=2.1\"​ M​UST​ be included in this list to indicate conformance with this specification."),
                schema = Schema(description = "a Taxii Media Type"),
                uniqueItems = true
        )
        val versions: List<TaxiiMediaType>, //@TODO rebuild to be proper types for versions

        @JsonProperty("max_content_length")
        @field:Schema(name = "max_content_length", description = "The maximum size of the request body in octets (8-bit bytes) that the server can support. The value of the max_content_length​ ​MUST​ be a positive ​integer greater than zero. This applies to requests only and is determined by the server. Requests with total body length values smaller than this value ​MUST NOT​ result in an HTTP 413 (Request Entity Too Large) response. If for example, the server supported 100 MB of data, the value for this property would be determined by 100*1024*1024 which equals 104,857,600. This property contains useful information for the client when it POSTs requests to the Add Objects endpoint.")
        val maxContentLength: Int

) : TaxiiDomain {
    init {
//        require(TaxiiVersion.defaultTaxiiVersion in versions, lazyMessage = {"api-root versions property must include ${TaxiiVersion.defaultTaxiiVersion}"})
//        require(versions.containsAll(TaxiiVersion.supportedTaxiiVersions), lazyMessage = {"api-root versions property can only include ${TaxiiVersion.supportedTaxiiVersions}"})
        //@TODO add require for other media types that include the version parameter
        require(maxContentLength > 0)
    }
}