package br.com.zup.edu.grpcEndpoints

import br.com.zup.edu.*
import br.com.zup.edu.chavePix.detalhaChaveKeyManager.toModel
import br.com.zup.edu.services.DetalhaChaveKeyManagerService
import br.com.zup.edu.validacoes.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class DetalhaChaveKeyManager(val listaChaveService: DetalhaChaveKeyManagerService): ListaChaveKeyManagerGrpcServiceGrpc.ListaChaveKeyManagerGrpcServiceImplBase() {

    override fun listaChave(
        request: ListaChaveKeyManagerRequest?,
        responseObserver: StreamObserver<ListaChaveKeyManagerResponse>?
    ) {

        val detalhaChaveRequest = request?.toModel()
        val detalhaChaveResponse = listaChaveService.consulta(detalhaChaveRequest!!)

        val response = ListaChaveKeyManagerResponse.newBuilder()
            .setIdCliente(detalhaChaveResponse.idCliente)
            .setIdPix(detalhaChaveResponse.idPix)
            .setTipoChave(TipoChave.valueOf(detalhaChaveResponse.tipoChaveData.name))
            .setValorChave(detalhaChaveResponse.valorChave)
            .setNome(detalhaChaveResponse.nome)
            .setCpf(detalhaChaveResponse.cpf)
            .setNomeInstituicao(detalhaChaveResponse.nomeInstituicao)
            .setAgencia(detalhaChaveResponse.agencia)
            .setNumeroConta(detalhaChaveResponse.numeroDaConta)
            .setTipoConta(TipoConta.valueOf(detalhaChaveResponse.tipoContaData.name))
            .setDataCriacao(detalhaChaveResponse.createdAt.toString())
            .build()

        responseObserver!!.onNext(response)
        responseObserver!!.onCompleted()

    }
}