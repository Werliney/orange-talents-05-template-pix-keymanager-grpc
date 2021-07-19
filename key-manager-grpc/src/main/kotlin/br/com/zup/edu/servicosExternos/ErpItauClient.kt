package br.com.zup.edu.servicosExternos

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client
interface ErpItauClient {

    @Get("http://localhost:9091/api/v1/clientes/{clienteId}/contas")
    @Consumes(MediaType.APPLICATION_JSON)
    fun consulta(@PathVariable clienteId: String, @QueryValue(defaultValue = "") tipo: String): HttpResponse<DadosDaContaResponse>
}