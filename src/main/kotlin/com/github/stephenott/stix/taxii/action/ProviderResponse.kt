package com.github.stephenott.stix.taxii.action

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse

data class ProviderResponse<T>(
        val responseBody: T,
        val additionalHeaders: HttpHeaders? = null
)

fun <T> MutableHttpResponse<T>.addAdditionalHeaders(httpHeaders: HttpHeaders?): HttpResponse<T>{
    httpHeaders?.let { headers ->
        headers.forEachValue { key, value ->
            this.headers.add(key, value)
        }
    }
    return this
}