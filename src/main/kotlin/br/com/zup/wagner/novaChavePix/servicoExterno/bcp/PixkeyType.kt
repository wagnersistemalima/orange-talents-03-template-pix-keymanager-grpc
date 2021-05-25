package br.com.zup.wagner.novaChavePix.servicoExterno.bcp

import br.com.zup.wagner.novaChavePix.model.TipoDeChaveModel

// Tipo de chave
// CPF -> Interno -> CPF
// EMAIL -> Interno -> EMAIL
// PHONE -> Interno -> CELULAR
// RANDOM -> Interno -> ALEATORIA

enum class PixkeyType {
    CPF,
    EMAIL,
    PHONE,
    RANDOM;


    // traduzir, guando receber um tipoDeChaveModel
    companion object {
        fun translation(tipoDeChaveModel: TipoDeChaveModel): PixkeyType {
            return when(tipoDeChaveModel) {      // when -> comportamento
                TipoDeChaveModel.CPF -> CPF
                TipoDeChaveModel.EMAIL -> EMAIL
                TipoDeChaveModel.CELULAR -> PHONE
                TipoDeChaveModel.ALEATORIA -> RANDOM
            }
        }
    }
}