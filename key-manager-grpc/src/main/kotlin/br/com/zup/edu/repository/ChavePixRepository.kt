package br.com.zup.edu.repository

import br.com.zup.edu.TipoChave
import br.com.zup.edu.chavePix.ChavePix
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, Long> {

    @Query("SELECT c FROM ChavePix c WHERE c.valorChave =:valorChave")
    fun findByValorChave(valorChave: String?): Optional<ChavePix>

    @Query("SELECT c FROM ChavePix c WHERE c.id=:idPix AND c.idCliente=:idCliente")
    fun findChaveByIdAndCliente(idPix: Long, idCliente: String): Optional<ChavePix>
}