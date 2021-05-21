package br.com.zup.wagner.novaChavePix.service

import br.com.zup.wagner.novaChavePix.exceptions.RemoveChavePixException
import br.com.zup.wagner.novaChavePix.repository.ChavePixRepository
import br.com.zup.wagner.novaChavePix.request.RemoveChavePixRequest
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemoveChavePixService(
    val repository: ChavePixRepository
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

        logger.info("Chave deletada com sucesso")

    }


}