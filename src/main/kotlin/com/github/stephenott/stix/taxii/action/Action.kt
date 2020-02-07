package com.github.stephenott.stix.taxii.action

interface Action<out T> {

    fun execute():T
}