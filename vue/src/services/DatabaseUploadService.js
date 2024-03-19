import axios from 'axios';

const http = axios.create({
    baseURL: "http://localhost:9000",
  });

export default {

    uploadSteamGames(clientCSV) {
        return http.post(`/uploadSteamGames`, clientCSV, {headers: {Accept: 'application/json', 'Content-Type': 'multipart/form-data'} })
    },

}
