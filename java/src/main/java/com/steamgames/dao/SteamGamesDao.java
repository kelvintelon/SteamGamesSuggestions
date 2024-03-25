package com.steamgames.dao;

import com.steamgames.model.SteamGame;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SteamGamesDao {

    void uploadSteamGamesCsv(MultipartFile multipartFile);

    List<SteamGame> getAllSteamGames();
}
