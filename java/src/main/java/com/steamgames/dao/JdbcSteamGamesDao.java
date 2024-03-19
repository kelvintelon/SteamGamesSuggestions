package com.steamgames.dao;

import com.steamgames.model.CustomException;
import com.steamgames.model.SteamGame;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

@Component
public class JdbcSteamGamesDao implements SteamGamesDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcSteamGamesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void uploadSteamGamesCsv(MultipartFile multipartFile) {

        int count = 0;

        List<String> listOfStringsFromBufferedReader = new ArrayList<>();

        Set<SteamGame> setOfSteamGamesFromFile = new HashSet<>();

        HashMap<String,Integer> mapColumns = new HashMap<>();

        try (BufferedReader fileReader = new BufferedReader(new
                InputStreamReader(multipartFile.getInputStream(), "UTF-8"))) {

            String line;
            while ((line = fileReader.readLine()) != null) {

                if (count > 0) {

                    listOfStringsFromBufferedReader.add(line);

                } else {
                    String[] firstLine = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    mapColumns = populateColumnsForMap(firstLine);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        readLinesFromListAndPopulateSet(listOfStringsFromBufferedReader,setOfSteamGamesFromFile, mapColumns);

        //Batch create;
        if (!setOfSteamGamesFromFile.isEmpty()) {
            batchCreateSteamGames(setOfSteamGamesFromFile);
        }

    }

    public void batchCreateSteamGames(final Collection<SteamGame> steamGames) {

        try {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO steam_game (game_id, name, release_date, required_age, " +
                            "price, about, image_url, is_for_windows, is_for_mac, is_for_linux, categories, genres, tags) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    steamGames,
                    100,
                    (PreparedStatement ps, SteamGame steamGame) -> {
                        ps.setInt(1, steamGame.getGame_id());
                        ps.setString(2, steamGame.getName());
                        ps.setString(3, steamGame.getRelease_date());
                        ps.setInt(4, steamGame.getRequired_age());
                        ps.setBigDecimal(5, steamGame.getPrice());
                        ps.setString(6, steamGame.getAbout());
                        ps.setString(7, steamGame.getImage_url());
                        ps.setBoolean(8, steamGame.getIs_for_windows());
                        ps.setBoolean(9, steamGame.getIs_for_mac());
                        ps.setBoolean(10, steamGame.getIs_for_linux());
                        ps.setArray(11, ps.getConnection().createArrayOf("VARCHAR", steamGame.getCategories()));
                        ps.setArray(12, ps.getConnection().createArrayOf("VARCHAR", steamGame.getGenres()));
                        ps.setArray(13, ps.getConnection().createArrayOf("VARCHAR", steamGame.getTags()));
                    });
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Error batch creating steam games");
        }

        System.out.println("Batch method finalized");
    }

    private void readLinesFromListAndPopulateSet(List<String> listOfStringsFromBufferedReader, Set<SteamGame> setOfSteamGamesToPopulate, HashMap<String, Integer> columnMap) {

        for (int i = 0; i < listOfStringsFromBufferedReader.size(); i++) {
            SteamGame steamGame = new SteamGame();

            String thisLine = String.valueOf(listOfStringsFromBufferedReader.get(i));
            String[] splitLine = thisLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            int game_id = Integer.parseInt(splitLine[columnMap.get("AppID")]);
            String name = splitLine[columnMap.get("Name")].trim();;
            String release_date = splitLine[columnMap.get("Release date")].trim();;
            int required_age = 0;
            try {
                required_age = Integer.parseInt(splitLine[columnMap.get("Required age")]);
            } catch (NumberFormatException e) {
                System.out.println("Required Age Error at Game ID: " + game_id);
            }
            BigDecimal price = null;
            try {
                price = new BigDecimal(splitLine[columnMap.get("Price")]);
            } catch (Exception e) {
                System.out.println("Price Error at Game ID: " + game_id);
            }
            String about = splitLine[columnMap.get("About the game")].trim();;
            String image_url = splitLine[columnMap.get("Header image")].trim();;
            boolean is_for_windows = Boolean.parseBoolean(splitLine[columnMap.get("Windows")]);
            boolean is_for_mac = Boolean.parseBoolean(splitLine[columnMap.get("Mac")]);
            boolean is_for_linux = Boolean.parseBoolean(splitLine[columnMap.get("Linux")]);
            String[] categories = splitLine[columnMap.get("Categories")].split(",");
            String[] genres = splitLine[columnMap.get("Genres")].split(",");
            String[] tags = splitLine[columnMap.get("Tags")].split(",");

            steamGame.setGame_id(game_id);
            steamGame.setName(name);
            steamGame.setRelease_date(release_date);
            steamGame.setRequired_age(required_age);
            steamGame.setPrice(price);
            steamGame.setAbout(about);
            steamGame.setImage_url(image_url);
            steamGame.setIs_for_windows(is_for_windows);
            steamGame.setIs_for_mac(is_for_mac);
            steamGame.setIs_for_linux(is_for_linux);
            steamGame.setCategories(categories);
            steamGame.setGenres(genres);
            steamGame.setTags(tags);

            setOfSteamGamesToPopulate.add(steamGame);

        }
    }

    public static HashMap<String, Integer> populateColumnsForMap(String[] array) {
        HashMap<String, Integer> columnMap = new HashMap<>();

        for (int i = 0; i < array.length; i++) {
            String currentString = array[i];
            if (currentString.contains("AppID")) {
                columnMap.put("AppID", i);
            } else if (currentString.contains("Name")) {
                columnMap.put("Name", i);
            } else if (currentString.contains("Release date")) {
                columnMap.put("Release date", i);
            } else if (currentString.contains("Required age")) {
                columnMap.put("Required age", i);
            } else if (currentString.contains("About the game")) {
                columnMap.put("About the game", i);
            } else if (currentString.contains("Header image")) {
                columnMap.put("Header image", i);
            } else if (currentString.contains("Windows")) {
                columnMap.put("Windows", i);
            } else if (currentString.contains("Mac")) {
                columnMap.put("Mac", i);
            } else if (currentString.contains("Linux")) {
                columnMap.put("Linux", i);
            } else if (currentString.contains("Categories")) {
                columnMap.put("Categories", i);
            } else if (currentString.contains("Genres")) {
                columnMap.put("Genres", i);
            } else if (currentString.contains("Tags")) {
                columnMap.put("Tags", i);
            } else if (currentString.contains("Price")) {
                columnMap.put("Price", i);
            }

        }

        return columnMap;
    }
}
