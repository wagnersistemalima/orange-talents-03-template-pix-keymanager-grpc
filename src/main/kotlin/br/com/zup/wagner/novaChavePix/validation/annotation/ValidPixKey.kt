package br.com.zup.wagner.novaChavePix.validation.annotation


import br.com.zup.wagner.novaChavePix.request.NovaChavePixRequest
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(
    val message: String = "chave pix invalida (\${validatedValue.tipoChave})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)

@Singleton
class ValidPixKeyValidator : ConstraintValidator<ValidPixKey, NovaChavePixRequest> {
    override fun isValid(value: NovaChavePixRequest?, context: ConstraintValidatorContext?): Boolean {
        if (value?.tipoChave == null) {
            return false
        }

        return value.tipoChave.valida(value.valorChave)
    }

}
