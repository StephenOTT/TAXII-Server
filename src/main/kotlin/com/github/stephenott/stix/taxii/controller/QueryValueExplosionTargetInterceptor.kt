package com.github.stephenott.stix.taxii.controller

import io.micronaut.aop.Around
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.context.annotation.Type
import io.micronaut.http.annotation.QueryValue
import org.jetbrains.kotlin.utils.addToStdlib.cast
import org.slf4j.LoggerFactory
import javax.inject.Singleton

/**
 * Applied to the method that contains a ```@QueryValueExploded``` annotation on the exploded param argument.
 * Set the value to query params that mach ```text[text]``` format: ```match[id]```, ```match[dog]```, etc
 *
 * Example: Consider you support multiple query params with a pattern: ```match[AFieldName]```.
 *
 * The Explosion target is configured with ```@ExplosionTarget(["match"])```, and the resulting ```@QueryValueExploded
 * params Map<String, Any>``` in the method arguments will result in a transformed map that contains a key/value such
 * as ```{match:{aFieldName: some value}}```
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Around
@Type(QueryValueExplosionTargetInterceptor::class)
annotation class ExplosionTarget(
        val value: Array<String>
)


/**
 * Provides a meta-annotation for ```@QueryValue``` that is to be used when ```@QueryValue``` is being used on exploded param arguments,
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@QueryValue
annotation class QueryValueExploded

/**
 * AOP interceptor for the ```@ExplosionTarget``` annotation
 */
@Singleton
class QueryValueExplosionTargetInterceptor : MethodInterceptor<Any, Any> {

    private val log = LoggerFactory.getLogger(QueryValueExplosionTargetInterceptor::class.java)

    override fun intercept(context: MethodInvocationContext<Any, Any>): Any {

        val targetParams = context.getDeclaredAnnotation(ExplosionTarget::class.java)!!["value", Array<String>::class.java].get()

        context.parameters.entries.stream().filter {
            it.value.isAnnotationPresent(QueryValueExploded::class.java) && it.value.type.isAssignableFrom(Map::class.java)
        }.forEach { eqp ->
            targetParams.forEach { targetParam ->
                val paramsRaw = eqp.value.value.cast<MutableMap<String, Any>>()

                val paramsTransformed = mutableMapOf<String, Any>()
                val deletions = mutableListOf<String>()

                //@TODO inject the regex with the targeted param rather than a generic catch all.
                val regex = Regex("(?<param>^[a-zA-Z\\S]+)\\[(?<field>[^]]\\S+)]$")

                paramsRaw.forEach {
                    val result: MatchResult? = regex.matchEntire(it.key)
                    if (result != null) {
                        val param = result.groups["param"]!!.value
                        val field = result.groups["field"]!!.value

                        if (targetParam == param) {
                            deletions.add(it.key)
                            paramsTransformed[field] = it.value
                        } else {
                            log.debug("Found regex match for '$param', but it was not targeted by annotation configuration.")
                        }

                    } else {
                        log.debug("Did not find regex match for param '${it.key}'.")
                    }
                }

                deletions.forEach {
                    paramsRaw.remove(it)
                }

                paramsRaw[targetParam] = paramsTransformed.toMap()
            }
        }
        return context.proceed()
    }
}