package br.ucb.prevejo.places.search;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/places/search")
public class PlaceSearchController {

    private final PlaceSearchService service;

    public PlaceSearchController(PlaceSearchService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{value}/{searchSession}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<PlaceSearchResult>> search(@PathVariable String value, @PathVariable String searchSession) {
        return new ResponseEntity<>(service.search(value, searchSession), HttpStatus.OK);
    }

}
