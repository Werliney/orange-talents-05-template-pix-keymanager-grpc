package br.com.zup.edu.grpcEndpoints

import br.com.zup.edu.ListaChaveKeyManagerGrpcServiceGrpc
import br.com.zup.edu.ListaChaveKeyManagerRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.chavePix.TipoChaveData
import br.com.zup.edu.chavePix.TipoContaData
import br.com.zup.edu.chavePix.cadastraChavePix.ChavePixRequest
import br.com.zup.edu.chavePix.detalhaChaveKeyManager.DetalhaKeyManagerChaveResponse
import br.com.zup.edu.repository.ChavePixRepository
import br.com.zup.edu.servicosExternos.*
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class DetalhaChaveKeyManagerTest(
    val chavePixRepository: ChavePixRepository,
    val grpcClient: ListaChaveKeyManagerGrpcServiceGrpc.ListaChaveKeyManagerGrpcServiceBlockingStub
) {

    @field:Inject
    lateinit var bcbClient: BcbClient

    @field:Inject
    lateinit var erpItauClient: ErpItauClient

    @Test
    internal fun `deve consultar os dados de uma chave pix`() {

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
        val chavePix = ChavePixRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoChaveData.CPF,
            "99256629070",
            TipoContaData.CONTA_CORRENTE
        ).paraChavePix(dadosDaContaResponse)

        val chavePixSalva = chavePixRepository.save(chavePix)

        val bankAccount = BankAccount(
            "60701190",
            "0001",
            "291900",
            AccountType.CACC
        )
        val owner = Owner(
            TypePerson.NATURAL_PERSON,
            "Rafael M C Ponte",
            "02467781054"
        )

        val listaChaveResponse = PixDetailsResponse(
            TipoChaveData.CPF,
            "99256629070",
            bankAccount,
            owner,
            LocalDateTime.parse("2021-07-19T23:19:06.710163")
        )

        val listaResponse = DetalhaKeyManagerChaveResponse(
            chavePixSalva.id!!,
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoChaveData.CPF,
            "99256629070",
            "Rafael M C Ponte",
            "02467781054",
            "ITAÚ UNIBANCO S.A.",
            "0001",
            "291900",
            TipoContaData.CONTA_CORRENTE,
            LocalDateTime.parse("2021-07-19T23:19:06.710163")
        )

        Mockito.`when`(erpItauClient.consulta("c56dfef4-7901-44fb-84e2-a2cefb157890", "CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(dadosDaContaResponse))

        Mockito.`when`(bcbClient.consultaChave("99256629070")).thenReturn(HttpResponse.ok(listaChaveResponse))


        // ação
        val response = grpcClient.listaChave(ListaChaveKeyManagerRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setIdPix(chavePixSalva.id!!)
            .build())


        // validação
        assertEquals(listaResponse.idPix, response.idPix)
        assertEquals(listaResponse.idCliente, response.idCliente)
        assertEquals(TipoChave.valueOf(listaResponse.tipoChaveData.name), response.tipoChave)
        assertEquals(listaResponse.valorChave, response.valorChave)
        assertEquals(listaResponse.nome, response.nome)
        assertEquals(listaResponse.cpf, response.cpf)
        assertEquals(listaResponse.nomeInstituicao, response.nomeInstituicao)
        assertEquals(listaResponse.agencia, response.agencia)
        assertEquals(listaResponse.numeroDaConta, response.numeroConta)
        assertEquals(TipoConta.valueOf(listaResponse.tipoContaData.name), response.tipoConta)
        assertEquals(listaResponse.createdAt.toString(), response.dataCriacao)
    }

    @Test
    internal fun `nao deve consultar chave pix se a chave informada nao existir`() {
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

        val bankAccount = BankAccount(
            "60701190",
            "0001",
            "291900",
            AccountType.CACC
        )
        val owner = Owner(
            TypePerson.NATURAL_PERSON,
            "Rafael M C Ponte",
            "02467781054"
        )

        val listaChaveResponse = PixDetailsResponse(
            TipoChaveData.CPF,
            "99256629070",
            bankAccount,
            owner,
            LocalDateTime.parse("2021-07-19T23:19:06.710163")
        )

        val listaResponse = DetalhaKeyManagerChaveResponse(
            50,
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoChaveData.CPF,
            "99256629070",
            "Rafael M C Ponte",
            "02467781054",
            "ITAÚ UNIBANCO S.A.",
            "0001",
            "291900",
            TipoContaData.CONTA_CORRENTE,
            LocalDateTime.parse("2021-07-19T23:19:06.710163")
        )

        Mockito.`when`(erpItauClient.consulta("c56dfef4-7901-44fb-84e2-a2cefb157890", "CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(dadosDaContaResponse))

        Mockito.`when`(bcbClient.consultaChave("99256629070")).thenReturn(HttpResponse.ok(listaChaveResponse))

        // ação
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.listaChave(ListaChaveKeyManagerRequest.newBuilder()
                .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157891")
                .setIdPix(50)
                .build())
        }



        // validação
        with(error) {
            assertEquals(Status.NOT_FOUND.code, error.status.code)
        }
    }

    @Test
    internal fun `nao deve consultar chave pix se retornar erro do bcb`() {
        TODO("Not yet implemented")
    }

    @Test
    internal fun `nao deve consultar chave pix se os dados informados estiverem invalidos`() {
        TODO("Not yet implemented")
    }

    @MockBean(BcbClient::class)
    fun bcbClientMock(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }
    @MockBean(ErpItauClient::class)
    fun erpClientMock(): ErpItauClient {
        return Mockito.mock(ErpItauClient::class.java)
    }


    @Factory
    class DetalhaClient {

        @Bean
        fun detalhaBlockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ListaChaveKeyManagerGrpcServiceGrpc.ListaChaveKeyManagerGrpcServiceBlockingStub? {
            return ListaChaveKeyManagerGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}