package br.ucb.prevejo.previsao.instanteoperacao;

import br.ucb.prevejo.core.resources.SqlResources;
import br.ucb.prevejo.previsao.operacao.EnumOperadora;
import br.ucb.prevejo.shared.model.Velocidade;
import br.ucb.prevejo.shared.util.DateAndTime;
import br.ucb.prevejo.shared.util.Geo;
import br.ucb.prevejo.transporte.percurso.EnumSentido;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ByteOrderValues;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InstanteOperacaoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final WKBReader GEOMETRY_READER = new WKBReader(new GeometryFactory(new PrecisionModel((int)Math.pow(10, 5)), 4326));
    private static final WKBWriter GEOMETRY_WRITER = new WKBWriter(2, ByteOrderValues.LITTLE_ENDIAN, true);


    public List<InstanteOperacao> findAllByLinha(String linha, List<Integer> diasSemana) {
        String sql = SqlResources.find(SqlResources.SELECT_INSTANTE_OPERACAO_BY_LINHA)
                .replace(":diasSemana", diasSemana.stream().map(ds -> "?").collect(Collectors.joining(",")));

        return jdbcTemplate.query(sql, (ps) -> {
            ps.setString(1, linha);

            int index = 2;
            for (Integer dia : diasSemana) {
                ps.setInt(index++, dia);
            }
        }, (rs, i) -> parse(rs));
    }

    public List<InstanteOperacao> findAllByLinhaInRange(String linha, LocalTime startTime, LocalTime endTime) {
        return jdbcTemplate.query(
                SqlResources.find(SqlResources.SELECT_INSTANTE_OPERACAO_BY_LINHA_IN_RANGE),
                new Object[]{ linha, DateAndTime.toString(startTime, "HH:mm:ss"), DateAndTime.toString(endTime, "HH:mm:ss") },
                (rs, i) -> parse(rs)
        );
    }

    public List<InstanteOperacao> findAllByLinhaAndSentido(String linha, EnumSentido sentido, List<Integer> diasSemana) {
        String sql = SqlResources.find(SqlResources.SELECT_INSTANTE_OPERACAO_BY_LINHA_AND_SENTIDO)
                .replace(":diasSemana", diasSemana.stream().map(ds -> "?").collect(Collectors.joining(",")));
        return jdbcTemplate.query(sql, (ps) -> {
            ps.setString(1, linha);
            ps.setString(2, sentido.toString());

            int index = 3;
            for (Integer dia : diasSemana) {
                ps.setInt(index++, dia);
            }
        }, (rs, i) -> parse(rs));
    }

    public List<InstanteOperacao> findAllByLinhaAndSentidoInRange(String linha, EnumSentido sentido, LocalTime startTime, LocalTime endTime) {
        return jdbcTemplate.query(
                SqlResources.find(SqlResources.SELECT_INSTANTE_OPERACAO_BY_LINHA_AND_SENTIDO_IN_RANGE),
                new Object[]{ linha, sentido.toString(), DateAndTime.toString(startTime, "HH:mm:ss"), DateAndTime.toString(endTime, "HH:mm:ss") },
                (rs, i) -> parse(rs)
        );
    }

    public void batchInsert(List<InstanteOperacao> instantes) {
        jdbcTemplate.batchUpdate(SqlResources.find(SqlResources.INSERT_INSTANTE_OPERACAO), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                InstanteOperacao instante = instantes.get(i);

                ps.setString(1, instante.getVeiculo().getNumero());
                ps.setString(2, instante.getLinha());

                if (instante.getSentido() == null) {
                    ps.setNull(3, Types.NULL);
                } else {
                    ps.setString(3, instante.getSentido().toString());
                }

                ps.setString(4, instante.getVeiculo().getOperadora().toString());

                if (instante.getInstante().getVelocidade() != null) {
                    ps.setBigDecimal(5, instante.getInstante().getVelocidade().getValor());
                    ps.setString(6, instante.getInstante().getVelocidade().getUnidade().toStringAbr());
                } else {
                    ps.setNull(5, Types.NULL);
                    ps.setNull(6, Types.NULL);
                }

                ps.setBigDecimal(7, instante.getInstante().getDirecao());
                ps.setTimestamp(8, Timestamp.valueOf(instante.getInstante().getData()));
                ps.setBytes(9, GEOMETRY_WRITER.write(instante.getInstante().getLocalizacao()));
            }

            @Override
            public int getBatchSize() {
                return instantes.size();
            }
        });
    }


    private InstanteOperacao parse(ResultSet rs) throws SQLException {
        try {
            String numero = rs.getString("numero");
            String linha = rs.getString("linha");
            String sentido = rs.getString("sentido");
            String operadora = rs.getString("operadora");
            Timestamp data = rs.getTimestamp("data");
            byte[] localizacao = rs.getBytes("localizacao");

            BigDecimal velocidade = rs.getBigDecimal("velocidade");
            String unitVelocidade = rs.getString("unit_velocidade");
            BigDecimal direcao = rs.getBigDecimal("direcao");
            Point point = null;
            try {
                point = (Point) GEOMETRY_READER.read(localizacao);
            } catch(ParseException e) {
                if (!(e.getMessage() != null && e.getMessage().startsWith("Unknown WKB type"))) {
                    throw e;
                }
                System.out.println("============================");
                System.out.println("Veiculo: " + numero);
                System.out.println("Linha  : " + linha);
                System.out.println("Sentido: " + sentido);
                System.out.println("Time   : " + data);
                System.out.println("============================");
                e.printStackTrace();
                point = Geo.makePoint(-15, -45);
            }

            return new InstanteOperacao(
                    new Veiculo(
                            numero,
                            EnumOperadora.valueOf(operadora)
                    ),
                    linha,
                    sentido != null ? EnumSentido.valueOf(sentido) : null,
                    new Instante(
                        data.toLocalDateTime(),
                            point, direcao, (velocidade != null ? Velocidade.build(unitVelocidade, velocidade) : null)
                    )
            );
        } catch (ParseException e) {
            throw new SQLException(e);
        }
    }

}
