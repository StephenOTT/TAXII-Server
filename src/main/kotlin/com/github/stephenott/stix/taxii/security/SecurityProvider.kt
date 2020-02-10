package com.github.stephenott.stix.taxii.security

import io.micronaut.security.authentication.*
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.util.*
import javax.inject.Singleton


@Singleton
class SecurityProvider : AuthenticationProvider {
    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>): Publisher<AuthenticationResponse> {
        return if (
            //@TODO replace with Auth provider
                authenticationRequest.identity == "sherlock" &&
                authenticationRequest.secret == "password") {

            Flowable.just(UserDetails(authenticationRequest.identity as String, ArrayList()))
        } else {
            Flowable.just(AuthenticationFailed())
        }
    }
}