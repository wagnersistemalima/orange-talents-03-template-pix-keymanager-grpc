package br.com.zup.wagner

import br.com.zup.wagner.novaChavePix.model.ChavePix
import br.com.zup.wagner.novaChavePix.model.ContaAssociada
import br.com.zup.wagner.novaChavePix.model.TipoDeChaveModel
import br.com.zup.wagner.novaChavePix.model.TipoDeContaModel
import br.com.zup.wagner.novaChavePix.repository.ChavePixRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*
import javax.inject.Inject


@MicronautTest(transactional = false)
class ListaTodasChavePixEndPointTest(
    @Inject val repository: ChavePixRepository,
    @Inject val grpcClient: KeyManagerCarregaTodasChavePixServiceGrpc.KeyManagerCarregaTodasChavePixServiceBlockingStub
) {

    lateinit var CHAVE_PIX_EXISTENTE_UMA: ChavePix

    lateinit var CHAVE_PIX_EXISTENTE_DUAS: ChavePix

    val identificadorItau = UUID.randomUUID()
    val identificadorItauInvalido = "c56dfef4-7901-44fb-84e2-a2cefb1"
    val identificadorItauOutraPessoa = "5260263c-a3c1-4727-ae32-3bdb2538841b"
    val valorChaveCpf = "02467781054"                     //cpf
    val valorChaveEmail = "rafael@email.com"
    val instituicao = "ITAÚ UNIBANCO S.A."
    val agencia = "0001"
    val numeroConta = "291900"
    val idNaoExiste = UUID.randomUUID()
    val nomeTitular = "Rafael M C Ponte"
    val cpf = "02467781054"     // cpf do Rafael Pontes
    val ispb = "60701190"
    val key = "33059192057"

    // rodar antes de cada teste

    @BeforeEach
    fun setUp() {
        CHAVE_PIX_EXISTENTE_UMA = ChavePix(
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

        CHAVE_PIX_EXISTENTE_DUAS = ChavePix(
            identificadorItau = identificadorItau,
            tipoChave = TipoDeChaveModel.EMAIL,
            chave = valorChaveEmail,
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

        repository.save(CHAVE_PIX_EXISTENTE_UMA)
        repository.save(CHAVE_PIX_EXISTENTE_DUAS)
    }

    // rodar depois de cada teste

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
    }

    // 1 cenario de teste / deve carregar todas as chaves pix do cliente solicitado

    @Test
    @DisplayName("deve carregar todas as chaves do cliente solicitado")
    fun deveCarregarTodasasChavesDoClienteSolicitado() {

        // cenario

        // ação

        val response = grpcClient.listarTodas(ListarTodasPixRequest.newBuilder()
            .setClientId(CHAVE_PIX_EXISTENTE_UMA.identificadorItau.toString())

            .build())


        // assertivas

        assertEquals(2, response.chavesCount)

    }

    // 2 cenario de teste / deve retornar lista vazia, quando clientId nao existir no banco

    @Test
    @DisplayName("deve retornar lista vazia, quando clientId nao existir no banco")
    fun deveRetornarUmaListaVazia() {

        // cenario

        // ação

        val response = grpcClient.listarTodas(ListarTodasPixRequest.newBuilder()
            .setClientId(identificadorItauInvalido)

            .build())


        // assertivas

        assertEquals(0, response.chavesCount)

    }

    // 3 cenario de teste / deve retornar uma exception quando for informado o clientId vazio

    @Test
    @DisplayName("deve retornar uma exception quando for informado o clientId vazio")
    fun deveRetornarUmaException() {

        // cenario

        // ação

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.listarTodas(ListarTodasPixRequest.newBuilder()
                .setClientId(" ")

                .build())
        }


        // assertivas

        assertEquals(Status.INVALID_ARGUMENT.code, response.status.code)

    }


}