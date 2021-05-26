package br.com.zup.wagner.novaChavePix.endPoint.response

data class DadosChavePixResponse(

    var id: String? = null,
    val identificadorItau: String? = null,
    val tipoChave: String?,
    var chave: String?,
    val tipoDeConta: String?,
    val instituicao: String?,
    val ispb: String?,
    val agencia: String?,
    val numeroConta: String?,
    val titular: String?,
    val cpf: String?,
    val criadoEm: String?
)
