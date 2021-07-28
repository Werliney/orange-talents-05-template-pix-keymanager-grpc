package br.com.zup.edu.chavePix.listaChaves

import br.com.zup.edu.ListaChavesDoClienteRequest

fun ListaChavesDoClienteRequest.toModel(): ListaChavesRequest {
    return ListaChavesRequest(
        idCliente = idCliente
    )
}