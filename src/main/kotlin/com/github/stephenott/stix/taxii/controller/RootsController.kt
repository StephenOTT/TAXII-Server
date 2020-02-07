package com.github.stephenott.stix.taxii.controller

import com.github.stephenott.stix.taxii.action.actions.*
import com.github.stephenott.stix.taxii.domain.*
import com.github.stephenott.stix.taxii.domain.Collection
import com.github.stephenott.stix.taxii.domain.Error
import com.github.stephenott.stix.taxii.domain.Status
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import io.reactivex.Single
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import java.time.Instant
import javax.inject.Inject

@Controller("/{apiRoot}/")
@Validated
open class RootsController() {

    @Inject
    lateinit var getApiRootInformationProvider: GetApiRootInformationProvider

    @Inject
    lateinit var getStatusProvider: GetStatusProvider

    @Inject
    lateinit var getCollectionsProvider: GetCollectionsProvider

    @Inject
    lateinit var getCollectionProvider: GetCollectionProvider

    @Inject
    lateinit var getCollectionManifestProvider: GetCollectionManifestProvider

    @Inject
    lateinit var getCollectionObjectsProvider: GetCollectionObjectsProvider

    @Inject
    lateinit var addCollectionObjectsProvider: AddCollectionObjectsProvider

    @Inject
    lateinit var getCollectionObjectProvider: GetCollectionObjectProvider

    @Inject
    lateinit var deleteCollectionObjectProvider: DeleteCollectionObjectProvider

    @Inject
    lateinit var getCollectionObjectVersionsProvider: GetCollectionObjectVersionsProvider


