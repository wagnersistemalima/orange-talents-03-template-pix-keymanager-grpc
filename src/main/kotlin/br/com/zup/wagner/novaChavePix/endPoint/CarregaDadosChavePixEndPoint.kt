package br.com.zup.wagner.novaChavePix.endPoint


import br.com.zup.wagner.CarregaChavePixRequest
import br.com.zup.wagner.CarregaChavePixResponse
import br.com.zup.wagner.KeyManagerCarregaChavePixServiceGrpc
import br.com.zup.wagner.novaChavePix.exceptions.ErrorHandlle
import br.com.zup.wagner.novaChavePix.request.ConsultaChavePixRequest
import br.com.zup.wagner.novaChavePix.request.PixIdRequest
import br.com.zup.wagner.novaChavePix.service.ConsultaChavePixService
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton


// end point consultar dados de chave do client

@ErrorHandlle
@Singleton
class CarregaDadosChavePixEndPoint(
   @Inject val service: ConsultaChavePixService
): KeyManagerCarregaChavePixServiceGrpc.KeyManagerCarregaChavePixServiceImplBase() {

    private val logger = LoggerFactory.getLogger(CarregaDadosChavePixEndPoint::class.java)

    // end point consultar dados

    override fun carrega(request: CarregaChavePixRequest?, responseObserver: StreamObserver<CarregaChavePixResponse>?) {
        logger.info("Iniciando a consulta de dados")



        val recebeDadosRequest = request?.toModel()  // extencion function

        val chaveResponse = service.consulta(recebeDadosRequest)


        val response = CarregaChavePixResponse.newBuilder()
            .setIdentificadorItau(chaveResponse?.identificadorItau)
            .setTipoDeChave(chaveResponse?.tipoChave)
            .setChave(chaveResponse!!.chave)
            .setInstituicao(chaveResponse?.instituicao)
            .setIspb(chaveResponse!!.ispb)
            .setAgencia(chaveResponse!!.agencia)
            .setNumeroConta(chaveResponse!!.numeroConta)
            .setTitular(chaveResponse!!.titular)
            .setCpf(chaveResponse!!.cpf)
            .setIdInterno(chaveResponse!!.id)
            .setCriadoEm(chaveResponse!!.criadoEm)
            .build()

        responseObserver!!.onNext(response)   // devolvendo a resposta
        responseObserver.onCompleted()

    }

}

// extencion function

fun CarregaChavePixRequest?.toModel(): ConsultaChavePixRequest {
    return ConsultaChavePixRequest(
        pixId = PixIdRequest(
            clientId = this!!.pixId.clientId,
            pixId = this!!.pixId.pixId
        ),
        chave = this!!.chave
    )
}



