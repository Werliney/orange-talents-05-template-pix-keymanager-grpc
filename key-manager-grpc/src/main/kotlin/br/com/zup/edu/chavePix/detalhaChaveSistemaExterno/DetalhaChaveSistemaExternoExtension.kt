package br.com.zup.edu.chavePix.detalhaChaveSistemaExterno

import br.com.zup.edu.ListaChaveSistemaExternoRequest

fun ListaChaveSistemaExternoRequest.toModel(): DetalhaChaveSistemaExternoRequest {
    return DetalhaChaveSistemaExternoRequest(
        chave = this.chave
    )
}
