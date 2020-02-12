package com.github.stephenott.stix.taxii.domain.types

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "TAXII Dictionary", type = "object")
data class Dictionary(
        @JsonAnySetter
        @get:JsonAnyGetter
        val dictionary: Map<String, Any> = mutableMapOf() //@TODO mapof()
)