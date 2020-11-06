package com.github.eduardoshibukawa.ifood.marketplace.dto;

import java.math.BigDecimal;

import io.vertx.mutiny.sqlclient.Row;

public class PratoDTO {       
    
    public String nome;

    public String descricao;    

    public BigDecimal preco;

    public Long id;

    public static PratoDTO from(Row row) {
        final PratoDTO dto = new PratoDTO();

        dto.id = row.getLong("id");
        dto.descricao = row.getString("descricao");
        dto.nome = row.getString("nome");
        dto.preco = row.getBigDecimal("preco");

        return dto;
    }
}
