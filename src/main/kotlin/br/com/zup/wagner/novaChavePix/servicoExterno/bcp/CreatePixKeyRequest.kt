package br.com.zup.wagner.novaChavePix.servicoExterno.bcp

import br.com.zup.wagner.novaChavePix.model.ChavePix

// objeto para enviar dados para banco central


data class CreatePixKeyRequest(
    val keyType: PixkeyType,             // tipo de chave
    val key: String,                     //  valor da chave
    val bankAccount: BankAccount,        // dados da conta
    val owner: Owner                     //  titular ou dono da conta

) {

    // construtor recebendo uma entidade

    constructor(chavePix: ChavePix) : this(
        keyType = PixkeyType.translation(chavePix.tipoChave),
        key = chavePix.chave,
        bankAccount = BankAccount(chavePix),
        owner = Owner(
            OwnerType.NATURAL_PERSON,
            name = chavePix.conta.titular,
            taxIdNumber = chavePix.conta.cpf
        )

    )

}