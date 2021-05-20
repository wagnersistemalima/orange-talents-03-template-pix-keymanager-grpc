package br.com.zup.wagner.novaChavePix.servicoExterno

import br.com.zup.wagner.novaChavePix.model.ContaAssociada

data class DadosDaContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    fun toModel() : ContaAssociada {
        return ContaAssociada(
            instituicao = this.instituicao.nome,
            agencia = this.agencia,
            numeroConta = numero,
            titular = this.titular.nome,
            cpf = this.titular.cpf
        )
    }
}
