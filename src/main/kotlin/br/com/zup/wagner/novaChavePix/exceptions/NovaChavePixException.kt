package br.com.zup.wagner.novaChavePix.exceptions

class NovaChavePixException(val msg: String = "Chave já existe cadastrada" ): RuntimeException(msg) {
}