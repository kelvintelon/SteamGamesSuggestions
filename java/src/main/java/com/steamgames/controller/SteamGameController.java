package com.steamgames.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.steamgames.dao.SteamGamesDao;
import com.steamgames.model.SteamGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class SteamGameController {

    @Autowired
    private SteamGamesDao steamGamesDao;

    @RequestMapping(value = "uploadSteamGames", method = RequestMethod.POST)
    public void uploadSteamGames(@RequestParam("file") MultipartFile multipartFile) {
        System.out.println("Started To Upload Steam Games Into Database");
        steamGamesDao.uploadSteamGamesCsv(multipartFile);
    }

    @GetMapping("/findSteamGame/{name}")
    public List<SteamGame> getListOfGamesByName(@PathVariable String name) {
        return steamGamesDao.getListOfGamesByName(name);
    }

    @GetMapping("/getSuggestions/{game_id}")
    public List<SteamGame> getListOfInitialRecommendations(@PathVariable int game_id) {
        return steamGamesDao.recommendSteamGamesByGameID(game_id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/applyAttributes", method = RequestMethod.POST)
    public ResponseEntity<SuggestionResponse> registerClient(@RequestBody AttributesObject applyFilteredObject) {
        String[] newAttributes = applyFilteredObject.getFilteredStrings();
        int game_id = applyFilteredObject.getGame_id();
        List<SteamGame> newGameSuggestions = steamGamesDao.applyAttributesAndSuggest(game_id, newAttributes);
        List<SteamGame> optimalGameSuggestions = steamGamesDao.recommendSteamGamesByGameID(game_id);
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(new SuggestionResponse(newGameSuggestions, optimalGameSuggestions), httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/test")
    public List<SteamGame> test() {
        return steamGamesDao.recommendSteamGamesTest();
    }

    static class SuggestionResponse {

        private List<SteamGame> newGameSuggestions = new ArrayList<>();

        private List<SteamGame> optimalGameSuggestions = new ArrayList<>();

        public SuggestionResponse(List<SteamGame> newGameSuggestions, List<SteamGame> optimalGameSuggestions) {
            this.newGameSuggestions = newGameSuggestions;
            this.optimalGameSuggestions = optimalGameSuggestions;
        }

        @JsonProperty("optimalGameSuggestions")
        public List<SteamGame> getOptimalGameSuggestions() {
            return optimalGameSuggestions;
        }

        public void setOptimalGameSuggestions(List<SteamGame> optimalGameSuggestions) {
            this.optimalGameSuggestions = optimalGameSuggestions;
        }

        @JsonProperty("newGameSuggestions")
        public List<SteamGame> getNewGameSuggestions() {
            return newGameSuggestions;
        }

        public void setNewGameSuggestions(List<SteamGame> newGameSuggestions) {
            this.newGameSuggestions = newGameSuggestions;
        }
    }

    public static class AttributesObject {

        private String[] filteredStrings;

        private int game_id;

        public AttributesObject() {
        }

        public String[] getFilteredStrings() {
            return filteredStrings;
        }

        public void setFilteredStrings(String[] filteredStrings) {
            this.filteredStrings = filteredStrings;
        }

        public int getGame_id() {
            return game_id;
        }

        public void setGame_id(int game_id) {
            this.game_id = game_id;
        }
    }
}
