insert into transporte.tb_localizacao_veiculo (
    num_veiculo,
    num_linha,
    ds_sentido,
    ds_operadora,
    num_velocidade,
    ds_velocidade,
    num_direcao,
    dt_localizacao,
    geo
)values(?, ?, ?, ?, ?, ?, ?, ?, ?::geometry)