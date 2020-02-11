package com.github.stephenott.stix.taxii.domain

import com.github.stephenott.stix.taxii.domain.types.Timestamp
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "versions", description = "The \u200Bversions\u200B resource is a simple wrapper around a list of versions.")
data class Versions(
        val more: Boolean? = null,

        val versions: List<Timestamp> = listOf()
)