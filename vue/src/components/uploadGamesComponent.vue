<template>
    <v-container>
      <form ref="uploadForm" @submit.prevent="submit">
          <v-row>
              <v-col>
                  <v-btn @click="uploadSteamGamesCSV" v-if="!attemptedUpload">
                      Upload Steam Games
                  </v-btn>
                  <v-btn @click="uploadSteamGamesCSV" v-if="attemptedUpload && successfulSteamGamesUpload" color="green">
                      Upload Steam Games
                  </v-btn>
                  <v-btn @click="uploadSteamGamesCSV" v-if="attemptedUpload && !successfulSteamGamesUpload" color="red">
                      Upload Steam Games
                  </v-btn>
              </v-col>
              <v-col>
                  <input type="file" ref="uploadCSV" @change="onCSVUpload()" class="form-control" required>
              </v-col>
              <v-spacer>  
              </v-spacer>
          </v-row>
      </form>
      <v-overlay :value="overlay">
      <v-progress-circular indeterminate size="70"></v-progress-circular>
    </v-overlay>
    </v-container>
</template>

<script>
  import databaseUploadService from "../services/DatabaseUploadService";
  export default {
    name: "upload-games-Component",
    components: {},
    data() {
      return {
          steamGamesFormData: null,
          attemptedUpload: false,
          successfulSteamGamesUpload: false,
          overlay: false
      };
    },
    created() {
    },
    methods: {
      onCSVUpload() {
          let file = this.$refs.uploadCSV.files[0];
          this.steamGamesFormData = new FormData();
          this.steamGamesFormData.append("file", file)
      },
      uploadSteamGamesCSV() {
          if (this.steamGamesFormData != null) {
              this.attemptedUpload = true;
              this.overlay = true;
              databaseUploadService.uploadSteamGames(this.steamGamesFormData).then((response) => {
                alert("hit")
                  if (response.status == 200) {
                      this.overlay = false;
                      alert("Successfully uploaded steam games to database")
                      this.successfulSteamGamesUpload = true;
                  } 
              }).catch((error) => {
            const response = error.response;
            if (response.status === 500 || response.status === 400) {
            alert(error.response.data.message)
              this.overlay = false;
            }
          });
          } else if (this.attemptedUpload) {
              alert("Select a different file to upload")
          } else {
              alert("Please choose a file")
          }
      }
    },
  };
  </script>
  
  <style>
  </style>