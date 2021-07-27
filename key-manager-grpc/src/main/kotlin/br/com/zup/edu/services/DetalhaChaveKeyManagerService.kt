package br.com.zup.edu.services

import br.com.zup.edu.chavePix.detalhaChaveKeyManager.DetalhaKeyManagerChaveRequest
import br.com.zup.edu.chavePix.detalhaChaveKeyManager.DetalhaKeyManagerChaveResponse
import br.com.zup.edu.exceptions.ChaveNaoExistenteException
import br.com.zup.edu.repository.ChavePixRepository
import br.com.zup.edu.servicosExternos.BcbClient
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class DetalhaChaveKeyManagerService(val chavePixRepository: ChavePixRepository, val bcbClient: BcbClient) {

    fun consulta(@Valid listaChave: DetalhaKeyManagerChaveRequest): DetalhaKeyManagerChaveResponse {

        val chavePix = chavePixRepository.findChaveByIdAndCliente(listaChave.idPix, listaChave.idCliente)

        if (chavePix.isEmpty) {
            throw ChaveNaoExistenteException("A chave com id: ${listaChave.idPix} não existe")
        }

        val chave = chavePix.get()

        val listaChaveResponse = bcbClient.consultaChave(chave.valorChave)

        if (listaChaveResponse.status != HttpStatus.OK) {
            throw ChaveNaoExistenteException("A chave com id: ${listaChave.idPix} não existe no Banco Central do Brasil (BCB)")
        }

        return DetalhaKeyManagerChaveResponse(
            chave.id!!,
            chave.idCliente,
            chave.tipoChave,
            chave.valorChave,
            chave.donoDaConta.nome,
            chave.donoDaConta.cpf,
            chave.donoDaConta.nomeInstituicao,
            chave.donoDaConta.agencia,
            chave.donoDaConta.numeroConta,
            chave.tipoConta,
            listaChaveResponse.body()!!.createdAt
        )

    }
}