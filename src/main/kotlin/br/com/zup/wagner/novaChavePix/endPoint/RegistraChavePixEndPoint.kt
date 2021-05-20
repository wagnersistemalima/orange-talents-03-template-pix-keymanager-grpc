package br.com.zup.wagner.novaChavePix.endPoint

import br.com.zup.wagner.KeyManagerRegistraChavePixServiceGrpc
import br.com.zup.wagner.RegistraChavePixRequest
import br.com.zup.wagner.RegistraChavePixResponse
import br.com.zup.wagner.novaChavePix.exceptions.ErrorHandlle
import br.com.zup.wagner.novaChavePix.model.TipoDeChaveModel
import br.com.zup.wagner.novaChavePix.request.NovaChavePixRequest
import br.com.zup.wagner.novaChavePix.service.NovaChavePixServise

import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

// sigleton = esta anotação é para que o micronaut reconheça essa classe.


@Validated
@Singleton
class RegistraChavePixEndPoint(
    @Inject val service: NovaChavePixServise
): KeyManagerRegistraChavePixServiceGrpc.KeyManagerRegistraChavePixServiceImplBase(){


    private val logger = LoggerFactory.getLogger(RegistraChavePixEndPoint::class.java)

    // metodo implementado da interface do arquivo proto

    @ErrorHandlle
    override fun registra(request: RegistraChavePixRequest?, responseObserver: StreamObserver<RegistraChavePixResponse>?
    ) {
        logger.info("Iniciando o registro da chave pix")

        // validação request aqui

        val chavePixRequest: NovaChavePixRequest? = request?.toModel()  // extension function

        val chave = service.registraChave(chavePixRequest!!)

        val response = RegistraChavePixResponse.newBuilder()
            .setClientId(chave.id.toString())
            .setPixId(chave.chave)

            .build()

        responseObserver!!.onNext(response)         // devolvendo a resposta
        responseObserver.onCompleted()            // finalizando
    }

}

// extesion function
fun RegistraChavePixRequest.toModel(): NovaChavePixRequest {
    return NovaChavePixRequest(
        clientId = this.clientId,
        tipoChave = TipoDeChaveModel.valueOf(this.tipoDeChave.name),
        valorChave = this.valorChave,
        tipoConta = this.tipoDeConta
    )
}
