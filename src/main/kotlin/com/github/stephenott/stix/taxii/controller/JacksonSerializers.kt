package com.github.stephenott.stix.taxii.controller

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import javax.inject.Singleton

/**
 * Automatically picked up by micronaut because of the `@Singleton`
 */
@Singleton
class TaxiiMediaTypeSerializer: StdSerializer<TaxiiMediaType>(TaxiiMediaType::class.java) {
    override fun serialize(value: TaxiiMediaType, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString((value as TaxiiMedia).toString())
    }
}

/**
 * Automatically picked up by micronaut because of the `@Singleton`
 */
@Singleton
class TaxiiMediaTypeDeserializer: StdDeserializer<TaxiiMediaType>(TaxiiMediaType::class.java){
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): TaxiiMediaType {
        return TaxiiMedia(p.text, false) //@TODO add taxii validation
    }
}