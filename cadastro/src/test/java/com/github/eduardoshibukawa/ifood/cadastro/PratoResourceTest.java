package com.github.eduardoshibukawa.ifood.cadastro;

import org.approvaltests.Approvals;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

import java.math.BigDecimal;
import java.util.Optional;

import javax.ws.rs.core.Response.Status;

import com.github.database.rider.cdi.api.DBRider;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.eduardoshibukawa.ifood.cadastro.configuration.CadastroTestLifecycle;
import com.github.eduardoshibukawa.ifood.cadastro.domain.Prato;
import com.github.eduardoshibukawa.ifood.cadastro.dto.AdicionarPratoDTO;
import com.github.eduardoshibukawa.ifood.cadastro.dto.AtualizarPratoDTO;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@QuarkusTest
@QuarkusTestResource(CadastroTestLifecycle.class)
public class PratoResourceTest {

    private static final String PRATOS_ENDPOINT = "/restaurantes/{idRestaurante}/pratos";
    private static final String PRATOS_CENARIO_1_YML = "pratos-cenario-1.yml";

    @Test
    @DataSet(PRATOS_CENARIO_1_YML)
    public void testGet() {
        final String resultado = given()
            .with().pathParam("idRestaurante", 123L)
            .when().get(PRATOS_ENDPOINT)
            .then()
            .statusCode(200)
            .extract().asString();

        Approvals.verifyAsJson(resultado);
    }

    @Test
    @DataSet(PRATOS_CENARIO_1_YML)
    public void testPost() {
        final AdicionarPratoDTO dto = new AdicionarPratoDTO();

        dto.preco = BigDecimal.valueOf(10d);
        dto.nome = "Saciroto";

        givenJsonContentWithIdRestauranteParam(123L)
            .body(dto)
            .when().post(PRATOS_ENDPOINT)
            .then()
            .statusCode(Status.CREATED.getStatusCode());

        final Long quantidadeEsperada = 2L;
        final Long quantidadeAtual = Prato.count();

        Assert.assertEquals(quantidadeEsperada, quantidadeAtual);
    }

    @Test
    @DataSet(PRATOS_CENARIO_1_YML)
    public void testPut() {
        final Long id = 321L;
        final AtualizarPratoDTO dto = new AtualizarPratoDTO();

        dto.preco = BigDecimal.valueOf(5d);

        givenJsonContentWithIdRestauranteParam(123L)
            .with().pathParam("id", id)
            .body(dto)
            .when().put(PRATOS_ENDPOINT + "/{id}")
            .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());

        final Prato atual = Prato.findById(id);

        Assert.assertTrue("Preço deve ser atualizado após PUT", dto.preco.compareTo(atual.preco) == 0);        
    }

    @Test
    @DataSet(PRATOS_CENARIO_1_YML)
    public void testDelete() {
        final Long id = 321L;

        givenJsonContentWithIdRestauranteParam(123L)
            .with().pathParam("id", id)
            .when().delete(PRATOS_ENDPOINT + "/{id}")
            .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());

        final Optional<Prato> op = Prato.findByIdOptional(id);

        Assert.assertTrue("Não deve existir prato", op.isEmpty());        
    }

    private RequestSpecification givenJsonContentWithIdRestauranteParam(Long idRestaurante) {
        return given()
            .contentType(ContentType.JSON)
            .with().pathParam("idRestaurante", idRestaurante);
    }        
}
