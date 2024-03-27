package com.steamgames.dao;

import com.steamgames.model.SteamGame;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SteamGamesDao {

    void uploadSteamGamesCsv(MultipartFile multipartFile);

    List<SteamGame> getAllSteamGames();

    List<SteamGame> recommendSteamGamesTest();

    List<SteamGame> recommendSteamGamesByGameID(int game_id);

    List<SteamGame> getListOfGamesByName(String name);

    SteamGame getGameByName(String name);

    SteamGame getGameByGameID(int game_id);

    List<SteamGame> applyAttributesAndSuggest(int game_id, String[] filters);
}
