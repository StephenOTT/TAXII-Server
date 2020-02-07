package com.github.stephenott.stix.taxii.action.actions

import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.Status
import javax.inject.Singleton

@Singleton
class AddCollectionObjectsProvider: Action<Status> {

    @Throws(TaxiiException::class)
    override fun execute(): Status {
        throw NotImplementedException()
    }
}