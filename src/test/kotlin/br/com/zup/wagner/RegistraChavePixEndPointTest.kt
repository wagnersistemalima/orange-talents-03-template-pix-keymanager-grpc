package br.com.zup.wagner

import br.com.zup.wagner.novaChavePix.model.ChavePix
import br.com.zup.wagner.novaChavePix.model.ContaAssociada
import br.com.zup.wagner.novaChavePix.model.TipoDeChaveModel
import br.com.zup.wagner.novaChavePix.model.TipoDeContaModel
import br.com.zup.wagner.novaChavePix.repository.ChavePixRepository
import br.com.zup.wagner.novaChavePix.servicoExterno.apiItau.InstituicaoResponse
import br.com.zup.wagner.novaChavePix.servicoExterno.apiItau.TitularResponse
import br.com.zup.wagner.novaChavePix.servicoExterno.apiItau.ApiItauClient
import br.com.zup.wagner.novaChavePix.servicoExterno.apiItau.DadosDaContaResponse
import br.com.zup.wagner.novaChavePix.servicoExterno.bcp.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

// lateinit = propriedade precisará ser inicializada assim que possível

@MicronautTest(transactional = false)  // servidor embarcado grpc não vai participar
class RegistraChavePixEndPointTest(
    @Inject val repository: ChavePixRepository,
    @Inject val grpcClient: KeyManagerRegistraChavePixServiceGrpc.KeyManagerRegistraChavePixServiceBlockingStub
) {


    // consultar os dados no serviço itau-----------------------------------------------------

    @Inject
    lateinit var apiItauClient: ApiItauClient

    @Inject
    lateinit var apiBancoCentralBrasilClient: BancoCentralBrasil

    val keyAleatorio = UUID.randomUUID().toString()
    val identificadorItau = UUID.randomUUID()
    val tipoDeConta = TipoDeContaModel.CONTA_CORRENTE
    val valorChave = "02467781054"                     //cpf
    val valorChaveCelular = "+5583993809934"          // celular
    val valorChaveEmail = "rafael@gmail.com"
    val valorChaveAleatoria = UUID.randomUUID()
    val instituicao = "ITAÚ UNIBANCO S.A."
    val ispb = "60701190"
    val agencia = "0001"
    val numeroConta = "291900"
    val id = "c56dfef4-7901-44fb-84e2-a2cefb157890"
    val nomeTitular = "Rafael M C Ponte"
    val cpf = "02467781054"     // cpf do Rafael Pontes
    val email = "rafael@gmail.com"

    @MockBean(ApiItauClient::class)   // mockando o serviço externo
    fun apiItauClient(): ApiItauClient {
        return Mockito.mock(ApiItauClient::class.java)
    }

    @MockBean(BancoCentralBrasil::class)
    fun apiBancoCentralBrasilClient(): BancoCentralBrasil {
        return Mockito.mock(BancoCentralBrasil::class.java)
    }

    val dadosDaContaItauResponse = DadosDaContaResponse(
            tipo = tipoDeConta.toString(),
            InstituicaoResponse(nome = instituicao, ispb = ispb),
            agencia = agencia,
            numero = numeroConta,
            TitularResponse(id = id, nome = nomeTitular, cpf = cpf)
        )




    // rodar antes de cada teste ------------------------------------------------------

    @BeforeEach
    fun setUp() {

    }

    // rodar depois de cada teste

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
    }

    // 1º cenario de testes/ inserção dos dados chave pix gerada cpf----------------------------------

    @Test
    @DisplayName("Deve inserir dados da chave pix no banco")
    fun deveGerarChavePixCpf() {

        // cenario,

        val createPixKeyRequest = CreatePixKeyRequest(
            key = valorChave,
            keyType = PixkeyType.CPF,
            bankAccount = BankAccount(
                participant = ispb,
                branch = agencia,
                accountNumber = numeroConta,
                accountType = AccountType.CACC,),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = nomeTitular,
                taxIdNumber = cpf
            )

        )

        val createPixKeyResponse = CreatePixKeyResponse(
            keyType = PixkeyType.CPF,
            key = cpf,
            bankAccount = BankAccount(
                participant = ispb,
                branch = agencia,
                accountNumber = numeroConta,
                accountType = AccountType.CACC),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = nomeTitular,
                taxIdNumber = cpf),
            createdAt = LocalDateTime.now()
        )


        // comportamento api itau

        `when`(apiItauClient.consulta(Mockito.anyString(), tipo = Mockito.anyString()))
            .thenReturn(HttpResponse.ok(dadosDaContaItauResponse))

        // comportamento banco central
        `when` (apiBancoCentralBrasilClient.creat(createPixKeyRequest))
            .thenReturn(HttpResponse.created(createPixKeyResponse))



        // ação serviço Bloom Grpc envia dados para o end point

        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setClientId(identificadorItau.toString())  // -> gerado randomicamente UUID
                .setTipoDeChave(TipoDeChave.CPF)
                .setValorChave(cpf)
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        // assertivias

        assertNotNull(response.pixId)
        assertTrue(repository.existsByChave(valorChave))  // existe a chave pix criada, teste efeito colateral
    }

    // 2º cenario de teste/ inserção dos dados chave pix gerada celular----------------------------------

    @Test
    @DisplayName("Deve inserir uma chave pix com o numero do telefone celular")
    fun deveGerarChavePixCelular() {

        // cenario

        val createPixKeyRequest = CreatePixKeyRequest(
            key = valorChaveCelular,
            keyType = PixkeyType.PHONE,
            bankAccount = BankAccount(
                participant = ispb,
                branch = agencia,
                accountNumber = numeroConta,
                accountType = AccountType.CACC,),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = nomeTitular,
                taxIdNumber = cpf
            )

        )

        val createPixKeyResponse = CreatePixKeyResponse(
            keyType = PixkeyType.PHONE,
            key = valorChaveCelular,
            bankAccount = BankAccount(
                participant = ispb,
                branch = agencia,
                accountNumber = numeroConta,
                accountType = AccountType.CACC),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = nomeTitular,
                taxIdNumber = cpf),
            createdAt = LocalDateTime.now()
        )

        // comportamento itau

        `when`(apiItauClient.consulta(clientId = Mockito.anyString(), tipo = Mockito.anyString()))
            .thenReturn(HttpResponse.ok(dadosDaContaItauResponse))

