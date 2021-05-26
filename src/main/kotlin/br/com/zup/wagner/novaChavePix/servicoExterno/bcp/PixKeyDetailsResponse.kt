package br.com.zup.wagner.novaChavePix.servicoExterno.bcp

import br.com.zup.wagner.novaChavePix.model.ContaAssociada
import br.com.zup.wagner.novaChavePix.model.TipoDeChaveModel
import br.com.zup.wagner.novaChavePix.model.TipoDeContaModel
import java.time.LocalDateTime
import java.util.*

// objeto de resposta

data class PixKeyDetailsResponse(
    val keyType: PixkeyType,              // tipo de chave
    val key: String,                      // valor da chave
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)


