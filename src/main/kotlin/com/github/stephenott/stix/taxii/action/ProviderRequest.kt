package com.github.stephenott.stix.taxii.action

import com.github.stephenott.stix.taxii.controller.TaxiiMediaType
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpParameters
import io.micronaut.http.HttpRequest
import io.micronaut.http.cookie.Cookies
import java.net.InetSocketAddress
import java.security.Principal

data class ProviderRequest<T> (
        val pathParameters: Map<String, String>,
        val requestBody: T?,
        val parameters: HttpParameters,
        val cookies: Cookies,
        val contentType: TaxiiMediaType,
        val acceptType: TaxiiMediaType,
        val headers: HttpHeaders,
        val httpMethod: HttpMethod,
        val path: String,
        val remoteAddress: InetSocketAddress,
        val serverAddress: InetSocketAddress,
        val isSecure: Boolean,
        val principal: Principal?
){
    constructor(pathParameters: Map<String, String>, contentType: TaxiiMediaType, acceptType: TaxiiMediaType, request: HttpRequest<T>):this(
            pathParameters = pathParameters,
            requestBody = request.body.takeIf { it.isPresent }?.get(),
            parameters = request.parameters,
            cookies = request.cookies,
            headers = request.headers,
            contentType = contentType,
            acceptType = acceptType,
            httpMethod = request.method,
            path = request.path,
            remoteAddress = request.remoteAddress,
            serverAddress = request.serverAddress,
            isSecure = request.isSecure,
            principal = request.userPrincipal.takeIf { it.isPresent }?.get()
    )
}