package br.com.zup.edu.services

import br.com.zup.edu.chavePix.TipoContaData
import br.com.zup.edu.chavePix.detalhaChaveSistemaExterno.DetalhaChaveSistemaExternoRequest
import br.com.zup.edu.chavePix.detalhaChaveSistemaExterno.DetalhaChaveSistemaExternoResponse
import br.com.zup.edu.exceptions.ChaveNaoExistenteException
import br.com.zup.edu.repository.ChavePixRepository
import br.com.zup.edu.servicosExternos.AccountType
import br.com.zup.edu.servicosExternos.BcbClient
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class DetalhaChaveSistemaExternoService(
    val chavePixRepository: ChavePixRepository,
    val bcbClient: BcbClient
) {

    fun consulta(@Valid detalhaChave: DetalhaChaveSistemaExternoRequest): DetalhaChaveSistemaExternoResponse {

        val chave = chavePixRepository.findByValorChave(detalhaChave.chave)

        if (chave.isEmpty) {
            throw ChaveNaoExistenteException("Fodeu aqui")
        }

        if (chave.isEmpty) {
            val listaChaveResponse = bcbClient.consultaChave(detalhaChave.chave)

            if (listaChaveResponse.status != HttpStatus.OK) {
                throw ChaveNaoExistenteException("A chave com o valor: ${detalhaChave.chave} não existe no Banco Central do Brasil (BCB)")
            }

            val dados = listaChaveResponse.body()

            return DetalhaChaveSistemaExternoResponse(
                dados.keyType,
                dados.key,
                dados.bankAccount.participant,
                dados.bankAccount.branch,
                dados.bankAccount.accountNumber,
                tipoConta = if (dados.bankAccount.accountType == AccountType.CACC) TipoContaData.CONTA_CORRENTE else TipoContaData.CONTA_POUPANCA,
                dados.owner.name,
                dados.owner.taxIdNumber,
                dados.createdAt
            )
        }
        val dadosChave = chave.get()

        val response = bcbClient.consultaChave(detalhaChave.chave)

        if (response.status != HttpStatus.OK) {
            throw ChaveNaoExistenteException("A chave com o valor: ${detalhaChave.chave} não existe no Banco Central do Brasil (BCB)")
        }

        val data = response.body()

        return DetalhaChaveSistemaExternoResponse(
            dadosChave.tipoChave,
            dadosChave.valorChave,
            dadosChave.donoDaConta.nomeInstituicao,
            dadosChave.donoDaConta.agencia,
            dadosChave.donoDaConta.numeroConta,
            dadosChave.tipoConta,
            dadosChave.donoDaConta.nome,
            dadosChave.donoDaConta.cpf,
            data.createdAt
        )
    }
}