package br.com.zup.edu.servicosExternos

import br.com.zup.edu.chavePix.TipoChaveData
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import java.time.LocalDateTime

@Client(value = "http://localhost:8082")
interface BcbClient {

    @Post("/api/v1/pix/keys")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun cadastraChaveBcb(@Body createPixRequest: CreatePixRequest): HttpResponse<CreatePixKeyResponse>

    @Delete("/api/v1/pix/keys/{keys}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun removeChaveBcb(@PathVariable keys: String, @Body deletePixKeyRequest: DeletePixKeyRequest): HttpResponse<DeletePixKeyResponse>

    @Get("/api/v1/pix/keys/{key}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun consultaChave(@PathVariable key: String): HttpResponse<PixDetailsResponse>
}

data class PixDetailsResponse(
    val keyType: TipoChaveData,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime)

data class DeletePixKeyRequest(val key: String, val participant: String)

data class DeletePixKeyResponse(val key: String, val participant: String) {
    val deletedAt = LocalDateTime.now()!!
}

data class CreatePixRequest(
    val keyType: TipoChaveData,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner
)

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
)

enum class AccountType {
    CACC,
    SVGS
}

data class Owner(
    val type: TypePerson,
    val name: String,
    val taxIdNumber: String
)

enum class TypePerson {
    NATURAL_PERSON,
    LEGAL_PERSON
}

data class CreatePixKeyResponse(
    val keyType: TipoChaveData,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner) {


    val createdAt = LocalDateTime.now()!!
}