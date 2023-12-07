<!-- 基本组件:游玩区域 -->
<template>
    <div class="playground">
        <GameMap/>
    </div>
</template>
<script>
import GameMap from './GameMap.vue'

export default {
    components:{
        GameMap
    }    
}
</script>
<style scoped>
div.playground{
    /* 浏览器宽度60% */
    width: 60vw;
    /* 浏览器高度70% */
    height: 70vh;
    /* 居中 距离上方边距40px */
    margin: 40px auto;
} 
</style>
