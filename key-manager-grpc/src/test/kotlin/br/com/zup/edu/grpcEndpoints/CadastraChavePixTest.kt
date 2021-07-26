package br.com.zup.edu.grpcEndpoints

import br.com.zup.edu.CadastraChavePixRequest
import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.chavePix.cadastraChavePix.ChavePixRequest
import br.com.zup.edu.chavePix.TipoChaveData
import br.com.zup.edu.chavePix.TipoContaData
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class CadastraChavePixTest(
    val chavePixRepository: ChavePixRepository,
    val grpClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub,
) {

    @field:Inject
    lateinit var erpItauClient: ErpItauClient

    @field:Inject
    lateinit var bcbClient: BcbClient

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    @AfterEach
    internal fun tearDown() {
        chavePixRepository.deleteAll()
    }

    @Test
    internal fun `deve cadastrar chave pix`() {
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

        val bankAccount = BankAccount("60701190", "0001", "291900", AccountType.CACC)
        val owner = Owner(TypePerson.NATURAL_PERSON, "Rafael M C Ponte", "02467781054")
        val createPixRequest = CreatePixRequest(TipoChaveData.CPF, "99256629070", bankAccount, owner)

        val createPixKeyResponse = CreatePixKeyResponse(TipoChaveData.CPF, "99256629070", bankAccount, owner)

        Mockito.`when`(erpItauClient.consulta("c56dfef4-7901-44fb-84e2-a2cefb157890", "CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(dadosDaContaResponse))

        Mockito.`when`(bcbClient.cadastraChaveBcb(createPixRequest)).thenReturn(HttpResponse.created(createPixKeyResponse))

        //ação
        val response = grpClient.cadastrarChavePix(CadastraChavePixRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.valueOf("CPF"))
            .setValorChave("99256629070")
            .setTipoConta(TipoConta.forNumber(TipoConta.CONTA_CORRENTE_VALUE))
            .build())


        //validação
        assertNotNull(response.idPix)
    }

    @Test
    internal fun `nao deve cadastrar chave pix com dados inválidos`() {
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

        // ação
        val error = assertThrows<StatusRuntimeException> {
            grpClient.cadastrarChavePix(CadastraChavePixRequest.newBuilder()
                .setIdCliente("")
                .setTipoChave(TipoChave.valueOf("CPF"))
                .setValorChave("99256629070")
                .setTipoConta(TipoConta.valueOf("CONTA_CORRENTE"))
                .build())
        }

        // validação
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    internal fun `nao deve cadastrar chave pix com chave repetida`() {
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
        val error = assertThrows<StatusRuntimeException> {
            grpClient.cadastrarChavePix(CadastraChavePixRequest.newBuilder()
                .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoChave(TipoChave.valueOf("CPF"))
                .setValorChave(chavePixSalva.valorChave)
                .setTipoConta(TipoConta.valueOf("CONTA_CORRENTE"))
                .build())
        }

        // validação
        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
        }
    }

    @Test
    internal fun `nao deve cadastrar chave pix quando nao for possivel registrar chave no bcb`() {
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

        val bankAccount = BankAccount("60701190", "0001", "291900", AccountType.CACC)
        val owner = Owner(TypePerson.NATURAL_PERSON, "Rafael M C Ponte", "02467781054")
        val createPixRequest = CreatePixRequest(TipoChaveData.CPF, "99256629070", bankAccount, owner)

        Mockito.`when`(erpItauClient.consulta("c56dfef4-7901-44fb-84e2-a2cefb157890", "CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(dadosDaContaResponse))

        Mockito.`when`(bcbClient.cadastraChaveBcb(createPixRequest)).thenReturn(HttpResponse.badRequest())

        // ação
        val erro = assertThrows<StatusRuntimeException> {
            grpClient.cadastrarChavePix(CadastraChavePixRequest.newBuilder()
                .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoChave(TipoChave.valueOf("CPF"))
                .setValorChave("99256629070")
                .setTipoConta(TipoConta.forNumber(TipoConta.CONTA_CORRENTE_VALUE))
                .build())
        }

        // validação
        with(erro) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Não foi possível cadastar a chave Pix no Banco Central do Brasil", status.description)
        }

    }

    @MockBean(ErpItauClient::class)
    fun erpItauMock(): ErpItauClient {
        return Mockito.mock(ErpItauClient::class.java)
    }

    @MockBean(BcbClient::class)
    fun bcbClientMock(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }


    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub? {
            return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}
