<template>
    <ContentFieldVue v-if="!$store.state.user.pulling_info">
        <div class="row  justify-content-md-center">
            <div class="col-3">
                <!--submit时触发login函数 并阻止默认行为-->
                <form @submit.prevent="login"> 
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <!-- 使用v-model与定义的变量username绑定 -->
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <!-- 使用v-model与定义的变量password绑定 -->
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <!-- {{error_message}}显示变量error_message的值 -->
                    <div class="error_message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary">登录</button>
                </form>
            </div>
        </div>
    </ContentFieldVue>
</template>
<script>
import ContentFieldVue from '../../../components/ContentField.vue'
import { useStore } from 'vuex' //全局变量
import { ref } from 'vue';//所有变量的定义借助ref表示
import router from '../../../router/index';//如果登录成功 跳转到相应的路由页面
export default {
    components: {
        ContentFieldVue
    },
    setup(){
        const store = useStore();//取出全局变量
        let username = ref("");//定义username 初始为空
        let password = ref("");//定义password
        let error_message = ref("");//表示是否成功登录
        const jwt_token = localStorage.getItem("jwt_token");
        if(jwt_token){//若jwt_token存在
            store.commit("updateToken",jwt_token);//将token更新为jwt_token
            //判断jwt是否有效
            store.dispatch("getinfo", {
                success(){
                    router.push({name:"home"});
                    store.commit("updatePullingInfo", false);//拉取结束
                },
                error(){
                    store.commit("updatePullingInfo", false);
                }
            })
        }else{
            store.commit("updatePullingInfo", false);
        }
        const login = () => { //定义login函数 当页面提交时触发
            error_message.value = "";
            store.dispatch("login",{//使用dispatch来调用store\user.js中action中函数
                username:username.value,//ref变量取值使用value
                password:password.value,
                success(){
                    //console.log(resp);
                    store.dispatch("getinfo",{
                        success(){
                            router.push({name:'home'})
                            //console.log(store.state);
                            //这里的user就是在store/index中导入的user: ModuleUser
                            //console.log(store.state.user);
                        }
                    })
                },
                error(){//如果登录失败，给用户返回一个提示
                    error_message.value = "用户名或密码错误";
                }
            })
        }
        return{
            username,
            password,
            error_message,
            login,
        }
    }
}
</script>
<style scoped>
button{
    width: 100%;
}
div.error_message{
    color:red;
}
</style>