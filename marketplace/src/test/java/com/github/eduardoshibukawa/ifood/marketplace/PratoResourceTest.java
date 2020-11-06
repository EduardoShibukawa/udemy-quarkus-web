package com.github.eduardoshibukawa.ifood.marketplace;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PratoResourceTest {

    @Test
    public void testHelloEndpoint() {
        final String body 
            = given()
                .when().get("/pratos")
                .then()
                    .statusCode(200).extract().asString();
                    
        System.out.print(body);
    }

}