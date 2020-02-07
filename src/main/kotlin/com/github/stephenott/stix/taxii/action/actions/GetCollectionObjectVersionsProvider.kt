package com.github.stephenott.stix.taxii.action.actions

import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.Versions
import javax.inject.Singleton

@Singleton
class GetCollectionObjectVersionsProvider: Action<Versions> {

    @Throws(TaxiiException::class)
    override fun execute(): Versions {
        throw NotImplementedException()
    }
}