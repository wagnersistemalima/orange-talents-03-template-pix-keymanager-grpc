package br.com.zup.wagner.novaChavePix.model

import javax.persistence.Embeddable

@Embeddable
class ContaAssociada(
    val instituicao: String,
    val agencia: String,
    val numeroConta: String,
    val titular: String,
    val cpf: String

)




