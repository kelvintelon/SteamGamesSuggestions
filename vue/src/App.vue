<template>
  <div id="app">
    <nav>
      <router-link to="/">Home</router-link> | 
      <a href="https://github.com/kelvintelon/SteamGamesSuggestions" target="_blank">Source Code</a>
    </nav>
    <router-view/>
  </div>
</template>

<style>
#app {
  font-family: system-ui;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  color: #ffffff;
}

body {
  background-image: url('assets/arcadeRoom.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  min-height: 100vh;
}
</style>
