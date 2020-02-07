package com.github.stephenott.stix.taxii.controller

import com.github.stephenott.stix.taxii.domain.Error

/**
 * Generic TaxiiException
 */
open class TaxiiException(val taxiError: Error): RuntimeException(){}


class RequestException(httpStatus: String, taxiError: Error = Error(
        title = "Did not understand request or filter",
        httpStatus = httpStatus
)): TaxiiException(taxiError){}

class FilterException(httpStatus: String, taxiError: Error = Error(
        title = "Did not understand request or filter",
        httpStatus = httpStatus
)): TaxiiException(taxiError){}

class AuthenticationException(httpStatus: String, taxiError: Error = Error(
        title = "Could not Authenticate the client",
        httpStatus = httpStatus
)): TaxiiException(taxiError){}


class AccessException(httpStatus: String, taxiError: Error = Error(
        title = "Client does not have access to this resource",
        httpStatus = httpStatus
)): TaxiiException(taxiError){}


class NotFoundException(httpStatus: String, taxiError: Error = Error(
        title = "Could not find the requested resource",
        httpStatus = httpStatus
)): TaxiiException(taxiError){}


class AccessOrNotFoundException(httpStatus: String, taxiError: Error = Error(
        title = "Could not find the requested resource or client does not have access to the requested resource",
        httpStatus = httpStatus
)): TaxiiException(taxiError){}


class MediaTypeException(httpStatus: String, taxiError: Error = Error(
        title = "The Media Type provided is invalid",
        httpStatus = httpStatus
)): TaxiiException(taxiError){}

class NotImplementedException(taxiError: Error = Error(
        title = "This TAXII Endpoint is not implemented.",
        httpStatus = "501"
)): TaxiiException(taxiError){}
