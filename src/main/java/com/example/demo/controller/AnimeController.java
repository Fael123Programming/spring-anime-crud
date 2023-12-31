package com.example.demo.controller;

import com.example.demo.domain.Anime;
import com.example.demo.request.AnimePostRequestBody;
import com.example.demo.request.AnimePutRequestBody;
import com.example.demo.service.AnimeService;
import com.example.demo.util.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("animes")
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity
public class AnimeController {
    private final DateUtil dateUtil;
    private final AnimeService animeService;

    @GetMapping
    @Operation(
            summary = "List all animes paginated",
            description = "The default size is 20, use the parameter size to change the default value",
            tags = {"anime"}
    )
    public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable) {
//        log.info("GET list " + dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAll(pageable));
    }
    @GetMapping(path = "/all")
    public ResponseEntity<List<Anime>> listAll() {
//        log.info("GET list " + dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAllNonPageable());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById(@PathVariable long id) {
//        log.info("GET findById " + dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
    }

//    @GetMapping(path = "by-id/{id}")
//    public ResponseEntity<Anime> findByIdAuthenticationPrincipal(
//            @PathVariable long id,
//            @AuthenticationPrincipal UserDetails userDetails
//            ) {
////        log.info("GET findById " + dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
//        log.info(userDetails);
//        return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
//    }

    @GetMapping(path = "/find")
    public ResponseEntity<List<Anime>> findByName(@RequestParam String name) {
//        log.info("GET findByName " + dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.findByName(name));
    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody animePostRequestBody) {
//        log.info("POST save " + dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return new ResponseEntity<>(animeService.save(animePostRequestBody), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/admin/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Anime not found"),
    })
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        animeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody animePutRequestBody) {
        animeService.replace(animePutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
