package com.example.demo.client;

import com.example.demo.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        // GET
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class, 6);
        log.info(entity);

        Anime object = new RestTemplate().getForObject("http://localhost:8080/animes/{id}", Anime.class, 6);
        log.info(object);

        Anime[] objects = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
        log.info(Arrays.toString(objects));

        ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        log.info(exchange.getBody());

        // POST
        Anime anime = Anime.builder().name("Kingdom").build();
        Anime animeSaved = new RestTemplate().postForObject("http://localhost:8080/animes", anime, Anime.class);
        log.info("Saved anime {}", animeSaved);

        Anime anime2 = Anime.builder().name("Samurai Champloo").build();
        ResponseEntity<Anime> animeSaved2 = new RestTemplate().exchange("http://localhost:8080/animes", HttpMethod.POST, new HttpEntity<>(anime2), Anime.class);
//        ResponseEntity<Anime> animeSaved2 = new RestTemplate().exchange("http://localhost:8080/animes", HttpMethod.POST, new HttpEntity<>(anime2, createJsonHeader()), Anime.class);
        log.info("Saved anime 2 {}", animeSaved2.getBody());

        // PUT
        Anime anime2Change = Anime.builder().id(9L).name("Samurai Champloo 2nd Season").build();
        ResponseEntity<Void> anime2ChangeResponse = new RestTemplate().exchange(
                "http://localhost:8080/animes",
                HttpMethod.PUT,
                new HttpEntity<>(anime2Change, createJsonHeader()),
                Void.class
        );
        log.info("PUT: {}", anime2ChangeResponse);

        // DELETE
        Long id = 6L;
        ResponseEntity<Void> animeDeleteResponse = new RestTemplate().exchange(
                "http://localhost:8080/animes/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                id
        );
        log.info("DELETED: {}", id);
    }

    private static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
