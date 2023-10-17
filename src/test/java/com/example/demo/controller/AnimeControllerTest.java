package com.example.demo.controller;

import com.example.demo.domain.Anime;
import com.example.demo.request.AnimePostRequestBody;
import com.example.demo.request.AnimePutRequestBody;
import com.example.demo.service.AnimeService;
import com.example.demo.util.AnimeCreator;
import com.example.demo.util.AnimePostRequestBodyCreator;
import com.example.demo.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {
    @InjectMocks  // Use this if you want to test the class itself.
    private AnimeController animeController;
    @Mock  // Use this if you want to test every member of the class.
    private AnimeService animeServiceMock;

    @BeforeEach
    void setUp() {
        Anime validAnime = AnimeCreator.createValidAnime();
        List<Anime> animeList = List.of(validAnime);
        PageImpl<Anime> animePage = new PageImpl<>(animeList);
        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any())).thenReturn(animePage);
        BDDMockito.when(animeServiceMock.listAllNonPageable()).thenReturn(animeList);
        BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong())).thenReturn(validAnime);
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString())).thenReturn(animeList);
        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class))).thenReturn(validAnime);
        BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));
        BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("GET - It returns a list of anime inside a page object if successful")
    void getReturnsListOfAnimeInsidePageObjectIfSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeController.list(null).getBody();
        Assertions.assertThat(animePage).isNotNull();
        List<Anime> animeList = animePage.toList();
        Assertions.assertThat(animeList).isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("GET - It returns a list of all anime if successful")
    void getReturnsListOfAllAnimeIfSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeController.listAll().getBody();
        Assertions.assertThat(animeList).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("GET - It tries to find an anime and returns it if successful")
    void getFindsAnimeByIdAndReturnsItIfSuccessful() {
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeController.findById(1L).getBody();
        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }
    @Test
    @DisplayName("GET - It tries to find an anime by name and returns a list if successful")
    void getFindsAnimeByNameAndReturnsListIfSuccessful() {
        Anime expectedAnime = AnimeCreator.createValidAnime();
        List<Anime> animeList = animeController.findByName("whatever").getBody();
        Assertions.assertThat(animeList)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animeList.get(0)).isEqualTo(expectedAnime);
    }
    @Test
    @DisplayName("GET - It tries to find an anime by name and returns an empty list if unsuccessful")
    void getFindsAnimeByNameAndReturnsEmptyListIfUnsuccessful() {
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());
        List<Anime> animeList = animeController.findByName("whatever").getBody();
        Assertions.assertThat(animeList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("POST - It saves the anime and returns it if successful")
    void postSavesAnimeAndReturnsItIfSuccessful() {
        Assertions.assertThat(animeServiceMock.save(AnimePostRequestBodyCreator.createAnimePostRequestBody()))
                .isNotNull()
                .isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("PUT - It updates an anime if it exists")
    void putUpdatesAnimeIfExists() {
        Assertions.assertThatCode(() -> animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                        .doesNotThrowAnyException();
        ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
    @Test
    @DisplayName("DELETE - It deletes an anime if it exists")
    void delDeletesAnimeIfExists() {
        Assertions.assertThatCode(() -> animeController.delete(1L))
                        .doesNotThrowAnyException();
        ResponseEntity<Void> entity = animeController.delete(1L);
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}