SELECT num_veiculo as numero,
    num_linha as linha,
    ds_operadora as operadora,
    dt_localizacao as data,
    ds_sentido as sentido,
    num_direcao as direcao,
    num_velocidade  as velocidade,
    ds_velocidade as unit_velocidade,
    ST_AsBinary(geo) as localizacao
FROM transporte.tb_localizacao_veiculo where num_linha = ? and
    extract(isodow from dt_localizacao) in (:diasSemana)