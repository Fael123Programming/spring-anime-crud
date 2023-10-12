package com.example.demo.service;

import com.example.demo.domain.Anime;
import com.example.demo.exception.BadRequestException;
import com.example.demo.mapper.AnimeMapper;
import com.example.demo.repository.AnimeRepository;
import com.example.demo.request.AnimePostRequestBody;
import com.example.demo.request.AnimePutRequestBody;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository animeRepository;
    public List<Anime> listAll() {
        return animeRepository.findAll();
    }
    public List<Anime> findByName(String name) {
        return animeRepository.findByName(name);
    }

    public Anime findByIdOrThrowBadRequestException(long id) {
        return animeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(String.format("Anime %d not found", id)));
    }

//    @Transactional
    @Transactional(rollbackOn = Exception.class)
    public Anime save(AnimePostRequestBody animePostRequestBody) {
        return animeRepository.save(AnimeMapper.INSTANCE.toAnime(animePostRequestBody));
    }

    public void delete(long id) {
        animeRepository.delete(this.findByIdOrThrowBadRequestException(id));
    }

    public void replace(AnimePutRequestBody animePutRequestBody) {
        Anime savedAnime = this.findByIdOrThrowBadRequestException(animePutRequestBody.getId());
        Anime replacementAnime = AnimeMapper.INSTANCE.toAnime(animePutRequestBody);
        replacementAnime.setId(savedAnime.getId());
        animeRepository.save(replacementAnime);
    }
}
