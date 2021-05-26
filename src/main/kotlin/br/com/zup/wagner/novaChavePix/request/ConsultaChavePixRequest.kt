package br.com.zup.wagner.novaChavePix.request

import io.micronaut.core.annotation.Introspected

@Introspected
data class ConsultaChavePixRequest(
    val pixId: PixIdRequest?,                      // 1 º opção pesquisa
    val chave: String?                    // 2º opção pesquisa
)
