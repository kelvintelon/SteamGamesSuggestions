<template>
  <div >
    <h1> Steam Games Suggestions </h1>
      <upload-games-component v-if="showUploadButton"></upload-games-component>
  </div>
</template>

<script>
import uploadGamesComponent from './uploadGamesComponent.vue'
export default {
  components: { uploadGamesComponent },
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
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
