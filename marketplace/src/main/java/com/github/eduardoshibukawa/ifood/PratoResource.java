package com.github.eduardoshibukawa.ifood;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.eduardoshibukawa.ifood.marketplace.domain.Prato;
import com.github.eduardoshibukawa.ifood.marketplace.dto.PratoDTO;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;

@Path("pratos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PratoResource {

    @Inject
    PgPool pgPool;

    @GET
    /*         
    
        Aparentemente não é mais necessário 
        a API faz o retorno do Multi também
        
     */
    @APIResponse(
        responseCode = "200",
        description = "Buscar todos os pratos",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = PratoDTO.class,
                type = SchemaType.ARRAY
            )
        )
    )
    public Multi<PratoDTO> buscarPratos () {
        return Prato.findAll(pgPool);
    }
}