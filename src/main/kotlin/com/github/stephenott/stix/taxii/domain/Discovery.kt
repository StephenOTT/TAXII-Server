package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "discovery", description = "The discovery resource contains information about a TAXII Server, such as a human-readable title, description, and contact information, as well as a list of API Roots that it is advertising. It also has an indication of which API Root it considers the default, or the one to use in the absence of another information/user choice.")
data class Discovery(

        @field:Schema(description = "A human readable plain text name used to identify this server.")
        val title: String,

        @field:Schema(description = "A human readable plain text description for this server.")
        val description: String? = null,

        @field:Schema(description = "The human readable plain text contact information for this server and/or the administrator of this server.")
        val contact: String? = null,

        @field:Schema(description = "The default API Root that a TAXII Client ​MAY​ use. Absence of this property indicates that there is no default API Root. The default API Root ​MUST ​be an item in ​api_roots​.")
        val default: String? = null,

        @JsonProperty("api_roots")
        @field:Schema(name = "api_roots", description = "A list of URLs that identify known API Roots. This list MAY be filtered on a per-client basis.  API Root URLs MUST be HTTPS absolute URLs or relative URLs. API Root relative URLs MUST begin with a single \"/\" character and MUST NOT begin with \"//\" or \"../\". API Root URLs MUST NOT contain a URL query component.")
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