package com.github.stephenott.stix.taxii.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.stephenott.stix.taxii.domain.types.Timestamp
import io.swagger.v3.oas.annotations.media.Schema


@Schema(name = "status", description = "The status resource represents information about a request to add objects to a Collection. It contains information about the status of the request, such as whether or not it's completed (\u200Bstatus\u200B) and \u200BMAY contain the status of individual objects within the request (i.e. whether they are still pending, completed and failed, or completed and succeeded).")
data class Status(
        val id: String, //@TODO Identifier

        val status: RequestStatus,

        @JsonProperty("request_timestamp") @field:Schema(name = "request_timestamp")
        val requestTimestamp: Timestamp? = null,

        val totalCount: Int,

        @JsonProperty("success_count") @field:Schema(name = "success_count")
        val successCount: Int,

        val successes: List<StatusDetails>? = null,

        @JsonProperty("failure_count") @field:Schema(name = "failure_count")
        val failureCount: Int,

        val failures: List<StatusDetails>? = null,

        @JsonProperty("pending_count") @field:Schema(name = "pending_count")
        val pendingCount: Int,

        val pendings: List<StatusDetails>? = null
): TaxiiDomain {
    init {
        require(totalCount >= 0)
        require(successCount >= 0)
        require(failureCount >= 0)
        require(pendingCount >= 0)
    }
}

@Schema(name = "status-details", description = "This type represents an object that was added, is pending, or not added to the Collection. It contains the id\u200B and \u200Bversion\u200B of the object along with a \u200Bmessage\u200B describing any details about its status.")
data class StatusDetails(
        val id: String, //@TODO Identifier
        val version: String,
        val message: String? = null
): TaxiiDomain

enum class RequestStatus{
    COMPLETE{
        override fun toString(): String {
            return "complete"
        }
    },
    PENDING{
        override fun toString(): String {
            return "pending"
        }
    }
}