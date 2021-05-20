package br.com.zup.wagner.novaChavePix.service


import br.com.zup.wagner.novaChavePix.model.ContaAssociada
import br.com.zup.wagner.novaChavePix.exceptions.NovaChavePixException
import br.com.zup.wagner.novaChavePix.model.ChavePix


import br.com.zup.wagner.novaChavePix.repository.ChavePixRepository


import br.com.zup.wagner.novaChavePix.request.NovaChavePixRequest
import br.com.zup.wagner.novaChavePix.servicoExterno.ApiItauClient
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid


@Validated
@Singleton
class NovaChavePixServise(
    @Inject val apiItauClient: ApiItauClient,
    @Inject val repository: ChavePixRepository
) {

    private val logger = LoggerFactory.getLogger(NovaChavePixServise::class.java)

    // metodo contendo a logica de buscar os dados no RP ITAU

    @Transactional
    fun registraChave(@Valid novaChavePixRequest: NovaChavePixRequest): ChavePix {

        // 1 verifica se a chave já existe no sistema chave relacionada cpf

        if (repository.existsByChave(novaChavePixRequest.valorChave)) {
            logger.info("Entrando na exceção, chave pix existente")

            throw NovaChavePixException("Chave pix ${novaChavePixRequest.valorChave} existente")

        }

        logger.info("Buscando dados no RP Itau")
        //2 busca dados da conta no RP do Itau
        val respostaItau = apiItauClient.consulta(novaChavePixRequest.clientId!!, novaChavePixRequest.tipoConta!!.name)


        val contaAssociada: ContaAssociada = respostaItau.body()?.toModel() ?:
            throw IllegalStateException("Cliente não encontrado no itau")

        // 3 grava no banco de dados
        val chave = novaChavePixRequest.toModel(contaAssociada)
        repository.save(chave)
        logger.info("Salvando registro no banco")
        return chave
    }



}