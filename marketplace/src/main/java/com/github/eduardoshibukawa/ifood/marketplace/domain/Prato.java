package com.github.eduardoshibukawa.ifood.marketplace.domain;

import java.math.BigDecimal;
import java.util.stream.StreamSupport;

import com.github.eduardoshibukawa.ifood.marketplace.dto.PratoDTO;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

public class Prato {
    
    public Long id;
    
    public String nome;

    public String descricao;

    public Restaurante restaurante;

    public BigDecimal preco;

    public static Multi<PratoDTO> findAll(PgPool pgPool) {        
        Uni<RowSet<Row>> uniRowSet = pgPool.preparedQuery("select * from prato").execute();

        return transformUniToMulti(uniRowSet);
	}

    public static Multi<PratoDTO> findByRestaurante(PgPool pgPool, Long idRestaurante) {
        final String query = ""
            + "SELECT * "
            + "  FROM prato "
            + " WHERE prato.restaurante_id = $1 "
            + " ORDER BY nome ASC";        

        Uni<RowSet<Row>> uniRowSet 
            = pgPool.preparedQuery(query)
                    .execute(Tuple.of(idRestaurante));

        return transformUniToMulti(uniRowSet);
	}

    private static Multi<PratoDTO> transformUniToMulti(Uni<RowSet<Row>> uniRowSet) {
        return uniRowSet.onItem().transformToMulti(rowSet -> Multi.createFrom().items(
            () -> {
                return StreamSupport.stream(rowSet.spliterator() , false);
            }
        )).onItem().transform(PratoDTO::from);
    }
    
}
