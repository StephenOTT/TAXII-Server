package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.github.stephenott.stix.taxii.domain.types.Dictionary
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "error", description = "The error message is provided by TAXII Servers in the response body when returning an HTTP error status and contains more information describing the error, including a human-readable title and description, an error_code and error_id, and a details structure to capture further structured information about the error. All of the properties are application-specific, and clients shouldn't assume consistent meaning across TAXII Servers even if the codes, IDs, or titles are the same.")
data class Error(

        @field:Schema(description = "A human readable plain text title for this error.")
        val title: String,

        @field:Schema(description = "A human readable plain text description that gives details about the error or problem that was encountered by the application.")
        val description: String? = null,

        @JsonProperty("error_id")
        @field:Schema(name = "error_id", description = "An identifier for this particular error instance. A TAXII Server might choose to assign each error occurrence its own identifier in order to facilitate debugging.")
        val errorId: String? = null,

        @JsonProperty("error_code")
        @field:Schema(name = "error_code", description = "The error code for this error type. A TAXII Server might choose to assign a common error code to all errors of the same type. Error codes are application-specific and not intended to be meaningful across different TAXII Servers.")
        val errorCode: String? = null,

        @JsonProperty("http_status")
        @field:Schema(name = "http_status", description = "The HTTP status code applicable to this error. If this property is provided it MUST match the HTTP status code found in the HTTP header.")
        val httpStatus: String? = null,

        @JsonProperty("external_details")
        @field:Schema(name = "external_details", description = "A URL that points to additional details. For example, this could be a URL pointing to a knowledge base article describing the error code. Absence of this property indicates that there are no additional details.")
        val externalDetails: String? = null,

        @field:Schema(description = "The details property captures additional server-specific details about the error. The keys and values are determined by the TAXII Server and ​MAY be any valid JSON object structure.")
        val details: Dictionary? = null,

        @JsonAnySetter @get:JsonAnyGetter
        override val customProperties: Map<String, Any> = mapOf()

): TaxiiDomain