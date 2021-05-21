package br.com.zup.wagner.novaChavePix.repository

import br.com.zup.wagner.novaChavePix.model.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, UUID> {

    fun existsByChave(valorChave: String?): Boolean

    fun findByIdAndIdentificadorItau(uuidPixId: UUID?, uuidIdentificadorItau: UUID?): ChavePix?


}