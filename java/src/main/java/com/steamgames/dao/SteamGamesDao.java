package com.steamgames.dao;

import org.springframework.web.multipart.MultipartFile;

public interface SteamGamesDao {

    void uploadSteamGamesCsv(MultipartFile multipartFile);
}
