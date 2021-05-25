package br.com.zup.wagner.novaChavePix.exceptions

import java.lang.RuntimeException

class RemoveChavePixException(var msg: String = "Recurso não encontrado"): RuntimeException(msg) {
}