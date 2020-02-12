package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.stephenott.stix.taxii.controller.TaxiiMediaType
import com.github.stephenott.stix.taxii.domain.types.Identifier
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "collections", description = "The \u200Bcollections\u200B resource is a simple wrapper around a list of \u200Bcollection\u200B resources.")
data class Collections(

        @field:ArraySchema(
                arraySchema = Schema(description = "A list of Collections. If there are no Collections in the list, this key ​MUST​ be omitted, and the response is an empty object. The ​collection​ resource is defined in section ​5.2.1​."),
                uniqueItems = true
        )
        val collections: List<Collection> = listOf() //@TODO must return a empoty object when empty list
): TaxiiDomain {}

@Schema(name = "collection", description = "The \u200Bcollection\u200B resource contains general information about a Collection, such as its \u200Bid\u200B, a human-readable \u200Btitle\u200B and \u200Bdescription\u200B, an optional list of supported \u200Bmedia_types\u200B (representing the media type of objects can be requested from or added to it), and whether the TAXII Client, as authenticated, can get objects from the Collection and/or add objects to it.")
data class Collection(

        @field:Schema(description = "The ​id​ property universally and uniquely identifies this Collection. It is used in the Get Collection Endpoint (see section​ ​5.2​) as the ​{id}​ parameter to retrieve the Collection.")
        val id: Identifier, //@TODO Identifier

        @field:Schema(description = "A human readable plain text title used to identify this Collection.")
        val title: String,

        @field:Schema(description = "A human readable plain text description for this Collection.")
        val description: String? = null,

        @field:Schema(description = "A human readable collection name that can be used on systems to alias a collection ID. This could be used by organizations that want to preconfigure a known collection of data, regardless of the underlying, collection ID that is configured on a specific implementations.  If defined, the alias ​MUST​ be unique within a single api-root on a single TAXII server. There is no guarantee that an alias is globally unique across api-roots or TAXII server instances.")
        val alias: String? = null,

        @JsonProperty("can_read")
        @field:Schema(name = "can_read", description = "Indicates if the requester can read (i.e., GET) objects from this Collection. If​ t​ rue​,​ users are allowed to access the ​Get Objects​, ​Get an Object​, or G​ et Object Manifests​ endpoints for this Collection. If​ f​ alse​, ​users are not allowed to access these endpoints.")
        val canRead: Boolean,

        @JsonProperty("can_write")
        @field:Schema(name = "can_write", description = "Indicates if the requester can write (i.e., POST) objects to this Collection. If​ ​true​,​ users are allowed to access the ​Add Objects​ endpoint for this Collection. If​ ​false​, users are not allowed to access this endpoint.")
        val canWrite: Boolean,

        @JsonProperty("media_types")
        @field:Schema(name = "media_types", description = "A list of supported media types for Objects in this Collection. Absence of this property is equivalent to a single-value list containing \"​application/stix+json\"​. This list ​MUST​ describe all media types that the Collection can store.")
        val mediaTypes: List<TaxiiMediaType>
): TaxiiDomain {}