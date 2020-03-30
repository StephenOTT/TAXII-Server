package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.github.stephenott.stix.taxii.domain.types.Identifier
import com.github.stephenott.stix.taxii.domain.types.Timestamp
import io.swagger.v3.oas.annotations.media.Schema


@Schema(name = "status", description = "The status resource represents information about a request to add objects to a Collection. It contains information about the status of the request, such as whether or not it's completed (status) and MAY contain the status of individual objects within the request (i.e. whether they are still pending, completed and failed, or completed and succeeded).")
data class Status(

        @field:Schema(description = "The ​identifier​ of this Status resource.")
        val id: Identifier,

        @field:Schema(description = "The overall status of a previous POST request where an HTTP 202 (Accept) was returned. The value of this property ​MUST​ be one of ​complete or ​pending​. A value of ​complete​ indicates that this resource will not be updated further, and MAY​ be removed in the future. A status of pending​ indicates that this resource ​MAY​ be updated in the future.")
        val status: RequestStatus,

        @JsonProperty("request_timestamp")
        @field:Schema(name = "request_timestamp", description = "The datetime of the request that this status resource is monitoring.")
        val requestTimestamp: Timestamp? = null,

        @JsonProperty("total_count")
        @field:Schema(description = "The total number of objects that were in the request, which would be the number of objects in the envelope. The value of the total_count MUST be a positive integer greater than or equal to zero. If this property has a value of 0, then the TAXII Server has not yet started processing the request.")
        val totalCount: Int,

        @JsonProperty("success_count")
        @field:Schema(name = "success_count", description = "The number of objects that were successfully created. The value of the success_count MUST be a positive integer greater than or equal to zero.")
        val successCount: Int,

        @field:Schema(description = "A list of objects that was successfully processed.")
        val successes: List<StatusDetails>? = null,

        @JsonProperty("failure_count")
        @field:Schema(name = "failure_count", description = "The number of objects that failed to be created. The value of the failure_count MUST be a positive integer greater than or equal to zero.")
        val failureCount: Int,

        @field:Schema(description = "A list of objects that was not successfully processed.")
        val failures: List<StatusDetails>? = null,

        @JsonProperty("pending_count")
        @field:Schema(name = "pending_count", description = "The number of objects that have yet to be processed. The value of the pending_count MUST be a positive integer greater than or equal to zero.")
        val pendingCount: Int,

        @field:Schema(description = "A list of objects that have yet to be processed.")
        val pendings: List<StatusDetails>? = null,

        @JsonAnySetter @get:JsonAnyGetter
        override val customProperties: Map<String, Any> = mapOf()

) : TaxiiDomain {
    init {
        require(totalCount >= 0)
        require(successCount >= 0)
        require(failureCount >= 0)
        require(pendingCount >= 0)

        CustomProperties.validateCustomProperties(customProperties)
    }
}

@Schema(name = "status-details", description = "This type represents an object that was added, is pending, or not added to the Collection. It contains the id and version of the object along with a message describing any details about its status.")
data class StatusDetails(

        @field:Schema(description = "The identifier of the object that succeed, is pending, or failed to be created. For STIX objects the ​id​ ​MUST​ be the STIX Object ​id​. For object types that do not have their own identifier, the server ​MAY ​use any value as the ​id​.")
        val id: Identifier,

        @field:Schema(description = "The version of the object that succeeded, is pending, or failed to be created. For STIX objects the version ​MUST​ be the STIX modified timestamp Property. If a STIX object is not versioned (and therefore does not have a modified timestamp), the server ​MUST ​use the created​ timestamp. If the STIX object does not have a ​created​ or ​modified​ timestamp then the server ​SHOULD​ use a value for the version that is consistent to the server.")
        val version: Timestamp,

        @field:Schema(description = "A message indicating more information about the object being created, its pending state, or why the object failed to be created.")
        val message: String? = null,

        @JsonAnySetter @get:JsonAnyGetter
        override val customProperties: Map<String, Any> = mapOf()

) : TaxiiDomain {
    init {
        CustomProperties.validateCustomProperties(customProperties)
    }
}

enum class RequestStatus {
    COMPLETE {
        override fun toString(): String {
            return "complete"
        }
    },
    PENDING {
        override fun toString(): String {
            return "pending"
        }
    }
}