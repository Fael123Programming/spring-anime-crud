package com.example.demo.util;

import com.example.demo.domain.Anime;
import com.example.demo.request.AnimePutRequestBody;

public class AnimePutRequestBodyCreator {
    public static AnimePutRequestBody createAnimePutRequestBody() {
        Anime anime = AnimeCreator.createValidUpdatedAnime();
        return AnimePutRequestBody.builder().name(anime.getName()).id(anime.getId()).build();
    }
}
