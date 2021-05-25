package br.com.zup.wagner.novaChavePix.servicoExterno.bcp

import br.com.zup.wagner.novaChavePix.model.ChavePix
import java.time.LocalDateTime

data class CreatePixKeyResponse(
    val keyType: PixkeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {

    // metodo para atualizar a chave que veio do banco central

    fun update(chavePix: ChavePix): ChavePix {
        chavePix.chave = this.key
        return chavePix
    }
}