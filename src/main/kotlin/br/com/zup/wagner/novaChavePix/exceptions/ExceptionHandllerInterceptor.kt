package br.com.zup.wagner.novaChavePix.exceptions

import br.com.zup.wagner.novaChavePix.service.NovaChavePixServise
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import kotlin.IllegalArgumentException

@Singleton
@InterceptorBean(ErrorHandlle::class)
class ExceptionHandllerInterceptor: MethodInterceptor<NovaChavePixServise, Any?> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<NovaChavePixServise, Any?>): Any? {
        // antes
        logger.info("Interceptando method: ${context.targetMethod}")

        try {
            return context.proceed()         // processa o metodo interceptado
        }catch (e: Exception) {

            logger.error("Convertendo exceçoes para erro grpc")
            // convertendo exceçoes para erro GRPC
            val error = when(e) {
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException()
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message).asRuntimeException() // esxeção no RP itau
                is NovaChavePixException -> Status.ALREADY_EXISTS.withDescription(e.msg).asRuntimeException()
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException()
                is RemoveChavePixException -> Status.NOT_FOUND.withDescription(e.msg).asRuntimeException()
                is ChavePixException -> Status.NOT_FOUND.withDescription(e.msg).asRuntimeException()
                //else -> Status.UNKNOWN.withDescription("Erro Inesperado").asRuntimeException()
                else -> throw e
            }

            val responseObserver = context.parameterValues[1] as StreamObserver<*>
            responseObserver.onError(error)

            return null
        }

    }

}