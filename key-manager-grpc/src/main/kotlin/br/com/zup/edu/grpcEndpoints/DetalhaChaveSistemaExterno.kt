package br.com.zup.edu.grpcEndpoints

import br.com.zup.edu.*
import br.com.zup.edu.chavePix.detalhaChaveSistemaExterno.toModel
import br.com.zup.edu.services.DetalhaChaveSistemaExternoService
import br.com.zup.edu.validacoes.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class DetalhaChaveSistemaExterno(val detalhaChave: DetalhaChaveSistemaExternoService): ListaChaveSistemaExternoGrpcServiceGrpc.ListaChaveSistemaExternoGrpcServiceImplBase() {

    override fun listaChave(
        request: ListaChaveSistemaExternoRequest?,
        responseObserver: StreamObserver<ListaChaveSistemaExternoResponse>?
    ) {

       val detalhaRequest = request?.toModel()
        val detalhaResponse = detalhaChave.consulta(detalhaRequest!!)

        val response = ListaChaveSistemaExternoResponse.newBuilder()
            .setTipoChave(TipoChave.valueOf(detalhaResponse.tipoChave.name))
            .setValorChave(detalhaResponse.valoChave)
            .setNome(detalhaResponse.nome)
            .setCpf(detalhaResponse.cpf)
            .setInstituicao(detalhaResponse.participant)
            .setAgencia(detalhaResponse.agencia)
            .setNumeroConta(detalhaResponse.numeroConta)
            .setTipoConta(TipoConta.valueOf(detalhaResponse.tipoConta.name))
            .setDataCriacao(detalhaResponse.createdAt.toString())
            .build()

        responseObserver!!.onNext(response)
        responseObserver!!.onCompleted()
    }
}