package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.stephenott.stix.taxii.controller.TaxiiMediaType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "collections", description = "The \u200Bcollections\u200B resource is a simple wrapper around a list of \u200Bcollection\u200B resources.")
data class Collections(
        val collections: List<Collection> = listOf() //@TODO must return a empoty object when empty list
)

@Schema(name = "collection", description = "The \u200Bcollection\u200B resource contains general information about a Collection, such as its \u200Bid\u200B, a human-readable \u200Btitle\u200B and \u200Bdescription\u200B, an optional list of supported \u200Bmedia_types\u200B (representing the media type of objects can be requested from or added to it), and whether the TAXII Client, as authenticated, can get objects from the Collection and/or add objects to it.")
data class Collection(
        val id: String, //@TODO Identifier
        val title: String,
        val description: String? = null,
        val alias: String? = null,
        @JsonProperty("can_read") val canRead: Boolean,
        @JsonProperty("can_write") val canWrite: Boolean,
        val mediaTypes: List<TaxiiMediaType>
)