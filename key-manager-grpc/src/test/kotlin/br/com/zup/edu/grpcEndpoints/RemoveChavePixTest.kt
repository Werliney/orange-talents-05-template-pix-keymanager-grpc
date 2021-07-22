package br.com.zup.edu.grpcEndpoints

import br.com.zup.edu.RemoveChaveGrpcServiceGrpc
import br.com.zup.edu.RemoveChavePixRequest
import br.com.zup.edu.chavePix.TipoChaveData
import br.com.zup.edu.chavePix.TipoContaData
import br.com.zup.edu.chavePix.cadastraChavePix.ChavePixRequest
import br.com.zup.edu.repository.ChavePixRepository
import br.com.zup.edu.servicosExternos.DadosDaContaResponse
import br.com.zup.edu.servicosExternos.ErpItauClient
import br.com.zup.edu.servicosExternos.Instituicao
import br.com.zup.edu.servicosExternos.Titular
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemoveChavePixTest(
    val chavePixRepository: ChavePixRepository,
    val grpcClient: RemoveChaveGrpcServiceGrpc.RemoveChaveGrpcServiceBlockingStub
) {

    @field:Inject
    lateinit var erpItauClient: ErpItauClient


    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    @Test
    internal fun `deve remover chave pix`() {

        // cenário
        val instituicao = Instituicao(
            "ITAÚ UNIBANCO S.A.",
            "60701190"
        )
        val titular = Titular(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "Rafael M C Ponte",
            "02467781054"
        )
        val dadosDaContaResponse = DadosDaContaResponse(
            "CONTA_CORRENTE",
            instituicao,
            "0001",
            "291900",
            titular
        )

        Mockito.`when`(erpItauClient.consulta("c56dfef4-7901-44fb-84e2-a2cefb157890", "CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(dadosDaContaResponse))

        val chavePix = ChavePixRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoChaveData.CPF,
            "99256629070",
            TipoContaData.CONTA_CORRENTE
        ).paraChavePix(dadosDaContaResponse)

       val chavePixSalva = chavePixRepository.save(chavePix)

        // ação
        val response = grpcClient.removeChavePix(RemoveChavePixRequest.newBuilder()
            .setIdPix(chavePixSalva.id!!)
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .build())

        // validação
        assertEquals("Chave removida com sucesso", response.mensagem)
    }

    @Test
    internal fun `nao deve remover chave pix com id nao existente`() {
        // cenário
        val instituicao = Instituicao(
            "ITAÚ UNIBANCO S.A.",
            "60701190"
        )
        val titular = Titular(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "Rafael M C Ponte",
            "02467781054"
        )
        val dadosDaContaResponse = DadosDaContaResponse(
            "CONTA_CORRENTE",
            instituicao,
            "0001",
            "291900",
            titular
        )

        Mockito.`when`(erpItauClient.consulta("c56dfef4-7901-44fb-84e2-a2cefb157890", "CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(dadosDaContaResponse))

        val chavePix = ChavePixRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoChaveData.CPF,
            "99256629070",
            TipoContaData.CONTA_CORRENTE
        ).paraChavePix(dadosDaContaResponse)

         chavePixRepository.save(chavePix)

        // ação
        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.removeChavePix(RemoveChavePixRequest.newBuilder()
                .setIdPix(10)
                .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .build())
        }

        // validação
        assertEquals(Status.NOT_FOUND.code, erro.status.code)
    }

    @Test
    internal fun `nao deve remover chave pix com dados invalidos`() {
        // cenário
        // cenário
        val instituicao = Instituicao(
            "ITAÚ UNIBANCO S.A.",
            "60701190"
        )
        val titular = Titular(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "Rafael M C Ponte",
            "02467781054"
        )
        val dadosDaContaResponse = DadosDaContaResponse(
            "CONTA_CORRENTE",
            instituicao,
            "0001",
            "291900",
            titular
        )

        Mockito.`when`(erpItauClient.consulta("c56dfef4-7901-44fb-84e2-a2cefb157890", "CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(dadosDaContaResponse))

        val chavePix = ChavePixRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoChaveData.CPF,
            "99256629070",
            TipoContaData.CONTA_CORRENTE
        ).paraChavePix(dadosDaContaResponse)

        val chavePixSalva2 = chavePixRepository.save(chavePix)

        // ação
        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.removeChavePix(RemoveChavePixRequest.newBuilder()
                .setIdPix(chavePixSalva2.id!!)
                .setIdCliente("")
                .build())
        }

        // validação

        assertEquals(Status.INVALID_ARGUMENT.code, erro.status.code)
    }

    @MockBean(ErpItauClient::class)
    fun erpItauMock(): ErpItauClient {
        return Mockito.mock(ErpItauClient::class.java)
    }


    @Factory
    class ClientRemove {
        @Bean
        fun blockingStubRemove(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RemoveChaveGrpcServiceGrpc.RemoveChaveGrpcServiceBlockingStub? {
            return RemoveChaveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}