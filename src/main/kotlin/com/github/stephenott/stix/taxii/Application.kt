package com.github.stephenott.stix.taxii

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License

@OpenAPIDefinition(
        info = Info(
                title = "TAXII Client API",
                version = "0.5",
                description = "TAXII Client API (2.1)",
                license = License(name = "--", url = "http://github.com/stephenott"),
                contact = Contact(url = "http://github.com/stephenott/TAXII-Server", name = "StephenOTT")
        )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("com.github.stephenott.stix.taxii")
                .mainClass(Application.javaClass)
                .start()
    }
}
