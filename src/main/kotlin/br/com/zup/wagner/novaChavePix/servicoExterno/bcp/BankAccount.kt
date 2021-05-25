package br.com.zup.wagner.novaChavePix.servicoExterno.bcp

import br.com.zup.wagner.TipoDeConta
import br.com.zup.wagner.novaChavePix.model.ChavePix
import br.com.zup.wagner.novaChavePix.model.ContaAssociada

// dados da conta

data class BankAccount(
    val participant: String,                 // = numero ispb de retorno da api do itau
    val branch: String,                      // agencia
    val accountNumber: String,              // numero conta
    val accountType: AccountType,           // tipo da conta

) {

    // construtor secundario recebendo uma entidade para construir o objeto

    constructor(chavePix: ChavePix): this(
        participant = chavePix.conta.ispb,
        branch = chavePix.conta.agencia,
        accountNumber = chavePix.conta.numeroConta,
        AccountType.translate(chavePix.tipoDeConta)
    )
}