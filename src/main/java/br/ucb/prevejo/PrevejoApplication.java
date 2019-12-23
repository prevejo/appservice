package br.ucb.prevejo;

import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.util.Geo;
import org.locationtech.jts.geom.Point;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class PrevejoApplication {
	//double CONST = 108453.78603423685;

	public static void main(String[] args) {
		SpringApplication.run(PrevejoApplication.class, args);

		/*Percurso perc = ContextProvider.getBean(PercursoService.class).obterPercursoFetchLinha("099.1", EnumSentido.CIRCULAR).get();
		Parada parada = ContextProvider.getBean(ParadaService.class).obterPorCodigo("4976").get();

		List<InstanteOperacao> instantes = ContextProvider.getBean(InstanteOperacaoService.class)
				.obterByPercurso(perc.toDTO()).stream()
				.collect(Collectors.groupingBy(i -> i.getVeiculo().getNumero()))
				.entrySet().stream().filter(e -> e.getKey().equals("440191"))
				.findFirst().map(io -> io.getValue()).get().stream()
				.filter(i -> i.getInstante().getData().compareTo(LocalDateTime.of(2019,10,28,13,0,0)) >= 0
					&& i.getInstante().getData().compareTo(LocalDateTime.of(2019,10,28,15,50,0)) <= 0)
				.collect(Collectors.toList());

		HistoricoOperacao hp = HistoricoOperacao.build(instantes);

		EstimativaPercurso ep = hp.calcularEstimativa(perc, parada, LOCATED_ENTITY);

		ep.getChegadas().forEach(chegada -> {
			System.out.println();
			System.out.println("--> Duração  : " + chegada.getDuracao() + " min");
			System.out.println("--> Distância: " + chegada.getDistancia() + " km");
			System.out.println("--> Trecho   : " + (chegada.getTrecho() != null));

			if (chegada.getTrecho() != null) {
				System.out.println(Geo.toGeoJson(chegada.getTrecho().getFeatures().stream().filter(t -> t.getProperties().get("position").equals("middle")).findFirst().get().getGeometry()));
			}l
		});*/
	}

	/*private static final List<LocatedEntity> LOCATED_ENTITY = Arrays.asList(new LocatedEntity() {
		@Override
		public Point getLocation() {
			return Geo.makePointXY(-48.02608966827392,-15.848015452982658);
		}
		@Override
		public List<Point> getRecordPath() {
			return Arrays.asList(
					Geo.makePointXY(-48.02993059158325,-15.852866277558576),
					Geo.makePointXY(-48.02892208099365,-15.851214946113021),
					Geo.makePointXY(-48.028385639190674,-15.849150762802713),
					Geo.makePointXY(-48.02608966827392,-15.848015452982658)
			);
		}
	});*/

}