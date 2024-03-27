<template>
  <div class="home">
    <home-Component/>
  </div>
</template>

<script>
// @ is an alias to /src
import homeComponent from '@/components/homeComponent.vue'

export default {
  name: 'HomeView',
  components: {
    homeComponent
  }
}
</script>
