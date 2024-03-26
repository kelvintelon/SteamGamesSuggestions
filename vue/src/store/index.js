import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    searchedGames: [],
  },
  getters: {
  },
  mutations: {
    SET_SEARCHED_GAMES(state, returnGameList) {
      state.searchedGames = returnGameList;
    },
  },
  actions: {
  },
  modules: {
  }
})
