package com.example.demo.repository;

import com.example.demo.domain.Anime;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import com.example.demo.util.AnimeCreator;

@DataJpaTest
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {
    @Autowired
    private AnimeRepository animeRepository;
    @Test
    @DisplayName("SAVE - It persists anime if successful")
    public void savePersistsAnimeIfSuccessful() {
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);
        Assertions.assertThat(savedAnime).isNotNull();
        Assertions.assertThat(savedAnime.getId()).isNotNull();
        Assertions.assertThat(savedAnime.getName()).isEqualTo(anime.getName());
    }

    @Test
    @DisplayName("SAVE - It updates anime if successful")
    public void saveUpdatesAnimeIfSuccessful() {
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);
        savedAnime.setName("Overlord");
        Anime updatedAnime = this.animeRepository.save(savedAnime);
        Assertions.assertThat(updatedAnime).isNotNull();
        Assertions.assertThat(updatedAnime.getId()).isNotNull();
        Assertions.assertThat(updatedAnime.getName()).isEqualTo(savedAnime.getName());
    }
    @Test
    @DisplayName("DELETE - It removes anime if successful")
    public void deleteRemovesAnimeIfSuccessful() {
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);
        this.animeRepository.delete(savedAnime);
        Optional<Anime> optionalAnime = this.animeRepository.findById(savedAnime.getId());
        Assertions.assertThat(optionalAnime).isEmpty();
    }
    @Test
    @DisplayName("GET - It finds anime by name if successful")
    public void getFindsAnimeByNameIfSuccessful() {
        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);
        String name = savedAnime.getName();
        List<Anime> foundAnime = this.animeRepository.findByName(name);
        Assertions.assertThat(foundAnime)
                .isNotEmpty()
                .contains(savedAnime);
    }
    @Test
    @DisplayName("GET - It returns an empty list if anime is not found")
    public void getReturnsEmptyListIfAnimeIsNotFound() {
        List<Anime> foundAnime = this.animeRepository.findByName("nonexistent anime");
        Assertions.assertThat(foundAnime).isEmpty();
    }

    @Test
    @DisplayName("SAVE - It throws ConstraintViolationException if name is empty")
    public void saveThrowsConstraintViolationExceptionIfNameIsEmpty() {
        Anime anime = new Anime();
//        Assertions.assertThatThrownBy(() -> this.animeRepository.save(anime))
//                .isInstanceOf(ConstraintViolationException.class);
        Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> this.animeRepository.save(anime))
                .withMessageContaining("The anime name cannot be empty");
    }

    private Anime createAnimeToBeSaved() {
        return Anime.builder().name("Hajime no Ippo").build();
    }
}