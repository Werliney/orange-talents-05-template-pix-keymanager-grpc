package br.com.zup.edu.validacoes

import br.com.zup.edu.chavePix.ChavePixRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ChaveValidator::class])
annotation class Chave(
    val message: String = "chave com formato inválido"
)

@Singleton
class ChaveValidator: ConstraintValidator<Chave, ChavePixRequest> {
    override fun isValid(
        value: ChavePixRequest?,
        annotationMetadata: AnnotationValue<Chave>,
        context: ConstraintValidatorContext
    ): Boolean {

        if (value?.tipoChave == null) {
            return false
        }

        return value.tipoChave.valida(value.valorChave)
    }

}