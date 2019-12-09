package br.ucb.prevejo.transporte.parada;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ParadaDTOImpl implements ParadaDTO {

    @EqualsAndHashCode.Include
    private Integer id;
    private String cod;
    private Point geo;
}
