package br.com.zup.edu.services

import br.com.zup.edu.chavePix.removeChavePix.RemoveChaveRequest
import br.com.zup.edu.exceptions.ChaveNaoExistenteException
import br.com.zup.edu.repository.ChavePixRepository
import javax.inject.Singleton

@Singleton
class RemoveChavePixService(val chavePixRepository: ChavePixRepository) {

    fun remove(removeRequest: RemoveChaveRequest) {

        val possivelChaveExistente = chavePixRepository.findById(removeRequest.idPix)

        if (possivelChaveExistente.isEmpty) {
            throw ChaveNaoExistenteException("A chave com id ${removeRequest.idPix} não existe")
        }

        val chavePix = chavePixRepository.findChaveByIdAndCliente(removeRequest.idPix, removeRequest.idCliente)

        chavePixRepository.delete(chavePix.get())
    }
}