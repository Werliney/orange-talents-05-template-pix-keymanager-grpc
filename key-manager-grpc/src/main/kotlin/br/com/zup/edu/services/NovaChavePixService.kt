package br.com.zup.edu.services

import br.com.zup.edu.chavePix.ChavePix
import br.com.zup.edu.chavePix.TipoChaveData
import br.com.zup.edu.chavePix.TipoContaData
import br.com.zup.edu.chavePix.cadastraChavePix.ChavePixRequest
import br.com.zup.edu.exceptions.ChaveExistenteException
import br.com.zup.edu.exceptions.ErroNoCadastroDaChaveBcbException
import br.com.zup.edu.repository.ChavePixRepository
import br.com.zup.edu.servicosExternos.*
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validator

@Validated
@Singleton
class NovaChavePixService(
    @Inject val erpItauClient: ErpItauClient,
    @Inject val bcbClient: BcbClient,
    @Inject val chavePixRepository: ChavePixRepository,
    val validator: Validator
) {

    fun cadastra(@Valid request: ChavePixRequest): ChavePix {

        val possivelChaveExistente = chavePixRepository.findByValorChave(request!!.valorChave)

        if (!possivelChaveExistente.isEmpty) {
            throw ChaveExistenteException("A chave ${request.valorChave} já existe")
        }

        val dadosDaContaResponse = erpItauClient.consulta(request!!.idCliente, request!!.tipoConta.toString())
        val chavePix = request.paraChavePix(dadosDaContaResponse.body()!!)

        val dados = dadosDaContaResponse.body()


        val erros = validator.validate(chavePix)

        if (erros.isNotEmpty()) {
            throw ConstraintViolationException(erros)
        }

        val bankAccount = BankAccount(
            "60701190",
            dados.agencia,
            dados.numero,
            accountType = if (chavePix.tipoConta == TipoContaData.CONTA_CORRENTE) AccountType.CACC else AccountType.SVGS)
        val owner = Owner(TypePerson.NATURAL_PERSON, dados.titular.nome, dados.titular.cpf)
        val createPixRequest = CreatePixRequest(chavePix.tipoChave, chavePix.valorChave, bankAccount, owner)
        val createPixKeyResponse = bcbClient.cadastraChaveBcb(createPixRequest)

        if (createPixKeyResponse.status != HttpStatus.CREATED) {
            throw ErroNoCadastroDaChaveBcbException("Não foi possível cadastar a chave Pix no Banco Central do Brasil")
        }

        chavePixRepository.save(chavePix)

        return chavePix
    }
}