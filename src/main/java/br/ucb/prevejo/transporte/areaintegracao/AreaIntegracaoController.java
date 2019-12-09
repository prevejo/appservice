package br.ucb.prevejo.transporte.areaintegracao;

import br.ucb.prevejo.shared.model.FeatureCollection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/areaintegracao")
public class AreaIntegracaoController {

    private final AreaIntegracaoService service;

    public AreaIntegracaoController(AreaIntegracaoService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/areas/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AreaIntegracaoDTO>> obterAreasById(@PathVariable("ids") String idsStr) {
        Collection<Integer> ids = Arrays.asList(idsStr.split("_")).stream().map(Integer::parseInt).collect(Collectors.toList());

        return new ResponseEntity<>(service.obterAreasById(ids), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/collection/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FeatureCollection> obterFeatureCollection(@PathVariable("ids") String idsStr) {
        Collection<Integer> ids = Arrays.asList(idsStr.split("_")).stream().map(Integer::parseInt).collect(Collectors.toList());

        return new ResponseEntity<>(service.obterCollectionById(ids), HttpStatus.OK);
    }

}
