package br.com.zup.wagner.novaChavePix.servicoExterno.bcp

// titular da conta / dono da conta

data class Owner(
    val type: OwnerType,
    val name: String,                            // nome tituar
    val taxIdNumber: String                      // identificação fiscal = cpf

) {
}