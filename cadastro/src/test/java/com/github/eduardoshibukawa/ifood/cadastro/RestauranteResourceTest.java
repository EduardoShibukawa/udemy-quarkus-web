package com.github.eduardoshibukawa.ifood.cadastro;

import org.approvaltests.Approvals;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

import java.util.Optional;

import javax.ws.rs.core.Response.Status;

import com.github.database.rider.cdi.api.DBRider;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.eduardoshibukawa.ifood.cadastro.configuration.CadastroTestLifecycle;
import com.github.eduardoshibukawa.ifood.cadastro.domain.Restaurante;
import com.github.eduardoshibukawa.ifood.cadastro.dto.AdicionarRestauranteDTO;
import com.github.eduardoshibukawa.ifood.cadastro.dto.AtualizarRestauranteDTO;
import com.github.eduardoshibukawa.ifood.cadastro.util.TokenUtils;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@QuarkusTest
@QuarkusTestResource(CadastroTestLifecycle.class)
public class RestauranteResourceTest {

    private static final String RESTAURANTES_ENDPOINT = "/restaurantes";
    private static final String RESTAURANTES_CENARIO_1_YML = "restaurantes-cenario-1.yml";
    private String token;

    @BeforeEach
    public void gerarToken() throws Exception {
        token = TokenUtils.generateTokenString("/JWTProprietarioClaims.json", null);
    }

    @Test
    @DataSet(RESTAURANTES_CENARIO_1_YML)
    public void testGet() {
        final String resultado = givenWithToken()
            .when().get(RESTAURANTES_ENDPOINT)
            .then()
            .statusCode(Status.OK.getStatusCode())
                .extract().asString();

        Approvals.verifyAsJson(resultado);
    }

    @Test
    @DataSet(RESTAURANTES_CENARIO_1_YML)
    public void testPost() {
        final AdicionarRestauranteDTO dto = new AdicionarRestauranteDTO();

        dto.nomeFantasia = "Saci Lanches";
        dto.proprietario = "Saci";        

        givenJsonContent()
            .body(dto)
            .when().post(RESTAURANTES_ENDPOINT)
            .then()
            .statusCode(Status.CREATED.getStatusCode());

        final Long quantidadeEsperada = 3L;
        final Long quantidadeAtual = Restaurante.count();

        Assert.assertEquals(quantidadeEsperada, quantidadeAtual);
    }

    @Test
    @DataSet(RESTAURANTES_CENARIO_1_YML)
    public void testPut() {
        final Long id = 123L;
        final AtualizarRestauranteDTO dto = new AtualizarRestauranteDTO();

        dto.nomeFantasia = "Capivara Lanches";        

        givenJsonContent()
            .with().pathParam("id", id)
            .body(dto)
            .when().put(RESTAURANTES_ENDPOINT + "/{id}")
            .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());

        final Restaurante atual = Restaurante.findById(id);

        Assert.assertEquals(dto.nomeFantasia, atual.nome);
    }

    @Test
    @DataSet(RESTAURANTES_CENARIO_1_YML)
    public void testDelete() {
        final Long id = 123L;

        givenJsonContent()
            .with().pathParam("id", id)
            .when().delete(RESTAURANTES_ENDPOINT + "/{id}")
            .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());

        final Optional<Restaurante> op = Restaurante.findByIdOptional(id);

        Assert.assertTrue("Não deve existir restaurante", op.isEmpty());        
    }

    private RequestSpecification givenWithToken() {
        return given()
            .header(new Header("Authorization", "Bearer " + token));
    }

    private RequestSpecification givenJsonContent() {
        return givenWithToken().contentType(ContentType.JSON);
    }    
}
