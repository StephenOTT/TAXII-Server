package com.github.stephenott.stix.taxii.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.stephenott.stix.taxii.action.ProviderRequest
import com.github.stephenott.stix.taxii.action.actions.AddCollectionObjectsProvider
import com.github.stephenott.stix.taxii.action.actions.DiscoveryProvider
import com.github.stephenott.stix.taxii.action.addAdditionalHeaders
import com.github.stephenott.stix.taxii.domain.Discovery
import com.github.stephenott.stix.taxii.domain.Error
import io.micronaut.http.*
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.validation.Validated
import io.reactivex.Single
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.Principal
import javax.inject.Inject

@Controller("/taxii2")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Validated
open class DiscoveryController() {

    private val log: Logger = LoggerFactory.getLogger(DiscoveryController::class.java)

    @Inject
    private lateinit var discoveryProvider: DiscoveryProvider

    @Inject
    private lateinit var mapper: ObjectMapper

    @Get(value = "/", processes = [StixMediaType.APPLCATION_JSON_STIX_VERSION_2_1])
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Operation(
            summary = "Get information about the TAXII Server and any advertised API Roots",
            description = "This Endpoint provides general information about a TAXII Server, including the advertised API Roots. It's a common entry point for TAXII Clients into the data and services provided by a TAXII Server."
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Discovery::class))]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The Discovery service is not found, or the client does not have access to the resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun discovery(principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<Discovery>> {
        return Single.fromCallable {
            discoveryProvider.execute(ProviderRequest(mapOf(), request))
        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map { pr ->
            HttpResponse.ok(pr.responseBody)
                    .addAdditionalHeaders(pr.additionalHeaders)
        }
    }

    @io.micronaut.http.annotation.Error
    fun taxiiException(request: HttpRequest<*>, exception: TaxiiException): HttpResponse<Error> {
        log.error("Discovery Controller Error", exception)
        requireNotNull(exception.taxiError.httpStatus)
        return HttpResponse.status<Error>(HttpStatus.valueOf(exception.taxiError.httpStatus.toInt())).body(exception.taxiError)
    }

}