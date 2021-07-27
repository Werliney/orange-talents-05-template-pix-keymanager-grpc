package br.com.zup.edu.chavePix.detalhaChaveSistemaExterno

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class DetalhaChaveSistemaExternoRequest(val chave: String) {
}