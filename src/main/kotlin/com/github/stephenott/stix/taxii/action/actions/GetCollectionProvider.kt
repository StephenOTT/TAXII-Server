package com.github.stephenott.stix.taxii.action.actions

import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.Collection
import javax.inject.Singleton

@Singleton
class GetCollectionProvider: Action<Collection> {

    @Throws(TaxiiException::class)
    override fun execute(): Collection {
        throw NotImplementedException()
    }
}