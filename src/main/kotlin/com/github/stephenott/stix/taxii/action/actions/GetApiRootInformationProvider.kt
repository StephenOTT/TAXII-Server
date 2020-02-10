package com.github.stephenott.stix.taxii.action.actions

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.action.ProviderRequest
import com.github.stephenott.stix.taxii.action.ProviderResponse
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.ApiRoot
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetApiRootInformationProvider: Action<Unit, ApiRoot> {

    private val log: Logger = LoggerFactory.getLogger(GetApiRootInformationProvider::class.java)

    @Inject
    private lateinit var mapper: ObjectMapper

    @Throws(TaxiiException::class)
    override fun execute(providerRequest: ProviderRequest<Unit>): ProviderResponse<ApiRoot> {
        if (log.isDebugEnabled) {
            log.debug(mapper.writeValueAsString(providerRequest))
        }

        return ProviderResponse(ApiRoot(
                title = "default-root-1",
                description = "this is the default root 1",
                versions = listOf("123"), // @TODO fix
                maxContentLength = 104857600 //100mb: 100*1024*1024
        ))
    }
}