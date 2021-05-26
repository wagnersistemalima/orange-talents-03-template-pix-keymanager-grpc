package br.com.zup.wagner.novaChavePix.request

import io.micronaut.core.annotation.Introspected


@Introspected
data class PixIdRequest(
    val clientId: String?,               // id iterno da aplicação
    val pixId: String?,                  // identificador itau

)
