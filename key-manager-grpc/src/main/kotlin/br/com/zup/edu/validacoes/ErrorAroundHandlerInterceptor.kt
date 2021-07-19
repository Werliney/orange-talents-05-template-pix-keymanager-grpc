package br.com.zup.edu.validacoes

import br.com.zup.edu.ChaveExistenteException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import java.lang.Exception
import java.lang.IllegalArgumentException
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorAroundHandler::class)
class ErrorAroundHandlerInterceptor: MethodInterceptor<Any, Any> {

    // o context é literalmente a requisição gRPC, por exemplo o "cadastraChavePix"
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        // o proceed é eu dizendo para o micronaut continuar com a requisição. Aí eu coloco isso dentro de um try catch, pois assim eu consigo capturar uma exception.
        try {
            return context.proceed()
        } catch (ex: Exception) {
            // pegando dos parâmetros o response observer e dando um cast, o [0] seria o request
            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when(ex) {
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withCause(ex).withDescription(ex.message)
                is ChaveExistenteException -> Status.ALREADY_EXISTS.withCause(ex).withDescription(ex.message)
                else -> Status.UNKNOWN
            }

            responseObserver.onError(status.asRuntimeException())

        }
        return null
    }
}