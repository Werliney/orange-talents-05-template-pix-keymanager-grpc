package br.com.zup.edu.services

import br.com.zup.edu.chavePix.removeChavePix.RemoveChaveRequest
import br.com.zup.edu.exceptions.ChaveNaoExistenteException
import br.com.zup.edu.repository.ChavePixRepository
import br.com.zup.edu.servicosExternos.BcbClient
import br.com.zup.edu.servicosExternos.DeletePixKeyRequest
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validator

@Validated
@Singleton
class RemoveChavePixService(
    val chavePixRepository: ChavePixRepository,
    val bcbClient: BcbClient,
    val validator: Validator
) {

    fun remove(@Valid removeRequest: RemoveChaveRequest) {

        val possivelChaveExistente = chavePixRepository.findById(removeRequest.idPix)

        if (possivelChaveExistente.isEmpty) {
            throw ChaveNaoExistenteException("A chave com id ${removeRequest.idPix} não existe")
        }

        val erros = validator.validate(removeRequest)

        if (erros.isNotEmpty()) {
            throw ConstraintViolationException(erros)
        }

        val chavePix = chavePixRepository.findChaveByIdAndCliente(removeRequest.idPix, removeRequest.idCliente)

        val deletePixKeyRequest = DeletePixKeyRequest(chavePix.get().valorChave,"60701190")
        val deletePixKeyResponse = bcbClient.removeChaveBcb(chavePix.get().valorChave, deletePixKeyRequest)

        chavePixRepository.delete(chavePix.get())
    }
}