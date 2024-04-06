package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    // Получение списка всех режиссёров
    @GetMapping
    public ResponseEntity<Collection<Director>> getAllDirectors() {
        Collection<Director> directors = directorService.getAllDirectors();
        return ResponseEntity.ok(directors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable Long id) {
        Director director = directorService.getDirectorById(id);
        return ResponseEntity.ok(director);
    }

    @PostMapping
    public ResponseEntity<Director> addNewDirector(@RequestBody Director director) {
        if (director.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        directorService.addNewDirector(director);
        return new ResponseEntity<>(director, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Director> updateDirector(@RequestBody Director director) {
        if (director.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        directorService.updateDirector(director);
        return new ResponseEntity<>(director, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        directorService.deleteDirector(id);
        return ResponseEntity.ok().build();
    }
}