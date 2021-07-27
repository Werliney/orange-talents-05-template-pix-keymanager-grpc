package br.com.zup.edu.chavePix.detalhaChaveKeyManager

import br.com.zup.edu.chavePix.TipoChaveData
import br.com.zup.edu.chavePix.TipoContaData
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class DetalhaKeyManagerChaveResponse(
    val idPix: Long,
    val idCliente: String,
    val tipoChaveData: TipoChaveData,
    val valorChave: String,
    val nome: String,
    val cpf: String,
    val nomeInstituicao: String,
    val agencia: String,
    val numeroDaConta: String,
    val tipoContaData: TipoContaData,
    val createdAt: LocalDateTime
) {
}