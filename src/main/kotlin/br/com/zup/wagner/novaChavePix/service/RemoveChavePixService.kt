package br.com.zup.wagner.novaChavePix.service

import br.com.zup.wagner.novaChavePix.exceptions.RemoveChavePixException
import br.com.zup.wagner.novaChavePix.repository.ChavePixRepository
import br.com.zup.wagner.novaChavePix.request.RemoveChavePixRequest
import br.com.zup.wagner.novaChavePix.servicoExterno.bcp.BancoCentralBrasil
import br.com.zup.wagner.novaChavePix.servicoExterno.bcp.DeletePixKeyRequest
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemoveChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val apiBancoCentralBrasil: BancoCentralBrasil
) {

    private val logger = LoggerFactory.getLogger(RemoveChavePixService::class.java)

    // metodo remover
    @Transactional
    fun remove(@Valid paraRemover: RemoveChavePixRequest?) {

        logger.info("Execultando a logica para remover a chave")

        val uuidPixId = UUID.fromString(paraRemover?.pixId)
        val uuidIdentificadorItau = UUID.fromString(paraRemover?.identificadorItau)

        val possivelChave = repository.findByIdAndIdentificadorItau(uuidPixId, uuidIdentificadorItau)
            ?: throw RemoveChavePixException()

        repository.deleteById(uuidPixId)

       // chamada para serviço externo banco central para deletar a chave

        try {
            val respostaBancoCentralRequest = apiBancoCentralBrasil.delete(
                possivelChave.chave,
                DeletePixKeyRequest(key = possivelChave.chave, possivelChave.conta.ispb)
            )
        }
        catch (e: Exception) {
            throw RemoveChavePixException("Erro ao tentar remover chave pix no banco central")
        }

        logger.info("Chave deletada com sucesso")

    }


}