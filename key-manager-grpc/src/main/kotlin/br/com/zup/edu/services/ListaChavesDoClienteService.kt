package br.com.zup.edu.services

import br.com.zup.edu.chavePix.ChavePix
import br.com.zup.edu.chavePix.listaChaves.ListaChavesRequest
import br.com.zup.edu.exceptions.ClienteNuloException
import br.com.zup.edu.repository.ChavePixRepository
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class ListaChavesDoClienteService(val chavePixRepository: ChavePixRepository) {

    fun lista(@Valid listaChaves: ListaChavesRequest): List<ChavePix> {

        if (listaChaves.idCliente.isNullOrBlank()) {
            throw ClienteNuloException("O cliente informado não pode ser nulo")
        }

        return chavePixRepository.findChavesByIdCliente(listaChaves.idCliente)

    }
}