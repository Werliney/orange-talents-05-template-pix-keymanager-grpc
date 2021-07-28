package br.com.zup.edu.chavePix.listaChaves

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class ListaChavesRequest(@field:NotBlank val idCliente: String) {
}