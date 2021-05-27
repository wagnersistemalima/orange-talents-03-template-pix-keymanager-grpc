package br.com.zup.wagner.novaChavePix.service

import br.com.zup.wagner.novaChavePix.endPoint.response.DadosChavePixResponse
import br.com.zup.wagner.novaChavePix.exceptions.ChavePixException
import br.com.zup.wagner.novaChavePix.repository.ChavePixRepository
import br.com.zup.wagner.novaChavePix.request.ConsultaChavePixRequest
import br.com.zup.wagner.novaChavePix.servicoExterno.bcp.BancoCentralBrasil
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// classe contendo a logica para realizar consultas e validar entrada de dados

@Singleton
class ConsultaChavePixService(
    @Inject private val apiBancoCentralBrasil: BancoCentralBrasil,
    @Inject private val repository: ChavePixRepository
) {

    private val logger = LoggerFactory.getLogger(ConsultaChavePixService::class.java)

    // metodo contendo a logica para consulta


    fun consulta(recebeDadosRequest: ConsultaChavePixRequest?): DadosChavePixResponse? {

        logger.info("Execultando a logica para consulta")

        if(recebeDadosRequest?.pixId?.clientId.isNullOrBlank() && recebeDadosRequest?.pixId?.pixId.isNullOrBlank()) {
            logger.info("PixId e ClientId chegou nulo, Consultar por chave no banco central")
            // consultar  no banco interno e tambem no banco central
            val key: String = recebeDadosRequest!!.chave.toString()

            val possivelChavePix = repository.findByChave(key)

            logger.info("possivel chave = $possivelChavePix")

            if (possivelChavePix != null) {
                val chaveResponse: DadosChavePixResponse = DadosChavePixResponse(
                    id = possivelChavePix!!.id.toString(),
                    identificadorItau = possivelChavePix.identificadorItau.toString(),
                    tipoChave = possivelChavePix.tipoChave.toString(),
                    chave = possivelChavePix.chave,
                    tipoDeConta = possivelChavePix.tipoDeConta.toString(),
                    instituicao = possivelChavePix.conta.instituicao,
                    ispb = possivelChavePix.conta.ispb,
                    agencia = possivelChavePix.conta.agencia,
                    numeroConta = possivelChavePix.conta.numeroConta,
                    titular = possivelChavePix.conta.titular,
                    cpf = possivelChavePix.conta.cpf,
                    criadoEm = possivelChavePix.criadoEm.toString()
                )
                return chaveResponse
            }

            else if (possivelChavePix == null) {


                try {
                    val respostaBancoCentral = apiBancoCentralBrasil.consulta(key)
                    val chaveResponse: DadosChavePixResponse = DadosChavePixResponse(
                        id = "null",
                        identificadorItau = "null",
                        tipoChave = respostaBancoCentral.body()!!.keyType.toString(),
                        chave = respostaBancoCentral.body()!!.key,
                        tipoDeConta = respostaBancoCentral.body()!!.keyType.toString(),
                        instituicao = "null",
                        ispb = respostaBancoCentral.body()!!.bankAccount.participant,
                        agencia = respostaBancoCentral.body()!!.bankAccount.branch,
                        numeroConta = respostaBancoCentral.body()!!.bankAccount.accountNumber,
                        titular = respostaBancoCentral.body()!!.owner.name,
                        cpf = respostaBancoCentral.body()!!.owner.taxIdNumber,
                        criadoEm = respostaBancoCentral.body()!!.createdAt.toString()
                    )
                    logger.info("Consulta realizada com sucesso")
                    return chaveResponse

                }
                catch (e: Exception) {
                    throw IllegalStateException()
                }

            }


        }
        else if(recebeDadosRequest?.chave.isNullOrBlank()) {
            logger.info("Chave chegou nula, favor consultar po PixId e ClientId no banco interno")
            // consultar banco de dados interno
            val id: UUID = UUID.fromString(recebeDadosRequest!!.pixId!!.clientId)
            val identificadorItau: UUID = UUID.fromString(recebeDadosRequest.pixId!!.pixId)


            val chavePix = repository.findByIdAndIdentificadorItau(id, identificadorItau)
                ?: throw ChavePixException()


            val chaveResponse: DadosChavePixResponse = DadosChavePixResponse(
                id = chavePix.id.toString(),
                identificadorItau = chavePix.identificadorItau.toString(),
                tipoChave = chavePix.tipoChave.toString(),
                chave = chavePix.chave,
                tipoDeConta = chavePix.tipoDeConta.toString(),
                instituicao = chavePix.conta.instituicao,
                ispb = chavePix.conta.ispb,
                agencia = chavePix.conta.agencia,
                numeroConta = chavePix.conta.numeroConta,
                titular = chavePix.conta.titular,
                cpf = chavePix.conta.cpf,
                criadoEm = chavePix.criadoEm.toString()
            )
            logger.info("Consulta realizada com sucesso")
            return chaveResponse
        }

        return throw IllegalArgumentException()
    }
}