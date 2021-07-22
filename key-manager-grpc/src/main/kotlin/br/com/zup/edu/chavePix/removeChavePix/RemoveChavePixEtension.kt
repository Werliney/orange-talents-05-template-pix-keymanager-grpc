package br.com.zup.edu.chavePix.removeChavePix

import br.com.zup.edu.RemoveChavePixRequest


fun RemoveChavePixRequest.toModel(): RemoveChaveRequest {
    return RemoveChaveRequest(
        idPix = idPix,
        idCliente = idCliente,
    )
}