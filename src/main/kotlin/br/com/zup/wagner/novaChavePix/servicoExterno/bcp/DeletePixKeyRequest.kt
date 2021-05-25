package br.com.zup.wagner.novaChavePix.servicoExterno.bcp


data class DeletePixKeyRequest(
    val key: String,                      //valor chave
    val participant: String              // = numero ispb de retorno da api do itau
)

