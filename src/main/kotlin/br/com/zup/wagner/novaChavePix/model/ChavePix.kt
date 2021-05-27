package br.com.zup.wagner.novaChavePix.model

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

// entidade

@Entity
class ChavePix(

    @field:NotNull
    @Column(nullable = false)                    // obrigatorio
    val identificadorItau: UUID,

    @field:NotNull
    @field:Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoChave: TipoDeChaveModel,

    @field:NotBlank
    @field:Column(unique = true, nullable = false)
    var chave: String,

    @field:NotNull
    @field:Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeConta: TipoDeContaModel,

    @field:NotNull
    @Embedded
    val conta: ContaAssociada,

    ){

    @Id
    @GeneratedValue
    var id: UUID? = null

    @Column(nullable = false)
    val criadoEm: LocalDateTime = LocalDateTime.now()

    // metodo para atualizar o valor da chave pix

    fun atualizaChave(key: String) {
        this.chave = key
    }

    override fun toString(): String {
        return "ChavePix(identificadorItau=$identificadorItau, tipoChave=$tipoChave, chave='$chave', tipoDeConta=$tipoDeConta, conta=$conta, id=$id, criadoEm=$criadoEm)"
    }


}