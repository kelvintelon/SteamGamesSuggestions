<template>
  <div style="background-image: url('src\assets\arcadeRoom.jpg')">
    <h1 class="steamGamesHeader"> Steam Games Suggestions </h1>
      <upload-games-component v-if="showUploadButton"></upload-games-component>
      <find-steam-game-component class="searchComponent"></find-steam-game-component>
      <v-row justify="center" class="searchedGameTable">
        <v-spacer></v-spacer>
        <v-col justify="center" lg="9">
           <searched-game-table-component ></searched-game-table-component>
        </v-col>
        <v-spacer></v-spacer>
      </v-row>
      
  </div>
</template>

<script>
import FindSteamGameComponent from './findSteamGameComponent.vue';
import searchedGameTableComponent from './searchedGameTableComponent.vue';
import uploadGamesComponent from './uploadGamesComponent.vue'
export default {
  components: { uploadGamesComponent, FindSteamGameComponent, searchedGameTableComponent},
  name: 'Home-Component',
  props: {
    msg: String
  },
  data() {
    return {
      showUploadButton: false,
    };
  },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

.steamGamesHeader {
  color: #ffffff;
  border: 2px solid white;
  display: inline-block;
  padding: 20px;
  background-color: rgba(219, 20, 226, 0.671);
}

.searchComponent {
  margin-top: 80px;
  width: 700px
}

.searchedGameTable {
  margin-top: 70px;
}
</style>
