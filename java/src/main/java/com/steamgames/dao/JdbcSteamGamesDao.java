package com.steamgames.dao;

import com.steamgames.model.CustomException;
import com.steamgames.model.SteamGame;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.apache.commons.text.similarity.SimilarityScore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
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

    @Override
    public List<SteamGame> getAllSteamGames() {
        List<SteamGame> listOfGamesToReturn = new ArrayList<>();
        String sql = "SELECT * FROM steam_game";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            try {
                SteamGame steamGame = mapRowToSteamGame(result);
                listOfGamesToReturn.add(steamGame);
            } catch (SQLException e) {
                throw new CustomException("Error retrieving all steam games");
            }

        }
        return listOfGamesToReturn;
    }

    @Override
    public SteamGame getGameByName(String name) {
        String sql = "SELECT * " +
                "FROM steam_game " +
                "WHERE lower(regexp_replace(name, '[^a-zA-Z0-9]+', '', 'g')) LIKE ALL (" +
                "    SELECT '%' || lower(regexp_replace(unnest(string_to_array(?, ' ')), '[^a-zA-Z0-9]+', '', 'g')) || '%' " +
                ");";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, name);
        List<SteamGame> listOfGamesToReturn = new ArrayList<>();
        while (result.next()) {
            try {
                SteamGame steamGame = mapRowToSteamGame(result);
                listOfGamesToReturn.add(steamGame);
            } catch (SQLException e) {
                throw new CustomException("Error retrieving steam game");
            }
        }
        return listOfGamesToReturn.isEmpty() ? null : listOfGamesToReturn.get(0);
    }

    @Override
    public List<SteamGame> recommendSteamGames() {
        List<Pair<SteamGame, Double>> similarityScores = new ArrayList<>();

        List<SteamGame> gameList = getAllSteamGames();

        SteamGame targetGame = getGameByName("Rainbow Six Siege");

        for (SteamGame game : gameList) {
            if (!targetGame.getRelease_date().equalsIgnoreCase(game.getRelease_date())) {
                double similarity = calculateCosineSimilarity(targetGame, game);
                game.setSimilarity_score(similarity);
                similarityScores.add(Pair.of(game, similarity));
            }
        }

        similarityScores.sort((p1, p2) -> Double.compare(p2.getRight(), p1.getRight())); // Sort by similarity

        List<SteamGame> recommendedGames = new ArrayList<>();
        for (Pair<SteamGame, Double> pair : similarityScores.subList(1, Math.min(21, similarityScores.size()))) {
            SteamGame recommendedGame = pair.getLeft();
            recommendedGames.add(recommendedGame);
        }

        return recommendedGames;
    }

    @Override
    public List<SteamGame> getListOfGamesByName(String name) {
        String sql = "SELECT * " +
                "FROM steam_game " +
                "WHERE lower(regexp_replace(name, '[^a-zA-Z0-9]+', '', 'g')) LIKE ALL (" +
                "    SELECT '%' || lower(regexp_replace(unnest(string_to_array(?, ' ')), '[^a-zA-Z0-9]+', '', 'g')) || '%' " +
                ");";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, name);
        List<SteamGame> listOfGamesToReturn = new ArrayList<>();
        while (result.next()) {
            try {
                SteamGame steamGame = mapRowToSteamGame(result);
                listOfGamesToReturn.add(steamGame);
            } catch (SQLException e) {
                throw new CustomException("Error retrieving steam game");
            }
        }
        if (listOfGamesToReturn.isEmpty()) {
            throw new CustomException("Game not found, adjust search");
        }
        return listOfGamesToReturn;
    }

    public double calculateCosineSimilarity(SteamGame game1, SteamGame game2) {
        // Combine all relevant attributes into a single map for similarity calculation
        Map<CharSequence, Integer> attributes1 = new HashMap<>();

        addAttributesToMap(attributes1, new String[]{game1.getName()});

        addAttributesToMap(attributes1, new String[]{game1.getAbout()});

        // release year
        addAttributesToMap(attributes1, new String[]{game1.getRelease_date().substring(game1.getRelease_date().length()-5,game1.getRelease_date().length()-1)});

        addAttributesToMap(attributes1, new String[]{String.valueOf(game1.getRequired_age())});

        addAttributesToMap(attributes1, new String[]{game1.getPrice().toString()});

        if (game1.getIs_for_windows()) {
            addAttributesToMap(attributes1, new String[]{"Windows"});
        }

        if (game1.getIs_for_linux()) {
            addAttributesToMap(attributes1, new String[]{"Linux"});
        }

        if (game1.getIs_for_mac()) {
            addAttributesToMap(attributes1, new String[]{"Mac"});
        }

        if (game1.getDevelopers() != null) {
            addAttributesToMap(attributes1, game1.getDevelopers());

        }

        if (game1.getPublishers() != null) {
            addAttributesToMap(attributes1, game1.getPublishers());
        }

        if (game1.getGenres() != null) {
            addAttributesToMap(attributes1, game1.getGenres());
        }

        if (game1.getCategories() != null) {
            addAttributesToMap(attributes1, game1.getCategories());
        }

        if (game1.getTags() != null) {
            addAttributesToMap(attributes1, game1.getTags());
        }

        Map<CharSequence, Integer> attributes2 = new HashMap<>();

        addAttributesToMap(attributes2, new String[]{game2.getName()});

        addAttributesToMap(attributes2, new String[]{game2.getAbout()});

        // release year
        addAttributesToMap(attributes2, new String[]{game2.getRelease_date().substring(game2.getRelease_date().length()-5,game2.getRelease_date().length()-1)});

        addAttributesToMap(attributes2, new String[]{String.valueOf(game2.getRequired_age())});

        addAttributesToMap(attributes2, new String[]{game2.getPrice().toString()});

        if (game2.getIs_for_windows()) {
            addAttributesToMap(attributes2, new String[]{"Windows"});
        }

        if (game2.getIs_for_linux()) {
            addAttributesToMap(attributes2, new String[]{"Linux"});
        }

        if (game2.getIs_for_mac()) {
            addAttributesToMap(attributes2, new String[]{"Mac"});
        }

        if (game2.getDevelopers() != null) {
            addAttributesToMap(attributes2, game2.getDevelopers());

        }

        if (game2.getPublishers() != null) {
            addAttributesToMap(attributes2, game2.getPublishers());
        }

        if (game2.getGenres() != null) {
            addAttributesToMap(attributes2, game2.getGenres());
        }

        if (game2.getCategories() != null) {
            addAttributesToMap(attributes2, game2.getCategories());
        }

        if (game2.getTags() != null) {
            addAttributesToMap(attributes2, game2.getTags());
        }



        // Calculate cosine similarity using Apache Commons Text library
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        Double similarityScore = cosineSimilarity.cosineSimilarity(attributes1, attributes2);
        return similarityScore;
    }

    private void addAttributesToMap(Map<CharSequence, Integer> map, String[] attributes) {
        for (String attribute : attributes) {
            map.merge(attribute, 1, Integer::sum);
        }
    }


    public void batchCreateSteamGames(final Collection<SteamGame> steamGames) {

        try {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO steam_game (game_id, name, release_date, required_age, " +
                            "price, about, image_url, is_for_windows, is_for_mac, is_for_linux, developers, publishers, categories, genres, tags) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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
                        ps.setArray(11, ps.getConnection().createArrayOf("VARCHAR", steamGame.getDevelopers()));
                        ps.setArray(12, ps.getConnection().createArrayOf("VARCHAR", steamGame.getPublishers()));
                        ps.setArray(13, ps.getConnection().createArrayOf("VARCHAR", steamGame.getCategories()));
                        ps.setArray(14, ps.getConnection().createArrayOf("VARCHAR", steamGame.getGenres()));
                        ps.setArray(15, ps.getConnection().createArrayOf("VARCHAR", steamGame.getTags()));
                    });
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Error batch creating steam games");
        }

        System.out.println("Batch creation complete");
    }

    private void readLinesFromListAndPopulateSet(List<String> listOfStringsFromBufferedReader, Set<SteamGame> setOfSteamGamesToPopulate, HashMap<String, Integer> columnMap) {

        for (int i = 0; i < listOfStringsFromBufferedReader.size(); i++) {
            SteamGame steamGame = new SteamGame();

            String thisLine = String.valueOf(listOfStringsFromBufferedReader.get(i));
            String[] splitLine = thisLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            int game_id = Integer.parseInt(splitLine[columnMap.get("AppID")]);
            String name = splitLine[columnMap.get("Name")].trim().replaceAll("^\"|\"$", "");;
            String release_date = splitLine[columnMap.get("Release date")].trim().replaceAll("^\"|\"$", "");;
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
            String about = splitLine[columnMap.get("About the game")].trim().replaceAll("^\"|\"$", "");
            String image_url = splitLine[columnMap.get("Header image")].trim().replaceAll("^\"|\"$", "");
            boolean is_for_windows = Boolean.parseBoolean(splitLine[columnMap.get("Windows")]);
            boolean is_for_mac = Boolean.parseBoolean(splitLine[columnMap.get("Mac")]);
            boolean is_for_linux = Boolean.parseBoolean(splitLine[columnMap.get("Linux")]);
            String[] developers = Arrays.stream(splitLine[columnMap.get("Developers")].split(","))
                    .map(String::trim)
                    .map(s -> s.replaceAll("^\"|\"$", ""))
                    .toArray(String[]::new);

            String[] publishers = Arrays.stream(splitLine[columnMap.get("Publishers")].split(","))
                    .map(String::trim)
                    .map(s -> s.replaceAll("^\"|\"$", ""))
                    .toArray(String[]::new);

            String[] categories = Arrays.stream(splitLine[columnMap.get("Categories")].split(","))
                    .map(String::trim)
                    .map(s -> s.replaceAll("^\"|\"$", ""))
                    .toArray(String[]::new);

            String[] genres = Arrays.stream(splitLine[columnMap.get("Genres")].split(","))
                    .map(String::trim)
                    .map(s -> s.replaceAll("^\"|\"$", ""))
                    .toArray(String[]::new);

            String[] tags = Arrays.stream(splitLine[columnMap.get("Tags")].split(","))
                    .map(String::trim)
                    .map(s -> s.replaceAll("^\"|\"$", ""))
                    .toArray(String[]::new);

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
            steamGame.setDevelopers(developers);
            steamGame.setPublishers(publishers);
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
            } else if (currentString.contains("Developers")) {
                columnMap.put("Developers", i);
            } else if (currentString.contains("Publishers")) {
                columnMap.put("Publishers", i);
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

    private SteamGame mapRowToSteamGame(SqlRowSet rs) throws SQLException {
        SteamGame steamGame = new SteamGame();
        steamGame.setGame_id(rs.getInt("game_id"));
        steamGame.setName(rs.getString("name"));
        steamGame.setRelease_date(rs.getString("release_date"));
        steamGame.setRequired_age(rs.getInt("required_age"));
        steamGame.setPrice(rs.getBigDecimal("price"));
        steamGame.setAbout(rs.getString("about"));
        steamGame.setImage_url(rs.getString("image_url"));
        steamGame.setIs_for_windows(rs.getBoolean("is_for_windows"));
        steamGame.setIs_for_mac(rs.getBoolean("is_for_mac"));
        steamGame.setIs_for_linux(rs.getBoolean("is_for_linux"));

        Object developersObject = rs.getObject("developers");

        if(developersObject instanceof Array) {
            Array tempArray = (Array) developersObject;
            Object[] tempObjectArray = (Object[]) tempArray.getArray();
            String[] developers = new String[tempObjectArray.length];
            for (int i = 0; i < tempObjectArray.length; i++) {
                developers[i] = tempObjectArray[i].toString();
            }
            steamGame.setDevelopers(developers);
        }

        Object publishersObject = rs.getObject("publishers");

        if(publishersObject instanceof Array) {
            Array tempArray = (Array) publishersObject;
            Object[] tempObjectArray = (Object[]) tempArray.getArray();
            String[] publishers = new String[tempObjectArray.length];
            for (int i = 0; i < tempObjectArray.length; i++) {
                publishers[i] = tempObjectArray[i].toString();
            }
            steamGame.setPublishers(publishers);
        }

        Object categoriesObject = rs.getObject("categories");

        if(categoriesObject instanceof Array) {
            Array tempArray = (Array) categoriesObject;
            Object[] tempObjectArray = (Object[]) tempArray.getArray();
            String[] categories = new String[tempObjectArray.length];
            for (int i = 0; i < tempObjectArray.length; i++) {
                categories[i] = tempObjectArray[i].toString();
            }
            steamGame.setCategories(categories);
        }

        Object genresObject = rs.getObject("genres");

        if(genresObject instanceof Array) {
            Array tempArray = (Array) genresObject;
            Object[] tempObjectArray = (Object[]) tempArray.getArray();
            String[] genres = new String[tempObjectArray.length];
            for (int i = 0; i < tempObjectArray.length; i++) {
                genres[i] = tempObjectArray[i].toString();
            }
            steamGame.setGenres(genres);
        }

        Object tagsObject = rs.getObject("tags");

        if(tagsObject instanceof Array) {
            Array tempArray = (Array) tagsObject;
            Object[] tempObjectArray = (Object[]) tempArray.getArray();
            String[] tags = new String[tempObjectArray.length];
            for (int i = 0; i < tempObjectArray.length; i++) {
                tags[i] = tempObjectArray[i].toString();
            }
            steamGame.setTags(tags);
        }

        return steamGame;
    }
}
