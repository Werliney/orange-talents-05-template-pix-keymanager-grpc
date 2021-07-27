package br.com.zup.edu.chavePix.detalhaChaveKeyManager

import br.com.zup.edu.ListaChaveKeyManagerRequest

fun ListaChaveKeyManagerRequest.toModel(): DetalhaKeyManagerChaveRequest {
    return DetalhaKeyManagerChaveRequest(
        idCliente = idCliente,
        idPix = idPix
    )
}