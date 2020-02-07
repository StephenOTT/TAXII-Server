package com.github.stephenott.stix.taxii.domain

data class CustomProperties(
        val customProperties: Map<String, Any>
) {
    init {
        //@TODO add json unwrap ability
        //@TODO add custom prop rules
    }
}