package com.github.stephenott.stix.taxii.action.actions

import com.github.stephenott.stix.taxii.action.Action
import com.github.stephenott.stix.taxii.controller.NotImplementedException
import com.github.stephenott.stix.taxii.controller.TaxiiException
import com.github.stephenott.stix.taxii.domain.Manifest
import javax.inject.Singleton

@Singleton
class GetCollectionManifestProvider: Action<Manifest> {

    @Throws(TaxiiException::class)
    override fun execute(): Manifest {
        throw NotImplementedException()
    }
}