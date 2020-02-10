package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.stephenott.stix.taxii.controller.TaxiiMediaType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "api-root", description = "The \u200Bapi-root\u200B resource contains general information about the API Root, such as a human-readable title\u200B and \u200Bdescription\u200B, the TAXII \u200Bversions\u200B it supports, and the maximum size (\u200Bmax_content_length\u200B) of the content body it will accept in a PUT or POST request.")
data class ApiRoot(
        val title: String,
        val description: String? = null,
        val versions: List<TaxiiMediaType>, //@TODO rebuild to be proper types for versions
        @JsonProperty("max_content_length") val maxContentLength: Int
) {
    init {
//        require(TaxiiVersion.defaultTaxiiVersion in versions, lazyMessage = {"api-root versions property must include ${TaxiiVersion.defaultTaxiiVersion}"})
//        require(versions.containsAll(TaxiiVersion.supportedTaxiiVersions), lazyMessage = {"api-root versions property can only include ${TaxiiVersion.supportedTaxiiVersions}"})
        //@TODO add require for other media types that include the version parameter
        require(maxContentLength > 0)
    }
}