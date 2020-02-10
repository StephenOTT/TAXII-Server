package com.github.stephenott.stix.taxii.action.actions

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.action.ProviderRequest
import com.github.stephenott.stix.taxii.action.ProviderResponse
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.Envelop
import com.github.stephenott.stix.taxii.domain.Status
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddCollectionObjectsProvider: Action<Envelop, Status> {

    private val log: Logger = LoggerFactory.getLogger(AddCollectionObjectsProvider::class.java)

    @Inject
    private lateinit var mapper: ObjectMapper

    @Throws(TaxiiException::class)
    override fun execute(providerRequest: ProviderRequest<Envelop>): ProviderResponse<Status> {
        if (log.isDebugEnabled){
            log.debug(mapper.writeValueAsString(providerRequest))
        }
        throw NotImplementedException()
    }
}