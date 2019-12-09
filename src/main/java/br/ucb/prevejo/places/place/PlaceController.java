package br.ucb.prevejo.places.place;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/places/detail")
public class PlaceController {

    private final PlaceService service;

    public PlaceController(PlaceService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{placeId}/{searchSession}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Place> detail(@PathVariable String placeId, @PathVariable String searchSession) {
        return new ResponseEntity<>(service.detail(placeId, searchSession), HttpStatus.OK);
    }

}