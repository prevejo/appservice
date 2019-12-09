package br.ucb.prevejo.transporte.percurso;

import br.ucb.prevejo.shared.model.Feature;
import br.ucb.prevejo.shared.model.FeatureCollection;
import br.ucb.prevejo.transporte.parada.ParadaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/percurso")
public class PercursoController {

    private final PercursoService service;
    private final ParadaService paradaService;

    public PercursoController(PercursoService service, ParadaService paradaService) {
        this.service = service;
        this.paradaService = paradaService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/feature/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Feature> obterPercursoById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(service.obterFeature(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/collection/{percursoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FeatureCollection> obterLinhaCollectionByPercursoId(@PathVariable("percursoId") Integer percursoId) {
        return new ResponseEntity<>(service.obterLinhaCollectionByPercursoId(percursoId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/parada/{codParada}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<PercursoDTO>> obterPercursosByParada(@PathVariable("codParada") String codParada) {
        return paradaService.obterPorCodigo(codParada)
                .map(parada -> service.obterPercursos(parada))
                .map(percursos -> new ResponseEntity<>(percursos, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.OK));
    }

}
