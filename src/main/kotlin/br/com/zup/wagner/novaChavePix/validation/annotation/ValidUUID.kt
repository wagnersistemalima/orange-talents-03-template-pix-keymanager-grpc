package br.com.zup.wagner.novaChavePix.validation.annotation

import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import kotlin.reflect.KClass

@ReportAsSingleViolation
@Constraint(validatedBy = [])
@Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$",
                     flags = [Pattern.Flag.CASE_INSENSITIVE])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class ValidUUID(
    val message: String = "não é um formato valido de UUID",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)