    @Get(value = "/", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @Operation(
            summary = "Get information about a specific API Root",
            description = "This Endpoint provides general information about an API Root, which can be used to help users and clients decide whether and how they want to interact with it. Multiple API Roots \u200BMAY\u200B be hosted on a single TAXII Server. Often, an API Root represents a single trust group.",
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root")
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = ApiRoot::class))]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The Discovery service is not found, or the client does not have access to the resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getApiRoot(apiRoot: String): Single<HttpResponse<ApiRoot>> {
        return Single.fromCallable {
            getApiRootInformationProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map {
            HttpResponse.ok(it)
        }
    }

    @Get(value = "/status/{statusId}", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @Operation(
            summary = "Get status information for a specific status ID",
            description = "This Endpoint provides information about the status of a previous request. In TAXII 2.1, the only request that can be monitored is one to add objects to a Collection. It is typically used by TAXII Clients to monitor a POST request that they made in order to take action when it is complete.",
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root"),
                Parameter(name = "statusId", `in` = ParameterIn.PATH, required = true, description = "the \u200Bidentifier\u200B of the status message being requested")
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Status::class))]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root or Status ID are not found, or the client does not have access to the resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getStatus(apiRoot: String, statusId: String): Single<HttpResponse<Status>> {
        return Single.fromCallable {
            getStatusProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map {
            HttpResponse.ok(it)
        }
    }


    @Get(value = "/collections", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @Operation(
            summary = "Get information about all collections",
            description = "This Endpoint provides information about the Collections hosted under this API Root. This is similar to the response to get a Collection, but rather than providing information about one Collection it provides information about all of the Collections. Most importantly, it provides the Collection's \u200Bid\u200B, which is used to request objects or manifest entries from the Collection.",
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root")
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Collections::class))]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this collections resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root is not found, or the client does not have access to the collections resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollections(apiRoot: String): Single<HttpResponse<Collections>> {
        return Single.fromCallable {
            getCollectionsProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map {
            HttpResponse.ok(it)
        }
    }


    @Get(value = "/collections/{collectionId}", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @Operation(
            summary = "Get information about a specific collection",
            description = "This Endpoint provides general information about a Collection, which can be used to help users and clients decide whether and how they want to interact with it. For example, it will tell clients what it's called and what permissions they have to it.",
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root"),
                Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the \u200Bidentifier\u200B of the Collection being requested")
            ]
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Collections::class))]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this collections resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root or Collection ID are not found, or the client does not have access to the collection resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollection(apiRoot: String, collectionId: String): Single<HttpResponse<Collection>> {
        return Single.fromCallable {
            getCollectionProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map {
            HttpResponse.ok(it)
        }
    }


    @Get(value = "/collections/{collectionId}/manifest{?qParams*}", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @ExplosionTarget(["match"])
    @Operation(
            summary = "Get manifest information about the contents of a specific collection",
            description = "This Endpoint retrieves a manifest about the objects in a Collection. It supports filtering identical to the get objects Endpoint but rather than returning the object itself it returns metadata about the object. It can be used to retrieve metadata to decide whether it's worth retrieving the actual objects.",
            parameters = [
                Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root"),
                Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the \u200Bidentifier\u200B of the Collection being requested"),
                Parameter(name = "added_after", `in` = ParameterIn.QUERY, description = "a single timestamp", example = "..."),
                Parameter(name = "limit", `in` = ParameterIn.QUERY, description = "a single integer", example = "..."),
                Parameter(name = "next", `in` = ParameterIn.QUERY, description = "a single string", example = "..."),
                Parameter(name = "match[id]", `in` = ParameterIn.QUERY, description = "an id(s) of an object", example = "..."),
                Parameter(name = "match[type]", `in` = ParameterIn.QUERY, description = "the type(s) of an object", example = "..."),
                Parameter(name = "match[version]", `in` = ParameterIn.QUERY, description = "the version(s) of an object", example = "..."),
                Parameter(name = "match[spec_version]", `in` = ParameterIn.QUERY, description = "the specification version(s)", example = "...")
            ]
    )
    //@TODO required Headers Accept: application/taxii+json;version=2.1,application/stix+json;version=2.1
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Manifest::class))],
                    headers = [
                        Header(name = Headers.X_TAXII_DATE_ADDED_FIRST, required = true, description = "timestamp", schema = Schema(implementation = Instant::class)),
                        Header(name = Headers.X_TAXII_DATE_ADDED_LAST, required = true, description = "timestamp", schema = Schema(implementation = Instant::class))
                    ]
            ),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this manifest resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root or Collection ID are not found, or the client does not have access to the manifest resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollectionManifest(apiRoot: String, collectionId: String, @QueryValueExploded qParams: Map<String, Any>?): Single<HttpResponse<Manifest>> {
        return Single.fromCallable {
            getCollectionManifestProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map {
            HttpResponse.ok(it)
        }
    }


    @Get(value = "/collections/{collectionId}/objects{?qParams*}", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @ExplosionTarget(["match"])
    @Operation(
            summary = "Get all objects from a collection",
            description = "This Endpoint retrieves objects from a Collection. Clients can search for objects in the Collection, retrieve all objects in a Collection, or paginate through objects in the Collection."
    )
    @Parameters(
            Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root"),
            Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the \u200Bidentifier\u200B of the Collection being requested"),
            Parameter(name = "added_after", `in` = ParameterIn.QUERY, description = "a single timestamp", example = "..."),
            Parameter(name = "limit", `in` = ParameterIn.QUERY, description = "a single integer", example = "..."),
            Parameter(name = "next", `in` = ParameterIn.QUERY, description = "a single string", example = "..."),
            Parameter(name = "match[id]", `in` = ParameterIn.QUERY, description = "an id(s) of an object", example = "..."),
            Parameter(name = "match[type]", `in` = ParameterIn.QUERY, description = "the type(s) of an object", example = "..."),
            Parameter(name = "match[version]", `in` = ParameterIn.QUERY, description = "the version(s) of an object", example = "..."),
            Parameter(name = "match[spec_version]", `in` = ParameterIn.QUERY, description = "the specification version(s)", example = "...")
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Envelop::class))],
                    headers = [
                        Header(name = Headers.X_TAXII_DATE_ADDED_FIRST, required = true, description = "timestamp"),
                        Header(name = Headers.X_TAXII_DATE_ADDED_LAST, required = true, description = "timestamp")
                    ]
            ),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this objects resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root or Collection ID are not found, or the client does not have access to the objects resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollectionObjects(apiRoot: String, collectionId: String, @QueryValueExploded qParams: Map<String, Any>?): Single<HttpResponse<Envelop>> {
        return Single.fromCallable {
            getCollectionObjectsProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map {
            HttpResponse.ok(it)
        }
    }

    @Post(value = "/collections/{collectionId}/objects", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @Operation(
            summary = "Add a new object to a specific collection",
            description = "This Endpoint adds objects to a Collection."
    )
    @Parameters(
            Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root"),
            Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the \u200Bidentifier\u200B of the Collection being requested")
    )
    @ApiResponses(
            ApiResponse(responseCode = "202", description = "The request was successful accepted", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Envelop::class))]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to write to this objects resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root or Collection ID are not found, or the client can not write to this objects resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "413", description = "The POSTed payload exceeds the max_content_length of the API Root", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "415", description = "The client attempted to POST a payload with a content type the server does not support", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "422", description = "The object type of version is not supported of could not be processed.  This can happen, for example, when sending a version of STIX that this TAXII Server does not support and cannot process, when sending a malformed body, or other unprocessable content", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun addCollectionObjects(apiRoot: String, collectionId: String, @Body envelop: Envelop): Single<HttpResponse<Status>> {
        return Single.fromCallable {
            addCollectionObjectsProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map {
            HttpResponse.ok(it) // @TODO **!! update with .accepted(). But currently does not allow a T body!
        }
    }

    @Get(value = "/collections/{collectionId}/objects/{objectId}{?qParams*}", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @ExplosionTarget(["match"])
    @Operation(
            summary = "Get a specific object from a collection",
            description = "This Endpoint gets an object from a Collection by its \u200Bid\u200B. It can be thought of as a search where the match[id]\u200B parameter is set to the \u200B{object-id}\u200B in the path. For STIX 2 objects, the \u200B{object-id}\u200B \u200BMUST be the STIX \u200Bid\u200B."
    )
    @Parameters(
            Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root"),
            Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the \u200Bidentifier\u200B of the Collection being requested"),
            Parameter(name = "objectId", `in` = ParameterIn.PATH, required = true, description = "the ID of the object being requested"),
            Parameter(name = "added_after", `in` = ParameterIn.QUERY, description = "a single timestamp", example = "..."),
            Parameter(name = "limit", `in` = ParameterIn.QUERY, description = "a single integer", example = "..."),
            Parameter(name = "next", `in` = ParameterIn.QUERY, description = "a single string", example = "..."),
            Parameter(name = "match[version]", `in` = ParameterIn.QUERY, description = "the version(s) of an object", example = "..."),
            Parameter(name = "match[spec_version]", `in` = ParameterIn.QUERY, description = "the specification version(s)", example = "...")
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Envelop::class))],
                    headers = [
                        Header(name = Headers.X_TAXII_DATE_ADDED_FIRST, required = true, description = "timestamp"),
                        Header(name = Headers.X_TAXII_DATE_ADDED_LAST, required = true, description = "timestamp")
                    ]
            ),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this objects resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root, Collection ID and/or Object ID are not found, or the client does not have access to the object resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollectionObject(apiRoot: String, collectionId: String, objectId: String, @QueryValueExploded qParams: Map<String, Any>?): Single<HttpResponse<Envelop>> {
        return Single.fromCallable {
            getCollectionObjectProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map {
            HttpResponse.ok(it)
        }
    }

    @Delete(value = "/collections/{collectionId}/objects/{objectId}{?qParams}}", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @ExplosionTarget(["match"])
    @Operation(
            summary = "Delete a specific object from a collection",
            description = "This Endpoint deletes an object from a Collection by its \u200Bid\u200B. For STIX 2 objects, the \u200B{object-id}\u200B \u200BMUST be the STIX \u200Bid\u200B."
    )
    @Parameters(
            Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root"),
            Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the \u200Bidentifier\u200B of the Collection being requested"),
            Parameter(name = "objectId", `in` = ParameterIn.PATH, required = true, description = "the ID of the object being deleted"),
            Parameter(name = "match[version]", `in` = ParameterIn.QUERY, description = "the version(s) of an object", example = "..."),
            Parameter(name = "match[spec_version]", `in` = ParameterIn.QUERY, description = "the specification version(s)", example = "...")
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1)]),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client has access to the object, but not to delete it", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root, Collection ID and/or Object ID are not found, or the client does not have access to the object", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun deleteCollectionObject(apiRoot: String, collectionId: String, objectId: String, @QueryValueExploded qParams: Map<String, Any>?): Single<HttpResponse<Unit>> {
        return Single.fromCallable {
            deleteCollectionObjectProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException) {
                Single.error(it)
            } else {
                Single.error(IllegalStateException(it))
            }
        }.map {
            HttpResponse.ok(it)
        }
    }

    @Get(value = "/collections/{collectionId}/objects/{objectId}/versions{?qParams*}", processes = [MediaTypes.APPLICATION_JSON_TAXII_2_1])
    @ExplosionTarget(["match"])
    @Operation(
            summary = "Get a list of object versions from a collection",
            description = "This Endpoint retrieves a list of one or more versions of an object in a Collection. This list can be used to decide whether it's worth retrieving the actual objects, or if new versions have been added. If a STIX object is not versioned (and therefore does not have a modified timestamp), the server \u200BMUST \u200Buse created\u200B timestamp."
    )
    @Parameters(
            Parameter(name = "apiRoot", `in` = ParameterIn.PATH, required = true, description = "the base URL of the API Root"),
            Parameter(name = "collectionId", `in` = ParameterIn.PATH, required = true, description = "the \u200Bidentifier\u200B of the Collection being requested"),
            Parameter(name = "objectId", `in` = ParameterIn.PATH, required = true, description = "the ID of the object being requested"),
            Parameter(name = "added_after", `in` = ParameterIn.QUERY, description = "a single timestamp", example = "..."),
            Parameter(name = "limit", `in` = ParameterIn.QUERY, description = "a single integer", example = "..."),
            Parameter(name = "next", `in` = ParameterIn.QUERY, description = "a single string", example = "..."),
            Parameter(name = "match[spec_version]", `in` = ParameterIn.QUERY, description = "the specification version(s)", example = "...")
    )
    @ApiResponses(
            ApiResponse(responseCode = "200", description = "The request was successful", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Versions::class))],
                    headers = [
                        Header(name = Headers.X_TAXII_DATE_ADDED_FIRST, required = true, description = "timestamp"),
                        Header(name = Headers.X_TAXII_DATE_ADDED_LAST, required = true, description = "timestamp")
                    ]
            ),
            ApiResponse(responseCode = "400", description = "The server did not understand the request or filter parameters", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "401", description = "The client needs to authenticate", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "403", description = "The client does not have access to this objects resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "404", description = "The API Root, Collection ID and/or Object ID are not found, or the client does not have access to the object resource", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))]),
            ApiResponse(responseCode = "406", description = "The media type provided in the Accept header is invalid", content = [Content(mediaType = MediaTypes.APPLICATION_JSON_TAXII_2_1, schema = Schema(implementation = Error::class))])
    )
    open fun getCollectionObjectVersions(apiRoot: String, collectionId: String, objectId: String, @QueryValueExploded qParams: Map<String, Any>?): Single<HttpResponse<Versions>> {
        return Single.fromCallable {
            getCollectionObjectVersionsProvider.execute()
        }.onErrorResumeNext {
            if (it is TaxiiException) {
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
        exception.printStackTrace() // @TODO
        requireNotNull(exception.taxiError.httpStatus, lazyMessage = {"TAXII Exception was missing HTTP Status Code"})
        return HttpResponseFactory.INSTANCE.status(HttpStatus.valueOf(exception.taxiError.httpStatus.toInt()), exception.taxiError)
    }

}