package com.github.stephenott.stix.taxii.action.actions

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.action.ProviderRequest
import com.github.stephenott.stix.taxii.action.ProviderResponse
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.Discovery
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoveryProvider : Action<Unit, Discovery> {

    private val log: Logger = LoggerFactory.getLogger(DiscoveryProvider::class.java)

    @Inject
    private lateinit var mapper: ObjectMapper

    @Throws(TaxiiException::class)
    override fun execute(providerRequest: ProviderRequest<Unit>): ProviderResponse<Discovery> {
        if (log.isDebugEnabled) {
            log.debug(mapper.writeValueAsString(providerRequest))
        }
        return ProviderResponse(
                Discovery(
                        title = "Some Default TAXII Server",
                        description = "This is some Default TAXII Server",
                        contact = "Some Contact goes here",
                        default = "/default-root-1",
                        apiRoots = listOf(
                                "/default-root-1",
                                "/root2",
                                "/root3"
                        ),
                        customProperties = mapOf(
                                Pair("x-someKey", "someValue")
                        )
                ),
                providerRequest.acceptType
        )
    }
}