package com.github.stephenott.stix.taxii.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.stephenott.stix.taxii.action.ProviderRequest
import com.github.stephenott.stix.taxii.action.actions.*
import com.github.stephenott.stix.taxii.action.addAdditionalHeaders
import com.github.stephenott.stix.taxii.domain.*
import com.github.stephenott.stix.taxii.domain.Collection
import com.github.stephenott.stix.taxii.domain.Error
import com.github.stephenott.stix.taxii.domain.Status
import com.github.stephenott.stix.taxii.domain.types.Timestamp
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.validation.Validated
import io.reactivex.Single
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.ArraySchema
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

@Controller("/{apiRoot}/")
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
open class RootsController() {

    private val log: Logger = LoggerFactory.getLogger(RootsController::class.java)

    @Inject
    private lateinit var getApiRootInformationProvider: GetApiRootInformationProvider

    @Inject
    private lateinit var getStatusProvider: GetStatusProvider

    @Inject
    private lateinit var getCollectionsProvider: GetCollectionsProvider

    @Inject
    private lateinit var getCollectionProvider: GetCollectionProvider

    @Inject
    private lateinit var getCollectionManifestProvider: GetCollectionManifestProvider

    @Inject
    private lateinit var getCollectionObjectsProvider: GetCollectionObjectsProvider

    @Inject
    private lateinit var addCollectionObjectsProvider: AddCollectionObjectsProvider

    @Inject
    private lateinit var getCollectionObjectProvider: GetCollectionObjectProvider

    @Inject
    private lateinit var deleteCollectionObjectProvider: DeleteCollectionObjectProvider

    @Inject
    private lateinit var getCollectionObjectVersionsProvider: GetCollectionObjectVersionsProvider

    @Inject
    private lateinit var mapper: ObjectMapper

