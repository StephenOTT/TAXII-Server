package com.github.stephenott.stix.taxii.action.actions

import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.Collections
import javax.inject.Singleton

@Singleton
class GetCollectionsProvider: Action<Collections> {

    @Throws(TaxiiException::class)
    override fun execute(): Collections {
        throw NotImplementedException()
    }
}