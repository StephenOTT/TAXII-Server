package com.github.stephenott.stix.taxii.domain


interface CustomProperties {
    val customProperties: Map<String, Any>

    companion object {

        val PROPERTY_PREFX: String = "x-" //@TODO move to a bean for customization capability

        //@TODO add validation to classes for custom props
        fun validateKey(key: String){
            require(key.startsWith(CustomProperties.PROPERTY_PREFX),
                    lazyMessage = {"Custom properties must start with '${CustomProperties.PROPERTY_PREFX}'"})
        }
    }
}