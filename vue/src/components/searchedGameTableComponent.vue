<template>
    <v-container>
        <v-data-table :headers="headers" :items="$store.state.searchedGames" class="dataTable"
            :single-expand="singleExpand" :expanded.sync="expanded" show-expand item-key="game_id" dense>
            <template v-slot:top>
                <v-toolbar flat class="dataTable">
                    <v-toolbar-title class="tableTitle">Search List</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-toolbar-title class="descriptionTitle">Select a game to find suggestions</v-toolbar-title>
                </v-toolbar>
            </template>
            <template v-slot:[`item.image_url`]="{ item }">
                <v-img max-height="100" max-width="150" :src="item.image_url"></v-img>
            </template>
            <template v-slot:expanded-item="{ headers, item }">
            <td :colspan="headers.length">
                <v-simple-table>
                    <template v-slot:default>
                        <thead>
                            <tr>
                                <th>Description</th>
                                <th>Categories</th>
                                <th>Genres</th>
                                <th>Tags</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr :key="item.game_id">
                                <td>{{ item.about }}</td>
                                <td>{{ item.categories }}</td>
                                <td>{{ item.genres }}</td>
                                <td>{{ item.tags }}</td>
                            </tr>
                        </tbody>
                    </template>
                </v-simple-table>
            </td>

        </template>
            <template v-slot:[`item.actions`]="{ item }">
                <v-btn small class="mr-2" @click="getSuggestions(item)" style="color: rgba(0, 42, 253, 1)">
                    Suggestions
                </v-btn>
            </template>

        </v-data-table>
        <v-overlay :value="overlay">
            <v-progress-circular indeterminate size="70"></v-progress-circular>
        </v-overlay>
    </v-container>
</template>

<script>
import SteamGamesService from '@/services/SteamGamesService';

export default {
    name: "searched-game-table-component",
    components: {},
    data() {
        return {
            expanded: [],
            singleExpand: true,
            overlay: false,
            headers: [
                {
                    text: 'ID',
                    align: 'start',
                    sortable: false,
                    value: 'game_id',
                },
                { text: '', value: 'image_url', sortable: false },
                { text: 'Name', value: 'name', sortable: false, },
                { text: 'Additional Info', value: 'data-table-expand' },
                { text: 'Release', value: 'release_date', sortable: false },
                { text: 'Price', value: 'price', sortable: false },
                { text: 'Developers', value: 'developers', sortable: false },
                { text: 'Publishers', value: 'publishers', sortable: false },
                { text: "Select Game", value: "actions", sortable: false },

            ],

        };
    },
    created() { },
    methods: {
        resetField() {
            this.steamGameNameToFind = ""
        },
        getSuggestions(game) {
            this.overlay = true;
            SteamGamesService
                .getSuggestions(game.game_id)
                .then((response) => {
                    if (response.status == 200) {
                        this.overlay = false;
                        this.$store.commit("SET_CHOSEN_GAME", game)
                        this.$store.commit("SET_GAME_SUGGESTIONS", response.data);
                        this.$store.commit("SET_OPTIMAL_GAME_SUGGESTIONS", response.data);
                    }
                })
                .catch((error) => {
                    this.overlay = false;
                    const response = error.response;
                    if (response.status === 400 || response.status === 404) {
                        alert(error.response.data.message)
                    }
                });
        }
    },
};
</script>

<style scoped>
.tableTitle {
    color: blueviolet;
    text-decoration: underline;
    font-weight: bold;
}

.descriptionTitle {
    color: rgba(0, 42, 253, 1);
    text-decoration: underline;
    font-weight: bold;
}

.dataTable {
    border-radius: 10px;
    background-color: rgba(255, 255, 255, 0.95);

}
</style>