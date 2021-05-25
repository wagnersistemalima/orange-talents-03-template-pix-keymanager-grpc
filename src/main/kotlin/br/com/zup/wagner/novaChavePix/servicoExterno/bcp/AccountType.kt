package br.com.zup.wagner.novaChavePix.servicoExterno.bcp

import br.com.zup.wagner.novaChavePix.model.TipoDeContaModel

// Tipo de conta

// CACC -> interno ->

enum class AccountType {

    CACC,
    SVGS;

    // traduzir, guando receber um tipoDeContaModel

    companion object {
        fun translate(tipoDeContaModel: TipoDeContaModel): AccountType {
            return when(tipoDeContaModel) {
                TipoDeContaModel.CONTA_CORRENTE -> CACC
                TipoDeContaModel.CONTA_POUPANCA -> SVGS
            }
        }
    }
}