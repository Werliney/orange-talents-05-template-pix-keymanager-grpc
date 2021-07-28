package br.com.zup.edu.grpcEndpoints

import br.com.zup.edu.*
import br.com.zup.edu.chavePix.listaChaves.toModel
import br.com.zup.edu.services.ListaChavesDoClienteService
import br.com.zup.edu.validacoes.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class ListaChavesDoCliente(val listaChaves: ListaChavesDoClienteService): ListaChavesDoClienteGrpcServiceGrpc.ListaChavesDoClienteGrpcServiceImplBase() {

    override fun listaChaves(
        request: ListaChavesDoClienteRequest?,
        responseObserver: StreamObserver<ListaChavesDoClienteResponse>?
    ) {

        val listaChaveRequest = request?.toModel()
        val chaves = listaChaves.lista(listaChaveRequest!!).map {
            ListaChavesDoClienteResponse.ChavePix.newBuilder()
                .setIdPix(it.id!!)
                .setIdCliente(it.idCliente)
                .setTipoChave(TipoChave.valueOf(it.tipoChave.name))
                .setValorChave(it.valorChave)
                .setTipoConta(TipoConta.valueOf(it.tipoConta.name))
                .build()
        }


        responseObserver!!.onNext(ListaChavesDoClienteResponse.newBuilder()
            .addAllChaves(chaves)
            .build())
        responseObserver!!.onCompleted()
    }
}