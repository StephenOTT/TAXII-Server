package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "error", description = "The \u200Berror\u200B message is provided by TAXII Servers in the response body when returning an HTTP error status and contains more information describing the error, including a human-readable \u200Btitle\u200B and description\u200B, an \u200Berror_code\u200B and \u200Berror_id\u200B, and a \u200Bdetails\u200B structure to capture further structured information about the error. All of the properties are application-specific, and clients shouldn't assume consistent meaning across TAXII Servers even if the codes, IDs, or titles are the same.")
data class Error(
        val title: String,
        val description: String? = null,
        @JsonProperty("error_id") val errorId: String? = null,
        @JsonProperty("error_code") val errorCode: String? = null,
        @JsonProperty("http_status") val httpStatus: String? = null,
        @JsonProperty("external_Details") val externalDetails: String? = null,
        val details: Map<String, Any>? = null
)