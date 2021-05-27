package br.com.zup.wagner.novaChavePix.endPoint

import br.com.zup.wagner.KeyManagerCarregaTodasChavePixServiceGrpc
import br.com.zup.wagner.ListarTodasPixRequest
import br.com.zup.wagner.ListarTodasPixResponse
import br.com.zup.wagner.novaChavePix.exceptions.ChavePixException
import br.com.zup.wagner.novaChavePix.exceptions.ErrorHandlle
import br.com.zup.wagner.novaChavePix.repository.ChavePixRepository
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandlle
@Singleton
class ListaTodasChavePixEndPoint(
    @Inject private val repository: ChavePixRepository
): KeyManagerCarregaTodasChavePixServiceGrpc.KeyManagerCarregaTodasChavePixServiceImplBase() {

    private val logger = LoggerFactory.getLogger(ListaTodasChavePixEndPoint::class.java)

    // end point

    override fun listarTodas(request: ListarTodasPixRequest?, responseObserver: StreamObserver<ListarTodasPixResponse>?
    ) {
        logger.info("Iniciando a busca de listar todas as chaves pix do cliente")

        // validação

        if (request!!.clientId.isNullOrBlank()) {
            throw  IllegalArgumentException()
        }

        val uuidClient: UUID = UUID.fromString(request.clientId)

        val chavesPix = repository.findAllByIdentificadorItau(uuidClient).map {
            ListarTodasPixResponse.ChavePix.newBuilder()
                .setPixId(it.id.toString())
                .setClientId(it.identificadorItau.toString())
                .setTipoDeChave(it.tipoChave.toString())
                .setTipoDaConta(it.tipoDeConta.toString())
                .setCriadoEm(it.criadoEm.toString())
                .build()

        }

        responseObserver!!.onNext(ListarTodasPixResponse.newBuilder()
            .setClientId(uuidClient.toString())
            .addAllChaves(chavesPix)

            .build())

        responseObserver.onCompleted()

    }

}

