package br.com.zup.edu.chavePix.removeChavePix

import com.sun.istack.NotNull
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RemoveChaveRequest(
    @field:NotNull val idPix: Long,
    @field:NotBlank val idCliente: String
)
