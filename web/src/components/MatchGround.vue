<!-- 基本组件:匹配区域 -->
<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user_photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user_username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                        <option value="-1" selected>亲自上阵</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">
                            {{ bot.title }}
                        </option>
                    </select>
                </div>
            </div>
            <div class="col-4">
                <div class="user_photo">
                    <img :src="$store.state.pk.opponent_photo" alt="">
                </div>
                <div class="user_username">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-12" style="text-align:center; padding-top:12vh">
                <button @click="click_match_btn" class="btn btn-success btn-lg">{{ match_btn_info }}</button>
            </div>
        </div>

    </div>
</template>
<script>
import { ref } from 'vue'
import { useStore } from 'vuex';
import $ from 'jquery'
export default {

    setup() {
        const store = useStore();
        let match_btn_info = ref("开始匹配");
        let bots = ref([]);
        let select_bot = ref("-1");
        const click_match_btn = () => {
            if (match_btn_info.value === "开始匹配") {
                console.log(select_bot.value);
                match_btn_info.value = "取消";
                //JSON.stringify将JSON转换为字符串
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching",
                    bot_id:select_bot.value,
                }));
            } else { 
                match_btn_info.value = "开始匹配";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }));
            }
        };

        //动态获取bot列表
        const refresh_bots = () => {
            $.ajax({
                url: "https://app3154.acapp.acwing.com.cn/api/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                }
            })
        }

        refresh_bots();//从云端动态获取Bot
        

        return {
            match_btn_info,
            click_match_btn,
            refresh_bots,
            bots,//一定要返回 否则前端获取不到
            select_bot
        }
    }

}
</script>
<style scoped>
div.matchground {
    /* 浏览器宽度60% */
    width: 60vw;
    /* 浏览器高度70% */
    height: 70vh;
    /* 居中 距离上方边距40px */
    margin: 40px auto;
    background-color: rgba(50, 50, 50, 0.5);
}

div.user_photo {
    text-align: center;
    padding-top: 10vh;
}

div.user_photo>img {
    border-radius: 50%;
    width: 20vh;
}

div.user_username {
    text-align: center;
    font-size: 24px;
    font-weight: 600;
    color: white;
    padding-top: 2vh;
}


div.user-select-bot{
    padding-top: 20vh;
}
div.user-select-bot > select{
    width: 60%;
    margin: 0 auto;
}
</style>
