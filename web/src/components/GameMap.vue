<!-- 基础组件:地图画面 -->
<template>
    <div ref="parent" class="gamemap">
        <!-- 画布 -->
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>
<script>
import { GameMap } from '../assets/scripts/GameMap'
import {onMounted, ref} from 'vue' //用于定义变量
import { useStore } from 'vuex';
export default {
    setup(){
        const store = useStore();
        let parent = ref(null);
        let canvas = ref(null);

        onMounted(()=>{
            store.commit("updateGameObject",
                new GameMap(canvas.value.getContext('2d'), parent.value, store)
            );
        });
        return{
            parent,
            canvas
        }
    }
}
</script>
<style scoped>
    div.gamemap{
        /* 100%相对于父元素来说的  */
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-content: center;
    }
</style>