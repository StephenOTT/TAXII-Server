package com.github.stephenott.stix.taxii.action

import com.github.stephenott.stix.taxii.controller.TaxiiMediaType
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse

data class ProviderResponse<T>(
        val responseBody: T,
        val contentType: TaxiiMediaType,
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