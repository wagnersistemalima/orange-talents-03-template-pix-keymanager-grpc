package br.com.zup.wagner.novaChavePix.servicoExterno.bcp

import java.time.LocalDateTime

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
) {
}