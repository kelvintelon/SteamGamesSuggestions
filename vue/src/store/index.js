import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    searchedGames: [],
    chosenGameForSuggestion: {},
    gameSuggestions: [],
  },
  getters: {
  },
  mutations: {
    SET_SEARCHED_GAMES(state, returnGameList) {
      state.searchedGames = returnGameList;
    },
    SET_CHOSEN_GAME(state, game) {
      state.chosenGameForSuggestion = game;
    },
    SET_GAME_SUGGESTIONS(state, returnGameList) {
      state.gameSuggestions = returnGameList;
    },
  },
  actions: {
  },
  modules: {
  }
})
