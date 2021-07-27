package br.com.zup.edu.chavePix.detalhaChaveSistemaExterno

import br.com.zup.edu.chavePix.TipoChaveData
import br.com.zup.edu.chavePix.TipoContaData
import br.com.zup.edu.servicosExternos.BankAccount
import br.com.zup.edu.servicosExternos.Owner
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class DetalhaChaveSistemaExternoResponse(
    val tipoChave: TipoChaveData,
    val valoChave: String,
    val participant: String,
    val agencia: String,
    val numeroConta: String,
    val tipoConta: TipoContaData,
    val nome: String,
    val cpf: String,
    val createdAt: LocalDateTime
) {
}