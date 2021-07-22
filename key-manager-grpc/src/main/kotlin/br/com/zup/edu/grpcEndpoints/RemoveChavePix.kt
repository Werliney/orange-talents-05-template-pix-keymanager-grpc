package br.com.zup.edu.grpcEndpoints

import br.com.zup.edu.RemoveChaveGrpcServiceGrpc
import br.com.zup.edu.RemoveChavePixRequest
import br.com.zup.edu.RemoveChavePixResponse
import br.com.zup.edu.chavePix.removeChavePix.toModel
import br.com.zup.edu.services.RemoveChavePixService
import br.com.zup.edu.validacoes.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@ErrorAroundHandler
@Singleton
class RemoveChavePix(val removeChavePixService: RemoveChavePixService): RemoveChaveGrpcServiceGrpc.RemoveChaveGrpcServiceImplBase() {

    override fun removeChavePix(
        request: RemoveChavePixRequest?,
        responseObserver: StreamObserver<RemoveChavePixResponse>?
    ) {

        val removeChavePixRequest = request?.toModel()
        removeChavePixService.remove(removeChavePixRequest!!)

        val response = RemoveChavePixResponse.newBuilder()
            .setMensagem("Chave removida com sucesso")
            .build()

        responseObserver!!.onNext(response)
        responseObserver!!.onCompleted()
    }
}