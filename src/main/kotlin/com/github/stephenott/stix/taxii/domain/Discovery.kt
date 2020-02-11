package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "discovery", description = "The \u200Bdiscovery\u200B resource contains information about a TAXII Server, such as a human-readable \u200Btitle\u200B, description\u200B, and \u200Bcontact\u200B information, as well as a list of API Roots that it is advertising. It also has an indication of which API Root it considers the \u200Bdefault\u200B, or the one to use in the absence of another information/user choice.")
data class Discovery(
        val title: String,
        val description: String? = null,
        val contact: String? = null,
        val default: String? = null,

        @JsonProperty("api_roots") @field:Schema(name = "api_roots")
        val apiRoots: List<String>? = null // @TODO should be a null or empty list?
): TaxiiDomain {
    init {
        default?.let {
            require(apiRoots != null)
            require(it in apiRoots)
        }
        apiRoots?.let {
            require(it.isNotEmpty())
        }
    }
}