package com.github.stephenott.stix.taxii.domain.types

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import io.swagger.v3.oas.annotations.media.Schema

//@TODO need to move to a model converter
//@Schema(name = "stix-object", type = "object")
data class StixObject(
        @JsonAnySetter
        @get:JsonAnyGetter
        val stixObject: Map<String, Any> = mutableMapOf()
)
