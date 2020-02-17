package com.github.stephenott.stix.taxii.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.stephenott.stix.taxii.domain.types.Timestamp
import io.micronaut.http.HttpHeaders

data class Filter(
        @JsonProperty(ADDED_AFTER_QPARAM)
        val addedAfter: Timestamp,

        val limit: Int,

        val next: String,

        val matches: Map<String, Any>
) {
    init {
        require(limit > 0, lazyMessage = { "A Filter's limit property must be greater than zero." })
    }

    companion object{

        const val ADDED_AFTER_QPARAM: String = "added_after"
        const val LIMIT_QPARAM: String = "limit"

        fun validateFilterAddedAfter(headers: HttpHeaders){
            headers.findFirst(ADDED_AFTER_QPARAM).ifPresent {
                require(kotlin.runCatching { Timestamp(it) }.isSuccess,lazyMessage = {"'$ADDED_AFTER_QPARAM' query parameter must be a valid timestamp."})
            }
        }

    }
}
