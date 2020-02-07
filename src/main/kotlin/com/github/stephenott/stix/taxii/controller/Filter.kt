package com.github.stephenott.stix.taxii.controller

import java.time.Instant

data class Filter(
        val addedAfter: Instant,
        val limit: Int,
        val next: String,
        val matches: Map<String, Any>
) {
    init {
        require(limit > 0, lazyMessage = {"A Filter's limit property must be greater than zero."})
    }
}
