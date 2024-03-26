<template>
    <v-data-table :headers="headers" :items="$store.state.searchedGames" class="dataTable" :single-expand="false"
        :expanded.sync="expanded" @click:row="(item, slot) => slot.expand(!slot.isExpanded)" dense>
        <template v-slot:top>
            <v-toolbar flat class="dataTable">
                <v-toolbar-title class="tableTitle">Click on row to see description</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-toolbar-title class="descriptionTitle">Select a game to find suggestions</v-toolbar-title>

            </v-toolbar>
        </template>
        <template v-slot:[`item.image_url`]="{ item }">
            <v-img max-height="100" max-width="150" :src="item.image_url"></v-img>
        </template>
        <template v-slot:expanded-item="{ headers, item }">
            <td :colspan="headers.length">
                <div>{{ item.about }}</div>
            </td>
        </template>
        <template v-slot:[`item.actions`]="{ item }">
            <v-btn small class="mr-2" @click="getSuggestions(item)" style="color: rgba(0, 42, 253, 1)">
                Get Suggestions
            </v-btn>
        </template>
    </v-data-table>
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
            headers: [
                {
                    text: 'ID',
                    align: 'start',
                    sortable: false,
                    value: 'game_id',
                },                
                { text: '', value: 'image_url', sortable: false },
                { text: 'Name', value: 'name', sortable: false, },
                { text: "Select Game", value: "actions", sortable: false },

                // { text: 'Price', value: 'price', sortable: false },
                { text: '', value: 'data-table-expand' },
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
                            alert("Error steam game not found")
                        }
                    });
            }

        }
    },
};
</script>

<style scoped>
.tableTitle {
    color: rgba(255, 24, 24, 0.9);
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
}
</style>