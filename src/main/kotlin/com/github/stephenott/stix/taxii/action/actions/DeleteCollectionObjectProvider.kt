package com.github.stephenott.stix.taxii.action.actions

import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import javax.inject.Singleton

@Singleton
class DeleteCollectionObjectProvider: Action<Unit> {

    @Throws(TaxiiException::class)
    override fun execute(): Unit {
        throw NotImplementedException()
    }
}