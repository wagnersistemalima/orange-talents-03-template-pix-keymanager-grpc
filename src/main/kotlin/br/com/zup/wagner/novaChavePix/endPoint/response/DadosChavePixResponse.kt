package br.com.zup.wagner.novaChavePix.endPoint.response

data class DadosChavePixResponse(

    var id: String? = null,                     //opcional
    val identificadorItau: String? = null,       //opcional
    val tipoChave: String?,         //ok
    var chave: String?,          //ok
    val tipoDeConta: String?,     //ok
    val instituicao: String?,     // ok
    val ispb: String?,
    val agencia: String?,           //ok
    val numeroConta: String?,      //ok
    val titular: String?,         // ok
    val cpf: String?,             //ok
    val criadoEm: String?         //ok
)
