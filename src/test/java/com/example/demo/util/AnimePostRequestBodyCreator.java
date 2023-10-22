package com.example.demo.util;

import com.example.demo.request.AnimePostRequestBody;

public class AnimePostRequestBodyCreator {
    public static AnimePostRequestBody createAnimePostRequestBody() {
        return AnimePostRequestBody.builder().name(AnimeCreator.createAnimeToBeSaved().getName()).build();
    }
}
