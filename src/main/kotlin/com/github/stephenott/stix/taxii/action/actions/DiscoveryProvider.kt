package com.github.stephenott.stix.taxii.action.actions

import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.Discovery
import javax.inject.Singleton

@Singleton
class DiscoveryProvider: Action<Discovery> {

    @Throws(TaxiiException::class)
    override fun execute(): Discovery {
        throw NotImplementedException()
    }
}