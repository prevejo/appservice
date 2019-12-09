package br.ucb.prevejo.transporte.percursoparada;

import br.ucb.prevejo.transporte.linha.Linha;
import br.ucb.prevejo.transporte.parada.ParadaDTO;
import br.ucb.prevejo.transporte.parada.ParadaDTOImpl;
import br.ucb.prevejo.transporte.percurso.EnumSentido;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import br.ucb.prevejo.transporte.percurso.PercursoDTOImpl;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PercursoParadaRepositoryImpl implements PercursoParadaRepositoryCustom {

    @Autowired
    private EntityManager manager;

    @Override
    public List<PercursoParadaDTO> findDto() {
        Query query = manager.createQuery("SELECT pp.percurso.id, pp.percurso.sentido, pp.percurso.linha, pp.percurso.origem, pp.percurso.destino, " +
                "pp.parada.id, pp.parada.cod, pp.parada.geo, " +
                "pp.sequencial " +
                "FROM PercursoParada pp");

        List<Object[]> rows = query.getResultList();

        return rows.stream().map(row -> parse(row)).collect(Collectors.toList());
    }

    private PercursoParadaDTO parse(Object[] values) {
        return new PercursoParadaDTOImpl(
                parsePercurso(Arrays.copyOfRange(values, 0, 5)),
                parseParada(Arrays.copyOfRange(values, 5, 8)),
                (Integer) values[8]
        );
    }

    private PercursoDTO parsePercurso(Object[] values) {
        return new PercursoDTOImpl(
                (Integer) values[0],
                (EnumSentido) values[1],
                (Linha) values[2],
                (String) values[3],
                (String) values[4]
        );
    }

    private ParadaDTO parseParada(Object[] values) {
        return new ParadaDTOImpl(
                (Integer) values[0],
                (String) values[1],
                (Point) values[2]
        );
    }

}
