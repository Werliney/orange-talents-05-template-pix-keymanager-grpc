package br.com.zup.edu.chavePix.cadastraChavePix

import br.com.zup.edu.CadastraChavePixRequest
import br.com.zup.edu.chavePix.TipoChaveData
import br.com.zup.edu.chavePix.TipoContaData

fun CadastraChavePixRequest.toModel(): ChavePixRequest {
    return ChavePixRequest(
       idCliente = idCliente,
        tipoChave = TipoChaveData.valueOf(tipoChave.name),
        valorChave = valorChave,
        tipoConta = TipoContaData.valueOf(tipoConta.name)
    )
}