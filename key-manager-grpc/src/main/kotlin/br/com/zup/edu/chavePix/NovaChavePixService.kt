package br.com.zup.edu.chavePix

import br.com.zup.edu.ChaveExistenteException
import br.com.zup.edu.repository.ChavePixRepository
import br.com.zup.edu.servicosExternos.ErpItauClient
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validator

@Validated
@Singleton
class NovaChavePixService(
    @Inject val erpItauClient: ErpItauClient,
    @Inject val chavePixRepository: ChavePixRepository,
    val validator: Validator
) {

    fun cadastra(@Valid request: ChavePixRequest): ChavePix {

        val possivelChaveExistente = chavePixRepository.findByValorChave(request!!.valorChave)

        if (!possivelChaveExistente.isEmpty) {
            throw ChaveExistenteException("A chave ${request.valorChave} já existe")
        }

        val dadosDaContaResponse = erpItauClient.consulta(request!!.idCliente, request!!.tipoConta.toString())
        val chavePix = request.paraChavePix(dadosDaContaResponse.body())

        val erros = validator.validate(chavePix)

        if (erros.isNotEmpty()) {
            throw ConstraintViolationException(erros)
        }

        chavePixRepository.save(chavePix)

        return chavePix
    }
}