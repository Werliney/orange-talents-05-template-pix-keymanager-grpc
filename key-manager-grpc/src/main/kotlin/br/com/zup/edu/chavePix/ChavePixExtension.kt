package br.com.zup.edu.chavePix

import br.com.zup.edu.CadastraChavePixRequest

fun CadastraChavePixRequest.toModel(): ChavePixRequest {
    return ChavePixRequest(
       idCliente = idCliente,
        tipoChave = TipoChaveData.valueOf(tipoChave.name),
        valorChave = valorChave,
        tipoConta = TipoContaData.valueOf(tipoConta.name)
    )
}