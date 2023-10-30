package com.example.demo.integration;

import com.example.demo.domain.Anime;
import com.example.demo.domain.SysUser;
import com.example.demo.repository.AnimeRepository;
import com.example.demo.repository.SysUserRepository;
import com.example.demo.request.AnimePostRequestBody;
import com.example.demo.util.AnimeCreator;
import com.example.demo.util.AnimePostRequestBodyCreator;
import com.example.demo.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    private static final SysUser USER = SysUser.builder()
            .name("Rafael")
            .username("leafar")
            .password("{bcrypt}$2a$10$Bgk9kRz0CKvui7OjG8iUW.5HRmk2aMLTVwYEjk3NqBprFQRd03hs2")
            .authorities("ROLE_USER")
            .build();

    private static final SysUser ADMIN = SysUser.builder()
            .name("Admin")
            .username("admin")
            .password("{bcrypt}$2a$10$/cR/au3E/XECpFc4G.IXoeWYaN83V0akOsMQeM2gH9qvOVWzQr10G")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("leafar", "leafar");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("admin", "admin");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("GET - It returns a list of anime inside a page object if successful")
    void getReturnsListOfAnimeInsidePageObjectIfSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        sysUserRepository.save(USER);
        String expectedName = savedAnime.getName();
        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();
        Assertions.assertThat(animePage).isNotNull();
        List<Anime> animeList = animePage.toList();
        Assertions.assertThat(animeList).isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("GET - It returns a list of anime if successful")
    void getReturnsListOfAnimeIfSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        sysUserRepository.save(USER);
        String expectedName = savedAnime.getName();
        List<Anime> animeList = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();
        Assertions.assertThat(animeList).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("GET - It tries to find an anime by id and returns it if found")
    void getFindsAnimeByIdAndReturnsItIfFound() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        sysUserRepository.save(USER);
        Long expectedId = savedAnime.getId();
        Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);
        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("GET - It tries to find an anime by name and returns a list if it is found")
    void getFindsAnimeByNameAndReturnsListIfFound() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        sysUserRepository.save(USER);
        String expectedName = savedAnime.getName();
        String url = String.format("/animes/find?name=%s", expectedName);
        List<Anime> animeList = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody();
        Assertions.assertThat(animeList).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("GET - It tries to find an anime by name and returns an empty list if it is not found")
    void getFindsAnimeByNameAndReturnsEmptyListIfNotFound() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        sysUserRepository.save(USER);
        String expectedName = "invalid name";
        String url = String.format("/animes/find?name=%s", expectedName);
        List<Anime> animeList = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody();
        Assertions.assertThat(animeList).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("POST - It creates a new anime if everything goes successful")
    void postCreatesNewAnimeIfSuccessful() {
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
        sysUserRepository.save(USER);
        ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Anime.class);
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
        Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("PUT - It updates an existing anime if everything goes successful")
    void putUpdatesAnimeIfSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        sysUserRepository.save(USER);
        savedAnime.setName("whatever");
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes", HttpMethod.PUT,
                new HttpEntity<>(savedAnime), Void.class);
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("DELETE - It deletes an existing anime if everything goes successful")
    void deleteDeletesAnimeIfSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        sysUserRepository.save(ADMIN);
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}", HttpMethod.DELETE,
                null, Void.class, savedAnime.getId());
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("DELETE - It returns 403 if user is not an admin")
    void deleteReturns403IfUserIsNotAdmin() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        sysUserRepository.save(USER);
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}",
                HttpMethod.DELETE, null, Void.class, savedAnime.getId());
        Assertions.assertThat(animeResponseEntity).isNotNull();
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}