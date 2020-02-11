package com.github.stephenott.stix.taxii.controller

import com.github.stephenott.stix.taxii.domain.types.Timestamp

data class Filter(
        val addedAfter: Timestamp,
        val limit: Int,
        val next: String,
        val matches: Map<String, Any>
) {
    init {
        require(limit > 0, lazyMessage = {"A Filter's limit property must be greater than zero."})
    }
}
