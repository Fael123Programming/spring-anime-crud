package com.example.demo.service;

import com.example.demo.domain.Anime;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.AnimeRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {
    @InjectMocks  // Use this if you want to test the class itself.
    private AnimeService animeService;
    @Mock  // Use this if you want to test every member of the class.
    private AnimeRepository animeRepositoryMock;

    @BeforeEach
    void setUp() {
        Anime validAnime = AnimeCreator.createValidAnime();
        List<Anime> animeList = List.of(validAnime);
        PageImpl<Anime> animePage = new PageImpl<>(animeList);
        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(animePage);
        BDDMockito.when(animeRepositoryMock.findAll()).thenReturn(animeList);
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(validAnime));
        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString())).thenReturn(animeList);
        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class))).thenReturn(validAnime);
        BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("GET - It returns a list of anime inside a page object if successful")
    void getReturnsListOfAnimeInsidePageObjectIfSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));
        Assertions.assertThat(animePage).isNotNull();
        List<Anime> animeList = animePage.toList();
        Assertions.assertThat(animeList).isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("GET - It returns a list of all anime if successful")
    void getReturnsListOfAllAnimeIfSuccessful() {
        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeService.listAllNonPageable();
        Assertions.assertThat(animeList).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("GET - It tries to find an anime by id and returns it if successful")
    void getFindsAnimeByIdAndReturnsItIfSuccessful() {
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeService.findByIdOrThrowBadRequestException(1L);
        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }
    @Test
    @DisplayName("GET - It tries to find an anime by id and returns it if successful. Throws BadRequestException otherwise")
    void getFindsAnimeByIdAndReturnsItIfSuccessfulThrowsBadRequestExceptionOtherwise() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> animeService.findByIdOrThrowBadRequestException(1L));
    }
    @Test
    @DisplayName("GET - It tries to find an anime by name and returns a list if successful")
    void getFindsAnimeByNameAndReturnsListIfSuccessful() {
        Anime expectedAnime = AnimeCreator.createValidAnime();
        List<Anime> animeList = animeService.findByName("whatever");
        Assertions.assertThat(animeList)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(animeList.get(0)).isEqualTo(expectedAnime);
    }
    @Test
    @DisplayName("GET - It tries to find an anime by name and returns an empty list if unsuccessful")
    void getFindsAnimeByNameAndReturnsEmptyListIfUnsuccessful() {
        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());
        List<Anime> animeList = animeService.findByName("whatever");
        Assertions.assertThat(animeList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("POST - It saves the anime and returns it if successful")
    void postSavesAnimeAndReturnsItIfSuccessful() {
        Assertions.assertThat(animeService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody()))
                .isNotNull()
                .isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("PUT - It updates an anime if it exists")
    void putUpdatesAnimeIfExists() {
        Assertions.assertThatCode(() -> animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                .doesNotThrowAnyException();
    }
    @Test
    @DisplayName("DELETE - It deletes an anime if it exists")
    void delDeletesAnimeIfExists() {
        Assertions.assertThatCode(() -> animeService.delete(1L))
                .doesNotThrowAnyException();
    }
}