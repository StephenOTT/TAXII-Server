package com.github.stephenott.stix.taxii.controller

import com.github.stephenott.stix.taxii.action.actions.DiscoveryProvider
import com.github.stephenott.stix.taxii.domain.Discovery
import com.github.stephenott.stix.taxii.domain.Error
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.validation.Validated
import io.reactivex.Single
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import javax.inject.Inject

@Controller("/taxii2")
@Validated
open class DiscoveryController(){

    @Inject
    lateinit var discoveryProvider: DiscoveryProvider

    @Get(value = "/", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @Operation(
            summary = "Get information about the TAXII Server and any advertised API Roots",
            description = "This Endpoint provides general information about a TAXII Server, including the advertised API Roots. It's a common entry point for TAXII Clients into the data and services provided by a TAXII Server."
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Discovery::class))]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The Discovery service is not found, or the client does not have access to the resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun discovery(): Single<HttpResponse<Discovery>> {
        return Single.fromCallable {
            discoveryProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException){
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map {
            HttpResponse.ok(it)
        }
    }

    @io.micronaut.http.annotation.Error
    fun taxiiException(request: HttpRequest<*>, exception: TaxiiException): HttpResponse<Error> {
        exception.printStackTrace() //@TODO
        requireNotNull(exception.taxiError.httpStatus)
        return HttpResponseFactory.INSTANCE.status(HttpStatus.valueOf(exception.taxiError.httpStatus.toInt()), exception.taxiError)
    }

}