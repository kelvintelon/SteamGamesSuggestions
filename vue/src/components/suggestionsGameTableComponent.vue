<template>
    <v-data-table :headers="headers" :items="$store.state.gameSuggestions" class="dataTable"
        :single-expand="singleExpand" :expanded.sync="expanded" show-expand item-key="game_id" dense>
        <template v-slot:top>
            <v-toolbar flat class="dataTable">
                <v-toolbar-title class="tableTitle">Cosine Similarity Suggestions for:
                    "{{ $store.state.chosenGameForSuggestion.name }}"</v-toolbar-title>
                <v-spacer></v-spacer>
                <filter-component></filter-component>
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
        <v-overlay :value="overlay">
            <v-progress-circular indeterminate size="70"></v-progress-circular>
        </v-overlay>
    </v-data-table>
</template>

<script>
import SteamGamesService from '@/services/SteamGamesService';
import filterComponent from './filterComponent.vue';

export default {
    name: "suggestions-game-table-component",
    components: { filterComponent },
    data() {
        return {
            expanded: [],
            overlay: false,
            singleExpand: true,
            headers: [
                { text: '', value: 'image_url', sortable: false },
                { text: 'Name', value: 'name', sortable: false, },
                { text: 'Additional Info', value: 'data-table-expand' },
                { text: 'Release', value: 'release_date', sortable: false },
                { text: 'Price', value: 'price', sortable: false },
                { text: 'Developers', value: 'developers', sortable: false },
                { text: 'Publishers', value: 'publishers', sortable: false },
                // { text: 'Categories', value: 'categories', sortable: false },
                // { text: 'Genres', value: 'genres', sortable: false },
                // { text: 'Tags', value: 'tags', sortable: false },
                { text: "Similarity Score (0 to 1)", value: "similarity_score", sortable: false },

            ],

        };
    },
    created() { },
    methods: {
        resetField() {
            this.steamGameNameToFind = ""
        },
        getSuggestions(game) {
            SteamGamesService
                .getSuggestions(game.game_id)
                .then((response) => {
                    if (response.status == 200) {
                        this.$store.commit("SET_CHOSEN_GAME", game)
                        this.$store.commit("SET_GAME_SUGGESTIONS", response.data);
                    }
                })
                .catch((error) => {
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