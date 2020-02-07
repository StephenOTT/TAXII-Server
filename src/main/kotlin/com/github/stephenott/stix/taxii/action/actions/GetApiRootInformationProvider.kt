package com.github.stephenott.stix.taxii.action.actions

import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.ApiRoot
import javax.inject.Singleton

@Singleton
class GetApiRootInformationProvider: Action<ApiRoot> {

    @Throws(TaxiiException::class)
    override fun execute(): ApiRoot {
        throw NotImplementedException()
    }
}