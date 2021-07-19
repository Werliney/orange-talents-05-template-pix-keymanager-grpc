package br.com.zup.edu.grpcEndpoints

import br.com.zup.edu.CadastraChavePixRequest
import br.com.zup.edu.CadastraChavePixResponse
import br.com.zup.edu.KeyManagerGrpcServiceGrpc
import br.com.zup.edu.chavePix.NovaChavePixService
import br.com.zup.edu.chavePix.toModel
import br.com.zup.edu.validacoes.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class CadastraChavePix(@Inject val novaChavePixService: NovaChavePixService): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    override fun cadastrarChavePix(
        request: CadastraChavePixRequest?,
        responseObserver: StreamObserver<CadastraChavePixResponse>?
    ) {

      val chavePixRequest = request?.toModel()
       val chavePixSalva = novaChavePixService.cadastra(chavePixRequest!!)

        val response = CadastraChavePixResponse.newBuilder()
            .setIdPix(chavePixSalva.id!!)
            .build()

        responseObserver!!.onNext(response)
        responseObserver!!.onCompleted()
    }
}