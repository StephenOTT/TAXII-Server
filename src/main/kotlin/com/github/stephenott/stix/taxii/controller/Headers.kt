package com.github.stephenott.stix.taxii.controller

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.http.HttpHeaders
import io.micronaut.http.annotation.Header

class Headers(
        @Header @JsonProperty(ACCEPT) val accept: String,
        @Header @JsonProperty(AUTHORIZATION) val authorization: String? = null,
        @Header @JsonProperty(CONTENT_TYPE) val contentType: String,
        @Header @JsonProperty(USER_AGENT) val userAgent: String? = null,
        @Header @JsonProperty(WWW_AUTHENTICATE) val wwwAuthenticate: String? = null,
        @Header @JsonAnySetter val additionalHeaders: Map<String, List<String>>
) {

    companion object {
        const val ACCEPT: String = "Accept"
        const val AUTHORIZATION: String = "Authorization"
        const val CONTENT_TYPE: String = "Content-Type"
        const val USER_AGENT: String = "User-Agent"
        const val WWW_AUTHENTICATE: String = "WWW-Authenticate"

        const val X_TAXII_DATE_ADDED_FIRST: String = "X-TAXII-Date-Added-First"
        const val X_TAXII_DATE_ADDED_LAST: String = "X-TAXII-Date-Added-Last"
    }
}