    @Get(value = "/")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Operation(
            summary = "Get information about a specific API Root",
            description = "This Endpoint provides general information about an API Root, which can be used to help users and clients decide whether and how they want to interact with it. Multiple API Roots MAY be hosted on a single TAXII Server.  Often, an API Root represents a single trust group.",
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root", schema = Schema(type = "string")),
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ],
            security = [
                SecurityRequirement(name = "basicAuth")
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = ApiRoot::class))], headers = [Header(name = Headers.CONTENT_TYPE, required = true, schema = Schema(type = "string"), description = "Always value of ${TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1}")]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The Discovery service is not found, or the client does not have access to the resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getApiRoot(apiRoot: String, principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<ApiRoot>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            getApiRootInformationProvider.execute(ProviderRequest(
                    mapOf(Pair("apiRoot", apiRoot)),
                    contentType,
                    acceptType,
                    request))

        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }

        }.map {
            HttpResponse.ok(it.responseBody)
                    .contentType(it.contentType as MediaType)
                    .addAdditionalHeaders(it.additionalHeaders)
        }
    }

    @Get(value = "/status/{statusId}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Operation(
            summary = "Get status information for a specific status ID",
            description = "This Endpoint provides information about the status of a previous request. In TAXII 2.1, the only request that can be monitored is one to add objects to a Collection. It is typically used by TAXII Clients to monitor a POST request that they made in order to take action when it is complete.",
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root", schema = Schema(type = "string")),
                Parameter(name = "statusId", `in` = ParameterIn.PATH, required = true, description = "the identifier of the status message being requested", schema = Schema(type = "string")),
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ],
            security = [
                SecurityRequirement(name = "basicAuth")
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Status::class))], headers = [Header(name = Headers.CONTENT_TYPE, required = true, schema = Schema(type = "string"), description = "Always value of ${TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1}")]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root or Status ID are not found, or the client does not have access to the resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getStatus(apiRoot: String, statusId: String, principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<Status>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            getStatusProvider.execute(ProviderRequest(
                    mapOf(
                            Pair("apiRoot", apiRoot),
                            Pair("statusId", statusId)
                    ),
                    contentType,
                    acceptType,
                    request))

        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }

        }.map {
            HttpResponse.ok(it.responseBody)
                    .contentType(it.contentType as MediaType)
                    .addAdditionalHeaders(it.additionalHeaders)
        }
    }


    @Get(value = "/collections")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Operation(
            summary = "Get information about all collections",
            description = "This endpoint provides information about the Collections hosted under this API Root. This is similar to the response to get a Collection, but rather than providing information about one Collection it provides information about all of the Collections. Most importantly, it provides the Collection's id, which is used to request objects or manifest entries from the Collection.",
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root", schema = Schema(type = "string")),
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ],
            security = [
                SecurityRequirement(name = "basicAuth")
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Collections::class))], headers = [Header(name = Headers.CONTENT_TYPE, required = true, schema = Schema(type = "string"), description = "Always value of ${TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1}")]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this collections resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root is not found, or the client does not have access to the collections resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollections(apiRoot: String, principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<Collections>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            getCollectionsProvider.execute(ProviderRequest(
                    mapOf(Pair("apiRoot", apiRoot)),
                    contentType,
                    acceptType,
                    request))

        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }

        }.map {
            HttpResponse.ok(it.responseBody)
                    .contentType(it.contentType as MediaType)
                    .addAdditionalHeaders(it.additionalHeaders)
        }
    }


    @Get(value = "/collections/{collectionId}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Operation(
            summary = "Get information about a specific collection",
            description = "This Endpoint provides general information about a Collection, which can be used to help users and clients decide whether and how they want to interact with it. For example, it will tell clients what it's called and what permissions they have to it.",
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root", schema = Schema(type = "string")),
                Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the identifier of the Collection being requested", schema = Schema(type = "string")),
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ],
            security = [
                SecurityRequirement(name = "basicAuth")
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Collections::class))], headers = [Header(name = Headers.CONTENT_TYPE, required = true, schema = Schema(type = "string"), description = "Always value of ${TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1}")]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this collections resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root or Collection ID are not found, or the client does not have access to the collection resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollection(apiRoot: String, collectionId: String, principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<Collection>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            getCollectionProvider.execute(ProviderRequest(
                    mapOf(
                            Pair("apiRoot", apiRoot),
                            Pair("collectionId", collectionId)
                    ),
                    contentType,
                    acceptType,
                    request))

        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }

        }.map {
            HttpResponse.ok(it.responseBody)
                    .contentType(it.contentType as MediaType)
                    .addAdditionalHeaders(it.additionalHeaders)
        }
    }


    @Get(value = "/collections/{collectionId}/manifest{?qParams*}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @ExplosionTarget(["match"])
    @Operation(
            summary = "Get manifest information about the contents of a specific collection",
            description = "This Endpoint retrieves a manifest about the objects in a Collection. It supports filtering identical to the get objects Endpoint but rather than returning the object itself it returns metadata about the object. It can be used to retrieve metadata to decide whether it's worth retrieving the actual objects.",
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root", schema = Schema(type = "string")),
                Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the identifier of the Collection being requested", schema = Schema(type = "string")),
                Parameter(name = "added_after", `in` = ParameterIn.QUERY, description = "a single timestamp", schema = Schema(type = "string")),
                Parameter(name = "limit", `in` = ParameterIn.QUERY, description = "a single integer", schema = Schema(type = "integer")),
                Parameter(name = "next", `in` = ParameterIn.QUERY, description = "a single string", schema = Schema(type = "string")),
                Parameter(name = "match[id]", `in` = ParameterIn.QUERY, description = "an id(s) of an object", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = "match[type]", `in` = ParameterIn.QUERY, description = "the type(s) of an object", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = "match[version]", `in` = ParameterIn.QUERY, description = "the version(s) of an object", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = "match[spec_version]", `in` = ParameterIn.QUERY, description = "the specification version(s)", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ],
            security = [
                SecurityRequirement(name = "basicAuth")
            ]
    )
    //@TODO required Headers Accept: application/taxii+json;version=2.1,application/stix+json;version=2.1
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Manifest::class))],
                    headers = [
                        Header(name = Headers.CONTENT_TYPE, required = true, description = "Always value of ${TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1}", schema = Schema(type = "string")),
                        Header(name = Headers.X_TAXII_DATE_ADDED_FIRST, required = true, description = "timestamp", schema = Schema(implementation = Timestamp::class)),
                        Header(name = Headers.X_TAXII_DATE_ADDED_LAST, required = true, description = "timestamp", schema = Schema(implementation = Timestamp::class))
                    ]
            ),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this manifest resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root or Collection ID are not found, or the client does not have access to the manifest resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollectionManifest(apiRoot: String, collectionId: String, @QueryValueExploded qParams: Map<String, Any>?, principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<Manifest>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            getCollectionManifestProvider.execute(ProviderRequest(
                    mapOf(
                            Pair("apiRoot", apiRoot),
                            Pair("collectionId", collectionId)
                    ),
                    contentType,
                    acceptType,
                    request))

        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }

        }.map {
            HttpResponse.ok(it.responseBody)
                    .contentType(it.contentType as MediaType)
                    .addAdditionalHeaders(it.additionalHeaders)
        }
    }


    @Get(value = "/collections/{collectionId}/objects{?qParams*}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @ExplosionTarget(["match"])
    @Operation(
            summary = "Get all objects from a collection",
            description = "This Endpoint retrieves objects from a Collection. Clients can search for objects in the Collection, retrieve all objects in a Collection, or paginate through objects in the Collection.",
            security = [
                SecurityRequirement(name = "basicAuth")
            ],
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root", schema = Schema(type = "string")),
                Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the identifier of the Collection being requested", schema = Schema(type = "string")),
                Parameter(name = "added_after", `in` = ParameterIn.QUERY, description = "a single timestamp", schema = Schema(type = "string")),
                Parameter(name = "limit", `in` = ParameterIn.QUERY, description = "a single integer", schema = Schema(type = "integer")),
                Parameter(name = "next", `in` = ParameterIn.QUERY, description = "a single string", schema = Schema(type = "string")),
                Parameter(name = "match[id]", `in` = ParameterIn.QUERY, description = "an id(s) of an object", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = "match[type]", `in` = ParameterIn.QUERY, description = "the type(s) of an object", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = "match[version]", `in` = ParameterIn.QUERY, description = "the version(s) of an object", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = "match[spec_version]", `in` = ParameterIn.QUERY, description = "the specification version(s)", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Envelop::class))],
                    headers = [
                        Header(name = Headers.CONTENT_TYPE, required = true, description = "Always value of ${TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1}", schema = Schema(type = "string")),
                        Header(name = Headers.X_TAXII_DATE_ADDED_FIRST, required = true, description = "timestamp", schema = Schema(implementation = Timestamp::class)),
                        Header(name = Headers.X_TAXII_DATE_ADDED_LAST, required = true, description = "timestamp", schema = Schema(implementation = Timestamp::class))
                    ]
            ),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this objects resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root or Collection ID are not found, or the client does not have access to the objects resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollectionObjects(apiRoot: String, collectionId: String, @QueryValueExploded qParams: Map<String, Any>?, principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<Envelop>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            getCollectionObjectsProvider.execute(ProviderRequest(
                    mapOf(
                            Pair("apiRoot", apiRoot),
                            Pair("collectionId", collectionId)
                    ),
                    contentType,
                    acceptType,
                    request))

        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }

        }.map {
            HttpResponse.ok(it.responseBody)
                    .contentType(it.contentType as MediaType)
                    .addAdditionalHeaders(it.additionalHeaders)
        }
    }

    @Post(value = "/collections/{collectionId}/objects")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Operation(
            summary = "Add a new object to a specific collection",
            description = "This Endpoint adds objects to a Collection.",
            security = [
                SecurityRequirement(name = "basicAuth")
            ],
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root", schema = Schema(type = "string")),
                Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the identifier of the Collection being requested", schema = Schema(type = "string")),
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1)),
                Parameter(name = Headers.CONTENT_TYPE, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "202", description = "The request was successful accepted", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Envelop::class))], headers = [Header(name = Headers.CONTENT_TYPE, required = true, schema = Schema(type = "string"), description = "Always value of ${TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1}")]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to write to this objects resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root or Collection ID are not found, or the client can not write to this objects resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "413", description = "The POSTed payload exceeds the max_content_length of the API Root", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "415", description = "The client attempted to POST a payload with a content type the server does not support", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "422", description = "The object type of version is not supported of could not be processed.  This can happen, for example, when sending a version of STIX that this TAXII Server does not support and cannot process, when sending a malformed body, or other unprocessable content", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun addCollectionObjects(apiRoot: String, collectionId: String, @Body envelop: Envelop, principal: Principal?, request: HttpRequest<Envelop>): Single<HttpResponse<Status>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            addCollectionObjectsProvider.execute(ProviderRequest(
                    mapOf(
                            Pair("apiRoot", apiRoot),
                            Pair("collectionId", collectionId)
                    ),
                    contentType,
                    acceptType,
                    request))

        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }

        }.map {
            HttpResponse.status<Status>(HttpStatus.ACCEPTED).body(it.responseBody)
                    .contentType(it.contentType as MediaType)
                    .addAdditionalHeaders(it.additionalHeaders)
        }
    }

    @Get(value = "/collections/{collectionId}/objects/{objectId}{?qParams*}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @ExplosionTarget(["match"])
    @Operation(
            summary = "Get a specific object from a collection",
            description = "This Endpoint gets an object from a Collection by its id. It can be thought of as a search where the match[id] parameter is set to the {objectId} in the path. For STIX 2 objects, the {objectId} MUST be the STIX id.",
            security = [
                SecurityRequirement(name = "basicAuth")
            ],
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root", schema = Schema(type = "string")),
                Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the identifier of the Collection being requested", schema = Schema(type = "string")),
                Parameter(name = "objectId", `in` = ParameterIn.PATH, required = true, description = "the ID of the object being requested", schema = Schema(type = "string")),
                Parameter(name = "added_after", `in` = ParameterIn.QUERY, description = "a single timestamp", schema = Schema(type = "string")),
                Parameter(name = "limit", `in` = ParameterIn.QUERY, description = "a single integer", schema = Schema(type = "integer")),
                Parameter(name = "next", `in` = ParameterIn.QUERY, description = "a single string", schema = Schema(type = "string")),
                Parameter(name = "match[version]", `in` = ParameterIn.QUERY, description = "the version(s) of an object", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = "match[spec_version]", `in` = ParameterIn.QUERY, description = "the specification version(s)", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Envelop::class))],
                    headers = [
                        Header(name = Headers.CONTENT_TYPE, required = true, description = "Always value of ${TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1}", schema = Schema(type = "string")),
                        Header(name = Headers.X_TAXII_DATE_ADDED_FIRST, required = true, description = "timestamp", schema = Schema(implementation = Timestamp::class)),
                        Header(name = Headers.X_TAXII_DATE_ADDED_LAST, required = true, description = "timestamp", schema = Schema(implementation = Timestamp::class))
                    ]
            ),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this objects resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root, Collection ID and/or Object ID are not found, or the client does not have access to the object resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollectionObject(apiRoot: String, collectionId: String, objectId: String, @QueryValueExploded qParams: Map<String, Any>?, principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<Envelop>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            getCollectionObjectProvider.execute(ProviderRequest(
                    mapOf(
                            Pair("apiRoot", apiRoot),
                            Pair("collectionId", collectionId),
                            Pair("objectId", objectId)
                    ),
                    contentType,
                    acceptType,
                    request))

        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }

        }.map {
            HttpResponse.ok(it.responseBody)
                    .contentType(it.contentType as MediaType)
                    .addAdditionalHeaders(it.additionalHeaders)
        }
    }

    @Delete(value = "/collections/{collectionId}/objects/{objectId}{?qParams}}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @ExplosionTarget(["match"])
    @Operation(
            summary = "Delete a specific object from a collection",
            description = "This endpoint deletes an object from a Collection by its id. For STIX 2 objects, the {object-id} MUST be the STIX id.",
            security = [
                SecurityRequirement(name = "basicAuth")
            ],
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root", schema = Schema(type = "string")),
                Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the identifier of the Collection being requested", schema = Schema(type = "string")),
                Parameter(name = "objectId", `in` = ParameterIn.PATH, required = true, description = "the ID of the object being deleted", schema = Schema(type = "string")),
                Parameter(name = "match[version]", `in` = ParameterIn.QUERY, description = "the version(s) of an object", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = "match[spec_version]", `in` = ParameterIn.QUERY, description = "the specification version(s)", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1)]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client has access to the object, but not to delete it", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root, Collection ID and/or Object ID are not found, or the client does not have access to the object", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun deleteCollectionObject(apiRoot: String, collectionId: String, objectId: String, @QueryValueExploded qParams: Map<String, Any>?, principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<Unit>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            deleteCollectionObjectProvider.execute(ProviderRequest(
                    mapOf(
                            Pair("apiRoot", apiRoot),
                            Pair("collectionId", collectionId),
                            Pair("objectId", objectId)
                    ),
                    contentType,
                    acceptType,
                    request))

        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }

        }.map {
            HttpResponse.ok(it.responseBody)
                    .contentType(it.contentType as MediaType)
                    .addAdditionalHeaders(it.additionalHeaders)
        }
    }

    @Get(value = "/collections/{collectionId}/objects/{objectId}/versions{?qParams*}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @ExplosionTarget(["match"])
    @Operation(
            summary = "Get a list of object versions from a collection",
            description = "This Endpoint retrieves a list of one or more versions of an object in a Collection. This list can be used to decide whether it's worth retrieving the actual objects, or if new versions have been added. If a STIX object is not versioned (and therefore does not have a modified timestamp), the server MUST use created timestamp.",
            security = [
                SecurityRequirement(name = "basicAuth")
            ],
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root", schema = Schema(type = "string")),
                Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the identifier of the Collection being requested", schema = Schema(type = "string")),
                Parameter(name = "objectId", `in` = ParameterIn.PATH, required = true, description = "the ID of the object being requested", schema = Schema(type = "string")),
                Parameter(name = "added_after", `in` = ParameterIn.QUERY, description = "a single timestamp", schema = Schema(type = "string")),
                Parameter(name = "limit", `in` = ParameterIn.QUERY, description = "a single integer", schema = Schema(type = "integer")),
                Parameter(name = "next", `in` = ParameterIn.QUERY, description = "a single string", schema = Schema(type = "string")),
                Parameter(name = "match[spec_version]", `in` = ParameterIn.QUERY, description = "the specification version(s)", array = ArraySchema(schema = Schema(type = "string"))),
                Parameter(name = Headers.ACCEPT, `in` = ParameterIn.HEADER, required = false, schema = Schema(type = "string", allowableValues = [TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_0, TaxiiMediaType.APPLCATION_JSON_TAXII], defaultValue = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1))
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Versions::class))],
                    headers = [
                        Header(name = Headers.CONTENT_TYPE, required = true, description = "Always value of ${TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1}", schema = Schema(type = "string")),
                        Header(name = Headers.X_TAXII_DATE_ADDED_FIRST, required = true, description = "timestamp", schema = Schema(implementation = Timestamp::class)),
                        Header(name = Headers.X_TAXII_DATE_ADDED_LAST, required = true, description = "timestamp", schema = Schema(implementation = Timestamp::class))
                    ]
            ),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this objects resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root, Collection ID and/or Object ID are not found, or the client does not have access to the object resource", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = TaxiiMediaType.APPLCATION_JSON_TAXII_VERSION_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollectionObjectVersions(apiRoot: String, collectionId: String, objectId: String, @QueryValueExploded qParams: Map<String, Any>?, principal: Principal?, request: HttpRequest<Unit>): Single<HttpResponse<Versions>> {
        return Single.fromCallable {
            val contentType = kotlin.runCatching { TaxiiMediaType.validateContentTypeTaxiiMediaType(request.headers.contentType().orElse(null)) }.getOrElse { throw MediaTypeException("415", Headers.CONTENT_TYPE) }
            val acceptType = kotlin.runCatching { TaxiiMediaType.validationAcceptTaxiiMediaType(request.headers.accept()) }.getOrElse { throw MediaTypeException("406", Headers.ACCEPT) }

            getCollectionObjectVersionsProvider.execute(ProviderRequest(
                    mapOf(
                            Pair("apiRoot", apiRoot),
                            Pair("collectionId", collectionId),
                            Pair("objectId", objectId)
                    ),
                    contentType,
                    acceptType,
                    request))

        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }

        }.map {
            HttpResponse.ok(it.responseBody)
                    .contentType(it.contentType as MediaType)
                    .addAdditionalHeaders(it.additionalHeaders)
        }
    }


    @io.micronaut.http.annotation.Error
    fun taxiiException(request: HttpRequest<*>, exception: TaxiiException): HttpResponse<Error> {
        log.error("Roots Controller Error", exception)
        requireNotNull(exception.taxiError.httpStatus, lazyMessage = { "TAXII Exception was missing HTTP Status Code" })
        return HttpResponse.status<Error>(HttpStatus.valueOf(exception.taxiError.httpStatus.toInt()))
                .contentType(TaxiiMediaType.taxii_2_1 as MediaType)
                .body(exception.taxiError)
    }

}