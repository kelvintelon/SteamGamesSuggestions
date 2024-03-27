<template>
    <v-container>
        <v-card class="searchCard">
            <v-card-title>
                <span  style="color: rgba(0, 42, 253, 1)">Search for Steam Game</span>
            </v-card-title>
            <v-card-text>
                <v-container>
                    <v-row>
                        <v-col cols="12">
                            <v-text-field label="Enter Steam Game Name" v-model="steamGameNameToFind" required
                                :rules="nameRules"></v-text-field>
                        </v-col>
                    </v-row>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="red darken-1" text @click="resetField">
                    Reset
                </v-btn>
                <v-btn style="color: rgba(0, 42, 253, 1)" text @click="searchSteamGame">
                    Search
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-container>
</template>

<script>
import SteamGamesService from '@/services/SteamGamesService';

export default {
    name: "find-steam-game-component",
    components: {},
    data() {
        return {
            steamGameNameToFind: '',
            nameRules: [
                (v) => !!v || "Steam game name is required",
                (v) =>
                    (v && v.length <= 30) || "Steam game name must be less than 30 characters",
            ],
        };
    },
    created() { },
    methods: {
        resetField() {
            this.steamGameNameToFind = ""
        },
        searchSteamGame() {
            if (this.steamGameNameToFind.length == 0) {
                alert("Please Include Steam Game Name")
            } else {
                SteamGamesService
                    .findSteamGame(this.steamGameNameToFind)
                    .then((response) => {
                        if (response.status == 200) {
                            this.$store.commit("SET_SEARCHED_GAMES", response.data);
                        }
                    })
                    .catch((error) => {
                        const response = error.response;
                        if (response.status === 400 || response.status === 404) {
                            alert(error.response.data.message)
                        }
                    });
            }

        }
    },
};
</script>

<style scoped>
.searchCard {
    background-color: rgba(255, 255, 255, 0.9);
}
</style>