package com.example.demo.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnimePostRequestBody {
    @NotEmpty(message = "The anime name cannot be empty")
//    @NotNull(message = "The anime name cannot be null")
    private String name;
}
