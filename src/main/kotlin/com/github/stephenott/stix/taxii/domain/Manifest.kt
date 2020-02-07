package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(name = "manifest", description = "The \u200Bmanifest\u200B resource is a simple wrapper around a list of \u200Bmanifest-record\u200B items.")
data class Manifest(
        val more: Boolean = false,
        val objects: List<ManifestRecord> = listOf() // @TODO omit to empty object
)

@Schema(name = "manifest-record", description = "The \u200Bmanifest-record\u200B type captures metadata about a single version of an \u200Bobject\u200B, indicated by the \u200Bid property. The metadata includes information such as when that version of the object was added to the Collection, the version of the object itself, and the media type that this specific version of the object is available in.")
data class ManifestRecord(
        val id: String, //@TODO Identifier
        @JsonProperty("data_added")val dateAdded: Instant, //@TODO Instant
        val version: String,
        @JsonProperty("media_type") val mediaType: String? = null
)