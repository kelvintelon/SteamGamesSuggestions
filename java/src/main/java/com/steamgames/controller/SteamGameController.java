package com.steamgames.controller;

import com.steamgames.dao.SteamGamesDao;
import com.steamgames.model.SteamGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
public class SteamGameController {

    @Autowired
    private SteamGamesDao steamGamesDao;

    @RequestMapping(value = "uploadSteamGames", method = RequestMethod.POST)
    public void uploadSteamGames(@RequestParam("file")MultipartFile multipartFile) {
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

    @GetMapping("/test")
    public List<SteamGame> test () {
        return steamGamesDao.recommendSteamGamesTest();
    }
}
