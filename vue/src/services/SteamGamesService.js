import axios from 'axios';

const http = axios.create({
    baseURL: "http://localhost:9000",
  });

export default {

    findSteamGame(name) {
        return http.get(`/findSteamGame/${name}`)
    },

}
