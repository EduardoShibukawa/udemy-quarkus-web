package com.github.eduardoshibukawa.ifood.cadastro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.eduardoshibukawa.ifood.cadastro.domain.Prato;
import com.github.eduardoshibukawa.ifood.cadastro.domain.Restaurante;
import com.github.eduardoshibukawa.ifood.cadastro.dto.AdicionarPratoDTO;
import com.github.eduardoshibukawa.ifood.cadastro.dto.AtualizarPratoDTO;
import com.github.eduardoshibukawa.ifood.cadastro.dto.PratoDTO;
import com.github.eduardoshibukawa.ifood.cadastro.dto.PratoMapper;

import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/restaurantes/{idRestaurante}/pratos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "prato")
@RolesAllowed("proprietario")
@SecurityScheme(
    securitySchemeName = "ifood-oauth",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(password =  @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/ifood/protocol/openid-connect"))
)
@SecurityRequirements(value = {@SecurityRequirement(name = "ifood-oauth", scopes = {})})
public class PratoResource {

    @Inject
    PratoMapper pratoMapper;

    @GET
    public List<PratoDTO> buscar(@PathParam("idRestaurante") Long idRestaurante) {
        Stream<Prato> pratos = Prato.stream("restaurante", buscaRestaurante(idRestaurante));
        
        return pratos
            .map(p -> pratoMapper.toDTO(p))
            .collect(Collectors.toList());
    }

    @POST
    @Transactional
    public Response adicionar(@PathParam("idRestaurante") Long idRestaurante, AdicionarPratoDTO dto) {
        final Restaurante restaurante = buscaRestaurante(idRestaurante);

        final Prato prato = pratoMapper.toPrato(dto);
        prato.restaurante = restaurante;

        prato.persist();

        return Response.status(Status.CREATED).build();
    }


    @PUT
    @Path("{id}")
    @Transactional
    public void atualizar(@PathParam("idRestaurante") Long idRestaurante, @PathParam("id") Long id, AtualizarPratoDTO dto) {
        final Prato prato = buscaPrato(idRestaurante, id);

        pratoMapper.toPrato(dto, prato);
        
        prato.persist();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void deletar(@PathParam("idRestaurante") Long idRestaurante, @PathParam("id") Long id) {
        final Prato prato = buscaPrato(idRestaurante, id);

        prato.delete();
    }

    private Prato buscaPrato(Long idRestaurante, Long id) {
        final Restaurante restaurante = buscaRestaurante(idRestaurante);

        final Map<String, Object> params = new HashMap<>();
        params.put("restaurante", restaurante);
        params.put("id", id);

        final Optional<Prato> op
                = Prato
                    .find("restaurante = :restaurante and id = :id", params)
                    .firstResultOptional();

        if (op.isEmpty()) {
            throw new NotFoundException();
        }

        return op.get();
    }

    private Restaurante buscaRestaurante(Long id) {
        final Optional<Restaurante> op = Restaurante.findByIdOptional(id);

        if (op.isEmpty()) {
            throw new NotFoundException();
        }

        return op.get();
    }
}