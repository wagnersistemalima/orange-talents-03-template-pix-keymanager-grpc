package br.com.zup.wagner

import br.com.zup.wagner.novaChavePix.model.ChavePix
import br.com.zup.wagner.novaChavePix.model.ContaAssociada
import br.com.zup.wagner.novaChavePix.model.TipoDeChaveModel
import br.com.zup.wagner.novaChavePix.model.TipoDeContaModel
import br.com.zup.wagner.novaChavePix.repository.ChavePixRepository
import br.com.zup.wagner.novaChavePix.servicoExterno.bcp.BancoCentralBrasil
import br.com.zup.wagner.novaChavePix.servicoExterno.bcp.DeletePixKeyRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@MicronautTest(transactional = false)  // desligar o controle transacional
class RemoveChavePixEndPointTest(
    @Inject val repository: ChavePixRepository,
    @Inject val grpcClient: KeyManagerRemoveChavePixServiceGrpc.KeyManagerRemoveChavePixServiceBlockingStub
) {

    lateinit var CHAVE_PIX_EXISTENTE: ChavePix

    @Inject
    lateinit var apiBancoCentralBrasilClient: BancoCentralBrasil

    val identificadorItau = UUID.randomUUID()
    val identificadorItauInvalido = "c56dfef4-7901-44fb-84e2-a2cefb1"
    val identificadorItauOutraPessoa = "5260263c-a3c1-4727-ae32-3bdb2538841b"
    val valorChaveCpf = "02467781054"                     //cpf
    val instituicao = "ITAÚ UNIBANCO S.A."
    val agencia = "0001"
    val numeroConta = "291900"
    val idNaoExiste = UUID.randomUUID()
    val nomeTitular = "Rafael M C Ponte"
    val cpf = "02467781054"     // cpf do Rafael Pontes
    val ispb = "60701190"

    // rodar antes de cada teste

    @BeforeEach
    internal fun setUp() {
        CHAVE_PIX_EXISTENTE = ChavePix(
            identificadorItau = identificadorItau,
            tipoChave = TipoDeChaveModel.CPF,
            chave = valorChaveCpf,
            tipoDeConta = TipoDeContaModel.CONTA_CORRENTE,
            conta = ContaAssociada(
                instituicao = instituicao,
                ispb = ispb,
                agencia = agencia,
                numeroConta = numeroConta,
                titular = nomeTitular,
                cpf = cpf
            )
        )
        repository.save(CHAVE_PIX_EXISTENTE)


    }

    // rodar depois de cada teste

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
    }

    // mockando Banco central client

    @MockBean(BancoCentralBrasil::class)
    fun apiBancoCentralBrasilClient(): BancoCentralBrasil {
        return Mockito.mock(BancoCentralBrasil::class.java)
    }

    // 1 cenario de teste / deve remover uma chave pix existente------------------------------------

    @Test
    @DisplayName("Deve remover chave pix existente no banco")
    fun deveRemoverChavePixExistente() {

        // cenario

        // comportamento banco central
        Mockito.`when`(apiBancoCentralBrasilClient.delete(Mockito.anyString(), MockitoHelper.anyObject()))
            .thenReturn(HttpResponse.ok())

        // ação

        val response = grpcClient.delete(
            DeleteChavePixRequest.newBuilder()
                .setPixId(CHAVE_PIX_EXISTENTE.id.toString())
                .setIdentificadorItau(CHAVE_PIX_EXISTENTE.identificadorItau.toString())
                .build()
        )

        // assertivas

        assertEquals(CHAVE_PIX_EXISTENTE.id.toString(), response.pixId)
    }

    // 2 cenario de teste / não deve remover chave pix que não existe, deve subir erro not found

    @Test
    @DisplayName("Nao deve remover chave pix que não exista no banco")
    fun naoDeveRemoverChavePixInexistente() {

        // cenario

        // comportamento banco central
        Mockito.`when`(apiBancoCentralBrasilClient.delete(Mockito.anyString(), MockitoHelper.anyObject()))
            .thenReturn(HttpResponse.badRequest())

        // ação

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.delete(
                DeleteChavePixRequest.newBuilder()
                    .setPixId(idNaoExiste.toString())
                    .setIdentificadorItau(CHAVE_PIX_EXISTENTE.identificadorItau.toString())
                    .build()
            )
        }

        // assertivas
        assertEquals(Status.NOT_FOUND.code, response.status.code)
    }

    // 3 cenario nao deve remover chave pix caso cliente passe o valor do pixId em branco, subir exceção INVALID_ARGUMENT

    @Test
    @DisplayName("deve subir exceção, quando cliente tentar remover pixId e passar o valor em branco")
    fun deveLançarExceptionInvalidArgument() {

        // cenario

        // comportamento banco central
        Mockito.`when`(apiBancoCentralBrasilClient.delete(Mockito.anyString(), MockitoHelper.anyObject()))
            .thenReturn(HttpResponse.badRequest())

        // ação

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.delete(
                DeleteChavePixRequest.newBuilder()
                    .setPixId("")
                    .setIdentificadorItau(identificadorItau.toString())
                    .build()
            )
        }

        //assertivas
        assertEquals(Status.INVALID_ARGUMENT.code, response.status.code)

    }

    // 4 cenario / deve subir exception quando tentar remover chave pix, e passar identificador itau invalido

    @Test
    @DisplayName("deve subir exceção ao tentar remover chave pix, passando o identificador do itau invalido")
    fun deveSubirExceptionAoTentarRemoverChavePixPassandoIdentificadorItauInvalido () {

        // cenario

        // comportamento banco central
        Mockito.`when`(apiBancoCentralBrasilClient.delete(Mockito.anyString(), MockitoHelper.anyObject()))
            .thenReturn(HttpResponse.badRequest())

        // ação

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.delete(
                DeleteChavePixRequest.newBuilder()
                    .setPixId(CHAVE_PIX_EXISTENTE.id.toString())
                    .setIdentificadorItau(identificadorItauInvalido)
                    .build()
            )
        }

        //assertivas
        assertEquals(Status.INVALID_ARGUMENT.code, response.status.code)
    }

    // 5 cenario de teste / deve subir exceção ao tentar remover chave pix existente, mas o identificadorItau pertencente a outra pessoa

    @Test
    @DisplayName("Deve subir exceção, quando tentar remover chavePix existente mas com outro identificadorItau")
    fun deveSubirExceptionAoTentarRemoverChavePixExistenteMasComIdentificadorItauDeOutraPessoa() {

        // cenario

        // comportamento banco central
        Mockito.`when`(apiBancoCentralBrasilClient.delete(Mockito.anyString(), MockitoHelper.anyObject()))
            .thenReturn(HttpResponse.badRequest())

        // ação

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.delete(
                DeleteChavePixRequest.newBuilder()
                    .setPixId(CHAVE_PIX_EXISTENTE.id.toString())
                    .setIdentificadorItau(identificadorItauOutraPessoa)
                    .build()
            )
        }

        // assertivas
        assertEquals(Status.NOT_FOUND.code, response.status.code)
    }



}