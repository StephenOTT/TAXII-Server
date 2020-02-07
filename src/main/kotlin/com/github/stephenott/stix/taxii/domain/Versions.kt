package com.github.stephenott.stix.taxii.domain

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(name = "versions", description = "The \u200Bversions\u200B resource is a simple wrapper around a list of versions.")
data class Versions(
        val more: Boolean? = null,
        val versions: List<Instant> = listOf()
)