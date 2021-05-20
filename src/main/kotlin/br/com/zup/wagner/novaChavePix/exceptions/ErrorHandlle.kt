package br.com.zup.wagner.novaChavePix.exceptions

import io.micronaut.aop.Around


@Around
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ErrorHandlle
