package org.example.springbootrecap.controller;

import org.example.springdata.model.AsterixCharacter;
import org.example.springdata.model.dto.CharacterInputDTO;
import org.example.springdata.service.AsterixService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asterix")
public class MainController {

    private final MainService mainService;

    public AsterixController(MainService asterixService) {
        this.asterixService = asterixService;
    }

    @PostMapping("init")
    public List<AsterixCharacter> init() {
        return asterixService.init();
    }

    @GetMapping
    public List<AsterixCharacter> getAllCharacters() {
        return asterixService.getAllCharacters();
    }

    @GetMapping("/job")
    public List<AsterixCharacter> getAllCharactersByJob(@RequestParam String job) {
        return asterixService.getAllCharactersByJob(job);
    }

    @GetMapping("/{id}")
    public AsterixCharacter getCharacterById(@PathVariable String id) {
        return asterixService.getCharacterById(id);
    }

    @PostMapping
    public AsterixCharacter addCharacter(@RequestBody CharacterInputDTO character) {
        return asterixService.addCharacter(character);
    }

    @PutMapping("/{id}")
    public AsterixCharacter updateCharacter(@PathVariable String id, @RequestBody AsterixCharacter character) {
        return asterixService.updateCharacter(id, character);
    }

    @DeleteMapping("/{id}")
    public void deleteCharacter(@PathVariable String id) {
        asterixService.deleteCharacter(id);
    }
}