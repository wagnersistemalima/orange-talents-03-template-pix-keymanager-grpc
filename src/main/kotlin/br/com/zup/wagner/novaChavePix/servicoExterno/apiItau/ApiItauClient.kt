package br.com.zup.wagner.novaChavePix.servicoExterno.apiItau


import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "\${servicos.ERP}")
interface ApiItauClient {

    @Get("/api/v1/clientes/{clientId}/contas/{?tipo}")
    fun consulta(@PathVariable clientId: String,@QueryValue tipo: String) : HttpResponse<DadosDaContaResponse>
}