//        // comportamento banco central
        `when` (apiBancoCentralBrasilClient.creat(createPixKeyRequest))
            .thenReturn(HttpResponse.created(createPixKeyResponse))

        // ação  ação serviço Bloom Grpc envia dados para o end point

        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setClientId(identificadorItau.toString())
                .setTipoDeChave(TipoDeChave.CELULAR)
                .setValorChave("+5583993809934")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        //assertivas

        assertNotNull(response.pixId)
        assertTrue(repository.existsByChave(valorChaveCelular))

    }

    // 3 cenario de testes / inserção dos dados chave pix gerada email

    @Test
    @DisplayName("Deve inserir dados e gerar chave pix email")
    fun deveGerarChavePixEmail() {

        // cenario

        val createPixKeyRequest = CreatePixKeyRequest(
            key = valorChaveEmail,
            keyType = PixkeyType.EMAIL,
            bankAccount = BankAccount(
                participant = ispb,
                branch = agencia,
                accountNumber = numeroConta,
                accountType = AccountType.CACC,),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = nomeTitular,
                taxIdNumber = cpf
            )

        )

        val createPixKeyResponse = CreatePixKeyResponse(
            keyType = PixkeyType.EMAIL,
            key = valorChaveEmail,
            bankAccount = BankAccount(
                participant = ispb,
                branch = agencia,
                accountNumber = numeroConta,
                accountType = AccountType.CACC),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = nomeTitular,
                taxIdNumber = cpf),
            createdAt = LocalDateTime.now()
        )

        // comportamento itau

        `when`(apiItauClient.consulta(clientId = Mockito.anyString(), tipo = Mockito.anyString()))
            .thenReturn(HttpResponse.ok(dadosDaContaItauResponse))

        // comportamento banco central
        `when` (apiBancoCentralBrasilClient.creat(createPixKeyRequest))
            .thenReturn(HttpResponse.created(createPixKeyResponse))

        // ação

        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setClientId(identificadorItau.toString())
                .setTipoDeChave(TipoDeChave.EMAIL)
                .setValorChave(email)
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        // assertivas

        assertNotNull(response.pixId)
        assertTrue(repository.existsByChave(valorChaveEmail))
    }

    // 4 cenario de testes / não deve cadastrar chave pix, quando campo for invalido

    @Test
    @DisplayName("não deve cadastrar nova chave quando campo inválido")
    fun naoDeveCadastrarChavePixCampoInvalido() {
        // cenario

        // ação

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setClientId("")
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setValorChave("")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        // assertivas

        assertEquals(Status.INVALID_ARGUMENT.code, response.status.code)

    }

    // 5 cenario de testes / nao pode cadastrar chave pix, quando chave já cadastrada no banco

    @Test
    @DisplayName("Nao deve cadastrar chave pix, quando chave pix ja existe no banco")
    fun naoDeveCadastrarChavePixQuandoChaveJaEstiverCadastradaNoBanco() {
        // cenario

        val chave = repository.save(
            ChavePix(
                identificadorItau = identificadorItau,
                tipoChave = TipoDeChaveModel.CPF,
                chave = valorChaveEmail,
                tipoDeConta = TipoDeContaModel.CONTA_CORRENTE,
                ContaAssociada(
                    instituicao = instituicao,
                    ispb = ispb,
                    agencia = agencia,
                    numeroConta = numeroConta,
                    titular = nomeTitular,
                    cpf = cpf
                )
            )
        )

        // ação

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setClientId(identificadorItau.toString())
                    .setTipoDeChave(TipoDeChave.EMAIL)
                    .setValorChave(email)
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        // assertivas

        assertEquals(Status.ALREADY_EXISTS.code, response.status.code)
    }

    // 6 cenario de testes / nao deve cadastrar chave, quando o cliente não consta no itau

    @Test
    @DisplayName("Nao deve cadastrar chave pix, quando o cliente nao consta no itau")
    fun naoDeveCadastrarChaveParaClienteQueNaoConstaNoItau() {
        // cenario

        `when`(apiItauClient.consulta(clientId = Mockito.anyString(), tipo = Mockito.anyString()))
            .thenThrow(HttpClientResponseException::class.java)

        // ação

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setClientId(identificadorItau.toString())
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setValorChave("04394450438")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        // assertivas
        assertEquals(Status.UNKNOWN.code, response.status.code)

    }

    // 7 cenario / deve inserir dados e registrar chave pix aleatoria

    @Test
    @DisplayName("Deve cadastrar dados e registrar chave pix aleatoria")
    fun deveCadastrarChavePixAleatoria() {

        // cenario

        val createPixKeyRequest = CreatePixKeyRequest(
            key = keyAleatorio,
            keyType = PixkeyType.RANDOM,
            bankAccount = BankAccount(
                participant = ispb,
                branch = agencia,
                accountNumber = numeroConta,
                accountType = AccountType.CACC,),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = nomeTitular,
                taxIdNumber = cpf
            )

        )

        val createPixKeyResponse = CreatePixKeyResponse(
            keyType = PixkeyType.RANDOM,
            key = keyAleatorio,
            bankAccount = BankAccount(
                participant = ispb,
                branch = agencia,
                accountNumber = numeroConta,
                accountType = AccountType.CACC),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = nomeTitular,
                taxIdNumber = cpf),
            createdAt = LocalDateTime.now()
        )
        // comportamento itau

        `when` (apiItauClient.consulta(clientId = Mockito.anyString(), tipo = Mockito.anyString()))
            .thenReturn(HttpResponse.ok(dadosDaContaItauResponse))

        // comportamento banco central
        `when` (apiBancoCentralBrasilClient.creat(MockitoHelper.anyObject()))
            .thenReturn(HttpResponse.created(createPixKeyResponse))

        //açao

        val response = grpcClient.registra(RegistraChavePixRequest.newBuilder()
            .setClientId(identificadorItau.toString())
            .setTipoDeChave(TipoDeChave.ALEATORIA)
            .setValorChave("")
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build())

        // assertivas

        assertNotNull(keyAleatorio)
    }

    // 8 cenario / nao deve cadastrar chave pix aleatoria, quando o valor da chave for preenchido

    @Test
    @DisplayName("Nao deve cadastrar chave pix aleatoria quando o valor da chave for preenchido")
    fun NaodeveCadastrarChavePixAleatoriaQuandoOvalorDaChaveForPreenchido() {

        // cenario

        `when` (apiItauClient.consulta(clientId = Mockito.anyString(), tipo = Mockito.anyString()))
            .thenReturn(HttpResponse.ok(dadosDaContaItauResponse))

        //açao


        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder()
                .setClientId(identificadorItau.toString())
                .setTipoDeChave(TipoDeChave.ALEATORIA)
                .setValorChave("04394450438")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        // assertivas

        assertEquals(Status.INVALID_ARGUMENT.code, response.status.code)
    }

    // 9 cenario / nao deve cadastrar chave pix quando não for possivel cadastrar chave banco central

    @Test
    @DisplayName("nao deve cadastrar chave pix quando não for possivel cadastrar chave banco central")
    fun naoDeveCadastrarChaveQuandoNaoRegistrarNoBancoCentral() {

        // cenario

        val createPixKeyRequest = CreatePixKeyRequest(
            key = valorChave,
            keyType = PixkeyType.CPF,
            bankAccount = BankAccount(
                participant = ispb,
                branch = agencia,
                accountNumber = numeroConta,
                accountType = AccountType.CACC,),
            owner = Owner(
                type = OwnerType.NATURAL_PERSON,
                name = nomeTitular,
                taxIdNumber = cpf
            )

        )

        // comportamento itau

        `when` (apiItauClient.consulta(clientId = Mockito.anyString(), tipo = Mockito.anyString()))
            .thenReturn(HttpResponse.ok(dadosDaContaItauResponse))

        // comportamento banco central
        `when` (apiBancoCentralBrasilClient.creat(createPixKeyRequest))
            .thenReturn(HttpResponse.badRequest())

        //açao

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder()
                .setClientId(identificadorItau.toString())
                .setTipoDeChave(TipoDeChave.ALEATORIA)
                .setValorChave("04394450438")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        // assertivas

        assertEquals(Status.INVALID_ARGUMENT.code, response.status.code)
    }

}


