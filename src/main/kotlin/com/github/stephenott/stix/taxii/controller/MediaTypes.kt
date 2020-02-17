package com.github.stephenott.stix.taxii.controller

import io.micronaut.core.value.OptionalValues
import io.micronaut.http.MediaType
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.nio.charset.Charset
import java.util.*

@Schema(name = "TaxiiMediaType", type = "string", format = "taxii-media-type", description = "A Media Type that is for TAXII content such as application/taxii+json;version=2.1", example = "application/taxii+json;version=2.1")
interface TaxiiMediaType{
    companion object {
        const val APPLCATION_JSON_TAXII: String = "application/taxii+json"
        const val TAXII_VERSION_2_0: String = "2.0"
        const val TAXII_VERSION_2_1: String = "2.1"
        const val APPLCATION_JSON_TAXII_VERSION_2_0: String = "$APPLCATION_JSON_TAXII;version=$TAXII_VERSION_2_0"
        const val APPLCATION_JSON_TAXII_VERSION_2_1: String = "$APPLCATION_JSON_TAXII;version=$TAXII_VERSION_2_1"

        val taxii_2_1: TaxiiMediaType = TaxiiMedia(TAXII_VERSION_2_1)
        val taxii_2_0: TaxiiMediaType = TaxiiMedia(TAXII_VERSION_2_0)
        val wildCardMediaType: TaxiiMediaType = TaxiiMedia("*/*", true)

        /**
         * If rawValue does not contain a version then will default to TAXII 2.1 version
         */
        @Throws(IllegalArgumentException::class)
        fun validateTaxiiMediaType(rawValue: String?): TaxiiMediaType{
            return if (rawValue != null){
                val rawMediaType = TaxiiMedia(rawValue, true)

                if (rawMediaType == wildCardMediaType){
                    TaxiiMediaType.taxii_2_1

                } else {
                    val defaultTaxiiMediaType = TaxiiMediaType.taxii_2_1 as MediaType
                    val supportedMediaTypes = listOf(TaxiiMediaType.taxii_2_0, TaxiiMediaType.taxii_2_1) // @TODO make configurable so more supported types are provided

                    //@TODO review is lowercase of values is required
                    require(rawMediaType.name == defaultTaxiiMediaType.name,
                            lazyMessage = { "Is not a supported Taxii Media Content Type name" })
                    require(rawMediaType.version in supportedMediaTypes.map { (it as TaxiiMedia).version },
                            lazyMessage = { "Not a supported TAXII Version" }) //@TODO make configurable to configure if default is supported or client must submit a version

                    TaxiiMediaType.taxii_2_1
                }
            } else {
                TaxiiMediaType.taxii_2_1
            }
        }

    }
}
class TaxiiMedia(rawText: String, isRaw: Boolean): MediaType(rawText),
        TaxiiMediaType {

    constructor(version: String):this("${TaxiiMediaType.APPLCATION_JSON_TAXII};version=${version}", false)

    override fun hashCode(): Int {
        return super<MediaType>.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return super<MediaType>.equals(other)
    }

    override fun getName(): String {
        return super.getName()
    }

    override fun toString(): String {
        return super<MediaType>.toString()
    }

    override fun getType(): String {
        return super.getType()
    }

    override fun getSubtype(): String {
        return super.getSubtype()
    }

    override fun getExtension(): String {
        return super.getExtension()
    }

    override fun getCharset(): Optional<Charset> {
        return super.getCharset()
    }

    override fun getQualityAsNumber(): BigDecimal {
        return super.getQualityAsNumber()
    }

    override fun getParameters(): OptionalValues<String> {
        return super.getParameters()
    }

    override fun getQuality(): String {
        return super.getQuality()
    }

    /**
     * Returns 2.1 as the default if null/not present
     */
    override fun getVersion(): String {
        return parameters.getOrDefault("version", TaxiiMediaType.TAXII_VERSION_2_1 )
    }

    override fun get(index: Int): Char {
        return super.get(index)
    }

    override fun isTextBased(): Boolean {
        return super.isTextBased()
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return super.subSequence(startIndex, endIndex)
    }

    override val length: Int
        get() = super.length
}

@Schema(name = "StixMediaType", type = "string", format = "stix-media-type", description = "A Media Type that is for STIX content such as application/stix+json;version=2.1", example = "application/stix+json;version=2.1")
interface StixMediaType{
    companion object{
        const val APPLCATION_JSON_STIX: String = "application/stix+json"
        const val STIX_VERSION_2_0: String = "2.0"
        const val STIX_VERSION_2_1: String = "2.1"
        const val APPLCATION_JSON_STIX_VERSION_2_0: String = "$APPLCATION_JSON_STIX;version=$STIX_VERSION_2_0"
        const val APPLCATION_JSON_STIX_VERSION_2_1: String = "$APPLCATION_JSON_STIX;version=$STIX_VERSION_2_1"

        val stix_2_1: StixMediaType = StixMedia(STIX_VERSION_2_1)
        val stix_2_0: StixMediaType = StixMedia(STIX_VERSION_2_0)
    }
}
class StixMedia(version: String):
        MediaType("${TaxiiMediaType.APPLCATION_JSON_TAXII};version=${version}"),
        StixMediaType{

    override fun hashCode(): Int {
        return super<MediaType>.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return super<MediaType>.equals(other)
    }

    override fun getName(): String {
        return super.getName()
    }

    override fun toString(): String {
        return super<MediaType>.toString()
    }

    override fun getType(): String {
        return super.getType()
    }

    override fun getSubtype(): String {
        return super.getSubtype()
    }

    override fun getExtension(): String {
        return super.getExtension()
    }

    override fun getCharset(): Optional<Charset> {
        return super.getCharset()
    }

    override fun getQualityAsNumber(): BigDecimal {
        return super.getQualityAsNumber()
    }

    override fun getParameters(): OptionalValues<String> {
        return super.getParameters()
    }

    override fun getQuality(): String {
        return super.getQuality()
    }

    /**
     * Return 2.1 if null/not present
     */
    override fun getVersion(): String {
        return parameters.getOrDefault("version", StixMediaType.STIX_VERSION_2_1)
    }

    override fun get(index: Int): Char {
        return super.get(index)
    }

    override fun isTextBased(): Boolean {
        return super.isTextBased()
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return super.subSequence(startIndex, endIndex)
    }

    override val length: Int
        get() = super.length
}




