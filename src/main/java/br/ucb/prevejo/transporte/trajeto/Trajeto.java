package br.ucb.prevejo.transporte.trajeto;

import br.ucb.prevejo.transporte.areaentity.AreaEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Trajeto implements Comparable<Trajeto> {

    private AreaEntity origem;
    private AreaEntity destino;
    private List<Trecho> trechos;

    @Override
    public int compareTo(Trajeto other) {
        int bySize = trechos.size() - other.trechos.size();

        if (bySize == 0) {
            double avgP1 = trechos.stream().mapToInt(trecho -> trecho.getPercursos().size()).average().orElse(0);
            double avgP2 = other.trechos.stream().mapToInt(trecho -> trecho.getPercursos().size()).average().orElse(0);

            if (avgP1 > avgP2) {
                return -1;
            } else if (avgP2 > avgP1) {
                return 1;
            }

            return 0;
        }

        return bySize;
    }
}
