<template>
  <NavBar/>
  <router-view></router-view>
</template>
<script>
// 导入组件
import NavBar from './components/NavBar.vue'
// 导入bootstrap
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"

// import $ from 'jquery'

export default{
  components:{
    NavBar
  }
}
</script>
<style>
body{
  background-image: url("./assets/images/background.jpg");
  background-size: cover;
}
</style>
