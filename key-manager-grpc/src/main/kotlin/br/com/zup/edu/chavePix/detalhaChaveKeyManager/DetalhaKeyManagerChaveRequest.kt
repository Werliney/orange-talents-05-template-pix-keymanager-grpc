package br.com.zup.edu.chavePix.detalhaChaveKeyManager

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class DetalhaKeyManagerChaveRequest(
    @field:NotBlank val idCliente: String,
    @field:NotNull val idPix: Long
) {
}