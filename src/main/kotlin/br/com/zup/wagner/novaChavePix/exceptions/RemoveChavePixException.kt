package br.com.zup.wagner.novaChavePix.exceptions

import java.lang.RuntimeException

class RemoveChavePixException(val msg: String = "Recurso não encontrado"): RuntimeException(msg) {
}