package br.com.zup.wagner

import br.com.zup.wagner.novaChavePix.endPoint.response.DadosChavePixResponse
import br.com.zup.wagner.novaChavePix.model.ChavePix
import br.com.zup.wagner.novaChavePix.model.ContaAssociada
import br.com.zup.wagner.novaChavePix.model.TipoDeChaveModel
import br.com.zup.wagner.novaChavePix.model.TipoDeContaModel
import br.com.zup.wagner.novaChavePix.repository.ChavePixRepository
import br.com.zup.wagner.novaChavePix.servicoExterno.bcp.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject


@MicronautTest(transactional = false)  // desligar o controle transacional
class CarregaDadosChavePixEndPointTest(
    @Inject val repository: ChavePixRepository,
    @Inject val grpcClient: KeyManagerCarregaChavePixServiceGrpc.KeyManagerCarregaChavePixServiceBlockingStub
    ) {

    @Inject lateinit var apiBancoCentralBrasilClient: BancoCentralBrasil

    lateinit var CHAVE_PIX_EXISTENTE: ChavePix

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
    val key = "33059192057"

    val respostaBancoCentral = PixKeyDetailsResponse(
        keyType = PixkeyType.CPF,
        key = key,
        bankAccount = BankAccount(
            participant = "60701190",
            branch = "0001",
            accountNumber = "123456",
            accountType = AccountType.CACC,
        ),
        owner = Owner(
            type = OwnerType.NATURAL_PERSON,
            name = "Steve Jobs",
            taxIdNumber = "33059192057",

        ),
        createdAt = LocalDateTime.now()
    )

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

    // 1 cenario de teste / deve carregar os dados quando estiver cadastrado no sistema interno

    @Test
    @DisplayName("Deve carregar os dados da chave pix quando estiver cadastrada no sistema interno")
    fun deveCarregarDadosNoSistemaInterno() {

        // cenario

        val response = grpcClient.carrega(CarregaChavePixRequest.newBuilder()
            .setPixId(CarregaChavePixRequest.FiltroPorPixId.newBuilder()
                .setClientId(CHAVE_PIX_EXISTENTE.id.toString())
                .setPixId(CHAVE_PIX_EXISTENTE.identificadorItau.toString())
                .build()
            )

            .build())

        // ação


        // assertivas

        assertNotNull(response)


    }

    //  2 cenario de teste / deve carregar os dados quando  estiver cadastrado no sistema interno, e consultado apenas pela chave

    @Test
    @DisplayName("deve carregar dados, quando solicitar apenas a chave, e a mesma estiver cadastrada no banco interno")
    fun deveCarregarDadosQuandoSolicitarPorChave() {

        // cenario

        val response = grpcClient.carrega(CarregaChavePixRequest.newBuilder()
            .setChave(CHAVE_PIX_EXISTENTE.chave)

            .build())

        // açao


        // assertivas

        assertNotNull(response)
    }

    //  3 cenario de teste / deve carregar os dados quando nao  estiver cadastrado no sistema
    //  interno, e consultado apenas pela chave, e estiver cadastrado no banco central

    @Test
    @DisplayName("deve carregar os dados pelo banco central, quando não tiver registro no banco interno")
    fun deveCarregarDadosPeloBancoCentral() {

        // cenario

        // comportamento do banco central

        Mockito.`when`(apiBancoCentralBrasilClient.consulta(key))
            .thenReturn(HttpResponse.ok(respostaBancoCentral))

        // ação

        val response = grpcClient.carrega(CarregaChavePixRequest.newBuilder()
            .setChave(key)

            .build())

        // assertivas
        assertNotNull(response)
    }

    // 4 cenario de teste / subir exception quando tentar consultar pela chave, e nao
    // estiver cadastrada no banco interno e no banco central

    @Test
    @DisplayName("deve subir exceção, ao tentar consultar chave inesistente")
    fun deveSubirExceçaoAoTentarConsultarChaveInesistente() {

        // cenario

        // comportamento banco central

        Mockito.`when`(apiBancoCentralBrasilClient.consulta(key))
            .thenReturn(HttpResponse.notFound())


        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest.newBuilder()
                .setChave(key)
                .build())
        }

        // assertivas
        assertEquals(Status.FAILED_PRECONDITION.code,response.status.code )

    }

    // 5 cenario de teste / deve subir exception, quando consultado no banco interno
    // pelo pixId existente e clientId inexistente

    @Test
    @DisplayName("deve subir exception ao tentar consultar no banco interno, pelo pixId existente e client id inexistente")
    fun deveSubirExceptionAoTentarConsultarPixIdExistenteAndClientIdInexistente() {

        // cenario
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest.newBuilder()
                .setPixId(CarregaChavePixRequest.FiltroPorPixId.newBuilder()
                    .setClientId(idNaoExiste.toString())
                    .setPixId(CHAVE_PIX_EXISTENTE.identificadorItau.toString())

                    .build())

                .build())
        }

        // ação

        // assertivas
        assertEquals(Status.NOT_FOUND.code, response.status.code)
    }

    // 5 cenario de teste / deve subir exception, quando consultado no banco interno
    // pelo pixId inexistente e clientId existente

    @Test
    @DisplayName("deve subir exception ao tentar consultar no banco interno, pelo pixId inexistente e client id existente")
    fun deveSubirExceptionAoTentarConsultarPixIdInexistenteAndClientIdExistente() {

        // cenario
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest.newBuilder()
                .setPixId(CarregaChavePixRequest.FiltroPorPixId.newBuilder()
                    .setClientId(CHAVE_PIX_EXISTENTE.id.toString())
                    .setPixId(identificadorItauInvalido)

                    .build())

                .build())
        }

        // ação

        // assertivas
        assertEquals(Status.NOT_FOUND.code, response.status.code)
    }
}