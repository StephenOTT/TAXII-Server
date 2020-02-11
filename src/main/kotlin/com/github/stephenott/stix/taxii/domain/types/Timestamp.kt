package com.github.stephenott.stix.taxii.domain.types

import com.fasterxml.jackson.annotation.JsonGetter
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(type = "string", format = "date-time")
data class Timestamp (val instant: Instant
){
    @JsonGetter
    fun getTimestampAsString(): String{
        return instant.toString()
    }
}