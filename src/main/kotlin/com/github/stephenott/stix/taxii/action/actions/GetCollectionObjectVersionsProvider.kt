package com.github.stephenott.stix.taxii.action.actions

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.action.ProviderRequest
import com.github.stephenott.stix.taxii.action.ProviderResponse
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.Versions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCollectionObjectVersionsProvider: Action<Unit, Versions> {

    private val log: Logger = LoggerFactory.getLogger(GetCollectionObjectVersionsProvider::class.java)

    @Inject
    private lateinit var mapper: ObjectMapper


    @Throws(TaxiiException::class)
    override fun execute(providerRequest: ProviderRequest<Unit>): ProviderResponse<Versions> {
        if (log.isDebugEnabled) {
            log.debug(mapper.writeValueAsString(providerRequest))
        }
        throw NotImplementedException()
    }
}