package br.com.zup.wagner.novaChavePix.endPoint

import br.com.zup.wagner.DeleteChavePixRequest
import br.com.zup.wagner.DeleteChavePixResponse
import br.com.zup.wagner.KeyManagerRemoveChavePixServiceGrpc
import br.com.zup.wagner.novaChavePix.exceptions.ErrorHandlle
import br.com.zup.wagner.novaChavePix.request.RemoveChavePixRequest
import br.com.zup.wagner.novaChavePix.service.RemoveChavePixService
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandlle
@Singleton
class RemoveChavePixEndPoint(
    @Inject val service: RemoveChavePixService
): KeyManagerRemoveChavePixServiceGrpc.KeyManagerRemoveChavePixServiceImplBase() {

    private val logger = LoggerFactory.getLogger(RemoveChavePixEndPoint::class.java)

    override fun delete(request: DeleteChavePixRequest?, responseObserver: StreamObserver<DeleteChavePixResponse>?) {
        logger.info("Iniciando a remoção da chave pix solicitada")

        val paraRemover: RemoveChavePixRequest? = request?.toModel()

        service.remove(paraRemover)

        // resposta

        val response = DeleteChavePixResponse.newBuilder()
            .setPixId(request!!.pixId)
            .setIdentificadorItau(request.identificadorItau)
            .build()

        responseObserver!!.onNext(response)   // devolvendo a resposta
        responseObserver.onCompleted()

    }
}

// extension function
fun DeleteChavePixRequest.toModel(): RemoveChavePixRequest {
    return RemoveChavePixRequest(
        pixId = this.pixId,
        identificadorItau = this.identificadorItau
    )
}