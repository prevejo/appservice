package br.ucb.prevejo.transporte.parada;

import br.ucb.prevejo.shared.model.FeatureCollection;
import br.ucb.prevejo.transporte.percurso.PercursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/parada")
public class ParadaController {

    private final ParadaService service;
    private final PercursoService percursoService;

    public ParadaController(ParadaService service, PercursoService percursoService) {
        this.service = service;
        this.percursoService = percursoService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/collection", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FeatureCollection> featureCollection() {
        return new ResponseEntity<FeatureCollection>(service.obterFeatureCollectionFromCache(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/collection/percurso/{percursoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FeatureCollection> featureCollection(@PathVariable("percursoId") Integer percursoId) {
        return percursoService.obterPercurso(percursoId)
                .map(p -> service.obterFeatureCollection(p))
                .map(collection -> new ResponseEntity<FeatureCollection>(collection, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.OK));
    }

}
