package br.com.zup.wagner.novaChavePix.request

import br.com.zup.wagner.novaChavePix.validation.annotation.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RemoveChavePixRequest(

    @field:ValidUUID
    @field:NotBlank
    val pixId: String?,

    @field:ValidUUID
    @field:NotBlank
    val identificadorItau: String?
)
