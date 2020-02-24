package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.github.stephenott.stix.taxii.controller.StixMediaType
import com.github.stephenott.stix.taxii.domain.types.Identifier
import com.github.stephenott.stix.taxii.domain.types.Timestamp
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "manifest", description = "The manifest resource is a simple wrapper around a list of manifest-record items.")
data class Manifest(
        @field:Schema(description = "This property identifies if there is more content available based on the search criteria. The absence of this property means the value is false.")
        val more: Boolean = false,

        @field:ArraySchema(
                arraySchema = Schema(description = "The list of manifest entries for objects returned by the request. If there are no manifest-record items in the list, this key MUST be omitted, and the response is an empty object."),
                uniqueItems = true
        )
        val objects: List<ManifestRecord> = listOf(), // @TODO omit to empty object

        @JsonAnySetter @get:JsonAnyGetter
        override val customProperties: Map<String, Any> = mapOf()

): TaxiiDomain

@Schema(name = "manifest-record", description = "The manifest-record type captures metadata about a single version of an object, indicated by the id property. The metadata includes information such as when that version of the object was added to the Collection, the version of the object itself, and the media type that this specific version of the object is available in.")
data class ManifestRecord(

        //@TODO ** The description is not appearing in the swagger
        @field:Schema(description = "The identifier of the object that this manifest entry describes. For STIX objects the ​id​ ​MUST be the STIX Object ​id​. For object types that do not have their own identifier, the server ​MAY ​use any value as the ​id​.")
        val id: Identifier,

        @JsonProperty("date_added")
        @field:Schema(name = "date_added", description = "The date and time this object was added.")
        val dateAdded: Timestamp,

        @field:Schema(description = "The version of this object. For objects in STIX format, the STIX ​modified property is the version. If a STIX object is not versioned (and therefore does not have a modified timestamp), the server ​MUST ​use the created​ timestamp. If the STIX object does not have a ​created​ or ​modified​ timestamp then the server ​SHOULD​ use a value for the version that is consistent to the server.")
        val version: Timestamp,

        @JsonProperty("media_type")
        @field:Schema(name = "media_type", description = "The media type that this specific version of the object can be requested in. This value ​MUST​ be one of the media types listed on the​ c​ ollection resource.")
        val mediaType: StixMediaType? = null,

        @JsonAnySetter @get:JsonAnyGetter
        override val customProperties: Map<String, Any> = mapOf()

): TaxiiDomain