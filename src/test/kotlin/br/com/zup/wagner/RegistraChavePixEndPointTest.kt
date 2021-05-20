package br.com.zup.wagner

import br.com.zup.wagner.novaChavePix.model.ChavePix
import br.com.zup.wagner.novaChavePix.model.ContaAssociada
import br.com.zup.wagner.novaChavePix.model.TipoDeChaveModel
import br.com.zup.wagner.novaChavePix.model.TipoDeContaModel
import br.com.zup.wagner.novaChavePix.repository.ChavePixRepository
import br.com.zup.wagner.novaChavePix.servicoExterno.ApiItauClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
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


    val identificadorItau = UUID.randomUUID()
    val tipoDeConta = TipoDeContaModel.CONTA_CORRENTE
    val tipoDeChave = TipoDeChaveModel.CPF
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

    val dadosDaContaItauResponse = br.com.zup.wagner.novaChavePix.servicoExterno
        .DadosDaContaResponse(
            tipo = tipoDeConta.toString(),
            br.com.zup.wagner.novaChavePix.servicoExterno.InstituicaoResponse(nome = instituicao, ispb = ispb),
            agencia = agencia,
            numero = numeroConta,
            br.com.zup.wagner.novaChavePix.servicoExterno.TitularResponse(id = id, nome = nomeTitular, cpf = cpf)
        )

    // fabrica de servidor grpc--------------------------------------------------------------

    @Factory
    class Client { // Grpc client
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegistraChavePixServiceGrpc.KeyManagerRegistraChavePixServiceBlockingStub {
            return KeyManagerRegistraChavePixServiceGrpc.newBlockingStub(channel)
        }
    }

    // rodar antes de cada teste ------------------------------------------------------

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    // 1º cenario de testes/ inserção dos dados chave pix gerada cpf----------------------------------

    @Test
    @DisplayName("Deve inserir dados da chave pix no banco")
    fun deveGerarChavePixCpf() {

        // cenario, comportamento api itau

        `when`(apiItauClient.consulta(Mockito.anyString(), tipo = Mockito.anyString()))
            .thenReturn(HttpResponse.ok(dadosDaContaItauResponse))


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

        `when`(apiItauClient.consulta(clientId = Mockito.anyString(), tipo = Mockito.anyString()))
            .thenReturn(HttpResponse.ok(dadosDaContaItauResponse))

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

        `when`(apiItauClient.consulta(clientId = Mockito.anyString(), tipo = Mockito.anyString()))
            .thenReturn(HttpResponse.ok(dadosDaContaItauResponse))

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

}


