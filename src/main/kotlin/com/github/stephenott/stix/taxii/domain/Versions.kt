package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.github.stephenott.stix.taxii.domain.types.Timestamp
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "versions", description = "The versions resource is a simple wrapper around a list of versions.")
data class Versions(
        @field:Schema(description = "This property identifies if there is more content available based on the search criteria. The absence of this property means the value is false.")
        val more: Boolean? = null,

        @field:ArraySchema(
                arraySchema = Schema(description = "The list of object versions returned by the request. If there are no versions returned, this key MUST be omitted, and the response is an empty object."),
                uniqueItems = true)
        val versions: List<Timestamp> = listOf(),

        @JsonAnySetter @get:JsonAnyGetter
        override val customProperties: Map<String, Any> = mapOf()

) : TaxiiDomain {
    init {
        CustomProperties.validateCustomProperties(customProperties)
    }
}