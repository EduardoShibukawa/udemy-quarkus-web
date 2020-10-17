package com.github.eduardoshibukawa.ifood.cadastro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Path("/restaurantes/{idRestaurante}/pratos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "prato")
public class PratoResource {

    @GET
    public List<Prato> buscar(@PathParam("idRestaurante") Long idRestaurante) {
        return Prato.list("restaurante", buscaRestaurante(idRestaurante));
    }

    @POST
    @Transactional
    public Response adicionar(@PathParam("idRestaurante") Long idRestaurante, Prato dto) {
        final Restaurante restaurante = buscaRestaurante(idRestaurante);

        final Prato prato = new Prato();

        prato.nome = dto.nome;
        prato.descricao = dto.descricao;
        prato.preco = dto.preco;
        prato.restaurante = restaurante;

        prato.persist();

        return Response.status(Status.CREATED).build();
    }


    @PUT
    @Path("{id}")
    @Transactional
    public void atualizar(@PathParam("idRestaurante") Long idRestaurante, @PathParam("id") Long id, Prato dto) {
        final Prato prato = buscaPrato(idRestaurante, id);

        prato.preco = dto.preco;

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