package br.com.zup.wagner

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel

// fabrica de servidor grpc--------------------------------------------------------------

@Factory
class GrpcFactoryBean {

    // 1 bean para testes cadastrar chave pix

    @Bean
    fun registraChaveStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegistraChavePixServiceGrpc.KeyManagerRegistraChavePixServiceBlockingStub {
        return KeyManagerRegistraChavePixServiceGrpc.newBlockingStub(channel)
    }

    // 2 bean para testes remover chave pix

    @Bean
    fun removeChaveStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveChavePixServiceGrpc.KeyManagerRemoveChavePixServiceBlockingStub {
        return KeyManagerRemoveChavePixServiceGrpc.newBlockingStub(channel)
    }

    // 3 bean para testes consultar chave pix

    @Bean
    fun consultaChaveStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerCarregaChavePixServiceGrpc.KeyManagerCarregaChavePixServiceBlockingStub {
        return KeyManagerCarregaChavePixServiceGrpc.newBlockingStub(channel)
    }
}