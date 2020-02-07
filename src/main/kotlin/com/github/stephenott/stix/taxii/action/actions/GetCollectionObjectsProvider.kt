package com.github.stephenott.stix.taxii.action.actions

import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.Envelop
import javax.inject.Singleton

@Singleton
class GetCollectionObjectsProvider: Action<Envelop> {

    @Throws(TaxiiException::class)
    override fun execute(): Envelop {
        throw NotImplementedException()
    }
}