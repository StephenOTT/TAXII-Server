package com.github.stephenott.stix.taxii.domain

import com.github.stephenott.stix.taxii.domain.types.StixObject
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

/**
 * @param more This property identifies if there is more content available based on the search criteria. The absence of this property means the value is false.
 * @param next This property identifies the server provided value of the next record or set of records in the paginated data set. This property ​MAY​ be populated if the ​more​ property is set to ​true.​
 * @param objects This property contains one or more STIX Objects. Objects in this list ​MUST​ be a STIX Object (e.g., SDO, SCO, SRO, Language Content object, or a Marking Definition object).
 */
@Schema(name = "envelop", description = "The envelope is a simple wrapper for STIX 2 content. When returning STIX 2 content in a TAXII response the HTTP root object payload \u200BMUST\u200B be an \u200Benvelope\u200B. This specification does not define any other form of content wrapper for objects outside of STIX content.")
data class Envelop(
        @field:Schema(description = "This property identifies if there is more content available based on the search criteria. The absence of this property means the value is false.")
        val more: Boolean = false,

        @field:Schema(description = "This property identifies the server provided value of the next record or set of records in the paginated data set. This property \u200BMAY\u200B be populated if the \u200Bmore\u200B property is set to \u200Btrue.\u200B")
        val next: String? = null,

        //@TODO review ArraySchema usage as it is not working as expected
        @ArraySchema(
                arraySchema = Schema(description = "This property contains one or more STIX Objects. Objects in this list MUST be a STIX Object (e.g., SDO, SCO, SRO, Language Content object, or a Marking Definition object)."),
                uniqueItems = true)
        val objects: List<StixObject> = listOf()
) : TaxiiDomain {
    init {
        next?.let {
            require(more, lazyMessage = { "Envelop's next property can only be populated if the more property is true." })
        }
        //@TODO add a plugin STIX validator for the returned objects
    }
}