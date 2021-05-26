package br.com.zup.wagner.novaChavePix.exceptions

import java.lang.RuntimeException

class ChavePixException(val msg: String = "Registro não encontrado"): RuntimeException(msg) {
}