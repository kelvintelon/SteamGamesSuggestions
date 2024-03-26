package com.steamgames.model;

import java.math.BigDecimal;

public class SteamGame {

    private int game_id;
    private String name;
    private String release_date;
    private int required_age;
    private BigDecimal price;
    private String about;
    private String image_url;
    private Boolean is_for_windows;
    private Boolean is_for_mac;
    private Boolean is_for_linux;
    private String[] developers;
    private String[] publishers;
    private String[] categories;
    private String[] genres;
    private String[] tags;

    private double similarity_score;

    public SteamGame(int game_id, String name, String release_date, int required_age, BigDecimal price, String about, String image_url, Boolean is_for_windows, Boolean is_for_mac, Boolean is_for_linux, String[] categories, String[] genres, String[] tags) {
        this.game_id = game_id;
        this.name = name;
        this.release_date = release_date;
        this.required_age = required_age;
        this.price = price;
        this.about = about;
        this.image_url = image_url;
        this.is_for_windows = is_for_windows;
        this.is_for_mac = is_for_mac;
        this.is_for_linux = is_for_linux;
        this.categories = categories;
        this.genres = genres;
        this.tags = tags;
    }

    public SteamGame() {

    }

    public double getSimilarity_score() {
        return similarity_score;
    }

    public void setSimilarity_score(double similarity_score) {
        this.similarity_score = similarity_score;
    }

    public String[] getDevelopers() {
        return developers;
    }

    public void setDevelopers(String[] developers) {
        this.developers = developers;
    }

    public String[] getPublishers() {
        return publishers;
    }

    public void setPublishers(String[] publishers) {
        this.publishers = publishers;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getRequired_age() {
        return required_age;
    }

    public void setRequired_age(int required_age) {
        this.required_age = required_age;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Boolean getIs_for_windows() {
        return is_for_windows;
    }

    public void setIs_for_windows(Boolean is_for_windows) {
        this.is_for_windows = is_for_windows;
    }

    public Boolean getIs_for_mac() {
        return is_for_mac;
    }

    public void setIs_for_mac(Boolean is_for_mac) {
        this.is_for_mac = is_for_mac;
    }

    public Boolean getIs_for_linux() {
        return is_for_linux;
    }

    public void setIs_for_linux(Boolean is_for_linux) {
        this.is_for_linux = is_for_linux;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
