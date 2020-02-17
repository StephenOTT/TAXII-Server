package com.github.stephenott.stix.taxii.security

import com.github.stephenott.stix.taxii.controller.AccessException
import com.github.stephenott.stix.taxii.controller.AuthenticationException
import com.github.stephenott.stix.taxii.controller.Headers
import com.github.stephenott.stix.taxii.controller.TaxiiMediaType
import com.github.stephenott.stix.taxii.domain.Error
import io.micronaut.context.annotation.Replaces
import io.micronaut.core.async.publisher.Publishers
import io.micronaut.http.*
import io.micronaut.security.handlers.HttpStatusCodeRejectionHandler
import org.reactivestreams.Publisher
import javax.inject.Singleton


@Singleton
@Replaces(HttpStatusCodeRejectionHandler::class)
class AuthenticationRejectionHandler : HttpStatusCodeRejectionHandler() {

    override fun reject(request: HttpRequest<*>?, forbidden: Boolean): Publisher<MutableHttpResponse<*>> {
        return Publishers.just(handleRejection(forbidden))

    }

    private fun handleRejection(forbidden: Boolean): MutableHttpResponse<*> {
        return if (forbidden) {
            HttpResponse.status<Error>(HttpStatus.FORBIDDEN)
                    .contentType(TaxiiMediaType.taxii_2_1 as MediaType)
                    .body(AccessException("403").taxiError)
        } else {
            HttpResponse.status<Error>(HttpStatus.UNAUTHORIZED)
                    .header(Headers.WWW_AUTHENTICATE, "Basic Realm=\"Restricted\"")
                    .contentType(TaxiiMediaType.taxii_2_1 as MediaType)
                    .body(AuthenticationException("401").taxiError)
        }
    }
}