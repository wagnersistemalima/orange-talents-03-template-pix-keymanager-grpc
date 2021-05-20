package br.com.zup.wagner.novaChavePix.request

import br.com.zup.wagner.TipoDeConta
import br.com.zup.wagner.novaChavePix.model.ContaAssociada
import br.com.zup.wagner.novaChavePix.model.ChavePix
import br.com.zup.wagner.novaChavePix.model.TipoDeChaveModel
import br.com.zup.wagner.novaChavePix.model.TipoDeContaModel
import br.com.zup.wagner.novaChavePix.validation.annotation.ValidPixKey
import br.com.zup.wagner.novaChavePix.validation.annotation.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@ValidPixKey
@Introspected
data class NovaChavePixRequest(

    @field:ValidUUID
    @field:NotBlank
    val clientId: String?,

    @field:NotNull
    val tipoChave: TipoDeChaveModel?,

    @field:Size(max = 77)
    val valorChave: String?,

    @field:NotNull
    val tipoConta: TipoDeConta?
) {

    // metodo

    fun toModel(conta: ContaAssociada): ChavePix {
        return ChavePix(
            identificadorItau = UUID.fromString(this.clientId),
            tipoChave = TipoDeChaveModel.valueOf(tipoChave!!.name),
            chave = if (this.tipoChave == TipoDeChaveModel.ALEATORIA) UUID.randomUUID().toString() else this.valorChave!!,
            tipoDeConta = TipoDeContaModel.valueOf(this.tipoConta!!.name),
            conta = conta
        )
    }
}
