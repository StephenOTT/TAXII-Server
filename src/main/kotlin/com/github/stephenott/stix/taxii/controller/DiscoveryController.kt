package com.github.stephenott.stix.taxii.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.stephenott.stix.taxii.action.ProviderRequest
import com.github.stephenott.stix.taxii.action.actions.DiscoveryProvider
import com.github.stephenott.stix.taxii.action.addAdditionalHeaders
import com.github.stephenott.stix.taxii.domain.Discovery
import com.github.stephenott.stix.taxii.domain.Error
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.validation.Validated
import io.reactivex.Single
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.Principal
import javax.inject.Inject

@Controller("/taxii2")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Validated
@SecuritySchemes(
        SecurityScheme(
                type = SecuritySchemeType.HTTP,
                name = "basicAuth",
                description = "Basic Authentication",
                scheme = "basic"
        )
)
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
            description = "This Endpoint provides general information about a TAXII Server, including the advertised API Roots. It's a common entry point for TAXII Clients into the data and services provided by a TAXII Server.",
            security = [
                SecurityRequirement(name = "basicAuth")
            ],
            parameters = [
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string",
                                allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII],
                                defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Discovery::class))], headers = [Header(name = Headers.CONTENT_TYPE, required = true, schema = Schema(type = "string"), description = "Always value of ${TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1}")]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The Discovery service is not found, or the client does not have access to the resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun discovery(principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<Discovery>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            discoveryProvider.execute(ProviderRequest(mapOf(), contentType, acceptType, request))
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
        return HttpResponse.status<Error>(HttpStatus.valueOf(exception.taxiError.httpStatus.toInt()))
                .contentType(TaxiiMediaType.taxii_2_1 as MediaType)
                .body(exception.taxiError)
    }

}