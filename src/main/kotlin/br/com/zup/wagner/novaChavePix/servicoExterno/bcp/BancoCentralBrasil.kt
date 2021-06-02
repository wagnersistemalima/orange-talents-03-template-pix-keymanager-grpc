package br.com.zup.wagner.novaChavePix.servicoExterno.bcp

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client(value = "\${servicos.BCB}")
interface BancoCentralBrasil {

    // end point post enviar registro para o banco central

    @Post("/api/v1/pix/keys")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun creat(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>


    // end point delete um registro no banco central

    @Delete("/api/v1/pix/keys/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun delete(@PathVariable key: String, @Body deletePixKeyRequest: DeletePixKeyRequest): HttpResponse<DeletePixKeyResponse>

    // end point consulta banco central

    @Get("/api/v1/pix/keys/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun consulta(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>




}