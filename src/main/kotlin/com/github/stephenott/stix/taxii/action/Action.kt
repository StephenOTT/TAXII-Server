package com.github.stephenott.stix.taxii.action

interface Action<B, R> {

    fun execute(providerRequest: ProviderRequest<B>): ProviderResponse<R>
}