package com.github.stephenott.stix.taxii.domain.types

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(type = "string", format = "uuid")
data class Identifier(val uuid: UUID = UUID.randomUUID()){
    override fun toString(): String {
        return uuid.toString()
    }
}