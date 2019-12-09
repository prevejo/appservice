package br.ucb.prevejo.transporte.trajeto;

import br.ucb.prevejo.transporte.areaentity.EnumAreaEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/trajeto")
public class TrajetoController {

    private final TrajetoService service;

    public TrajetoController(TrajetoService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{origemType}/{origemData}/{destinoType}/{destinoData}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Trajeto>> obterTrajetos(
            @PathVariable("origemType") EnumAreaEntity origem, @PathVariable("origemData") String origemData,
            @PathVariable("destinoType") EnumAreaEntity destino, @PathVariable("destinoData") String destinoData) {

        return new ResponseEntity<>(service.search(origem.buildArea(origemData), destino.buildArea(destinoData)), HttpStatus.OK);
    }

}
