package com.steamgames.controller;

import com.steamgames.dao.SteamGamesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class SteamGameController {

    @Autowired
    private SteamGamesDao steamGamesDao;

    @RequestMapping(value = "uploadSteamGames", method = RequestMethod.POST)
    public void uploadSteamGames(@RequestParam("file")MultipartFile multipartFile) {
        steamGamesDao.uploadSteamGamesCsv(multipartFile);
    }

    @GetMapping("/test")
    public String test () {
        return "worked";
    }

}
