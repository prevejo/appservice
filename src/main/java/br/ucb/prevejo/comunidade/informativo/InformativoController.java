package br.ucb.prevejo.comunidade.informativo;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/informativo")
public class InformativoController {

    private final InformativoService service;

    public InformativoController(InformativoService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InformativoDTO> obterInformativo(@PathVariable("id") Integer id) {
        return service.obterById(id)
                .map(info -> new ResponseEntity<>(info.toDTO(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/last", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<InformativoDTO>> obterUltimos() {
        return new ResponseEntity<>(service.obterFeed(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/last/{lastDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<InformativoDTO>> obterUltimos(@PathVariable("lastDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime lastDate) {
        return new ResponseEntity<>(service.obterFeed(lastDate), HttpStatus.OK);
    }

}
