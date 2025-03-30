<template>
  <div class="jdwa-header">
    <!--图标-->
    <div class="header-logo" @click="handleLogoClick">
      <jdwa-icon :icon="iconList.ERJI"></jdwa-icon>
      <span>{{ musicName }}</span>
    </div>
    <jdwa-header-nav class="jdwa-header-nav" :styleList="headerNavList" :activeName="activeNavName" @click="goPage"></jdwa-header-nav>
    <!--搜索框-->
    <div class="header-search">
      <el-input placeholder="搜索" :prefix-icon="Search" v-model="keywords" @keyup.enter="goSearch()" />
    </div>
    <!--设置-->
    <jdwa-header-nav v-if="!token" :styleList="signList" :activeName="activeNavName" @click="goPage"></jdwa-header-nav>
    <el-dropdown class="user-wrap" v-if="token" trigger="click" :teleported="false" @command="handleCommand">
      <el-image 
        class="user" 
        fit="cover" 
        :src="userPic"
        :alt="'用户头像'"
        loading="eager"
        :preview-src-list="[]"
        @error="handleImageError"
      >
        <template #error>
          <div class="image-error">
            <el-icon><Avatar /></el-icon>
          </div>
        </template>
      </el-image>
      <template #dropdown>
        <el-dropdown-menu role="menu">
          <el-dropdown-item command="personal" role="menuitem">个人中心</el-dropdown-item>
          <el-dropdown-item command="setting" role="menuitem">设置</el-dropdown-item>
          <el-dropdown-item command="logout" role="menuitem">退出</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, getCurrentInstance, computed, reactive, watch, onMounted } from "vue";
import { Search, Avatar } from "@element-plus/icons-vue";
import { useStore } from "vuex";
import JdwaIcon from "./JdwaIcon.vue";
import JdwaHeaderNav from "./JdwaHeaderNav.vue";
import mixin from "@/mixins/mixin";
import { HEADERNAVLIST, SIGNLIST, MENULIST, Icon, MUSICNAME, RouterName, NavName } from "@/enums";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    JdwaIcon,
    JdwaHeaderNav,
    Avatar,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { changeIndex, routerManager } = mixin();

    const musicName = ref(MUSICNAME);
    const headerNavList = ref(HEADERNAVLIST); // 左侧导航栏
    const signList = ref(SIGNLIST); // 右侧导航栏
    const menuList = ref(MENULIST); // 用户下拉菜单项
    const iconList = reactive({
      ERJI: Icon.ERJI,
    });
    const keywords = ref("");
    const activeNavName = computed(() => store.getters.activeNavName);
    const defaultAvatar = ref('/img/avatorImages/user.jpg');

    // 修改 userPic 计算属性，简化逻辑
    const userPic = computed(() => {
      const pic = store.getters.userPic;
      if (pic) {
        return HttpManager.attachImageUrl(pic);
      }
      return defaultAvatar.value;
    });

    const token = computed(() => store.getters.token);

    function goPage(path, name) {
      if (!path && !name) {
        changeIndex(NavName.Home);
        routerManager(RouterName.Home, { path: RouterName.Home });
      } else {
        changeIndex(name);
        routerManager(path, { path });
      }
    }

    function goMenuList(path) {
      if (path == RouterName.SignOut) {
        proxy.$store.commit("setToken", false);
        changeIndex(NavName.Home);
        routerManager(RouterName.Home, { path: RouterName.Home });
      } else {
        routerManager(path, { path });
      }
    }
    function goSearch() {
      if (keywords.value !== "") {
        proxy.$store.commit("setSearchWord", keywords.value);
        routerManager(RouterName.Search, { path: RouterName.Search, query: { keywords: keywords.value } });
      } else {
        (proxy as any).$message({
          message: "搜索内容不能为空",
          type: "error",
        });
      }
    }

    // 修改 onMounted 钩子中的状态恢复逻辑
    onMounted(() => {
      // 从 sessionStorage 恢复状态
      const dataStore = sessionStorage.getItem("dataStore");
      if (dataStore) {
        try {
          const state = JSON.parse(dataStore);
          // 重要：先恢复 token 状态
          if (state.token) {
            store.commit('setToken', true); // 修改这里，确保设置为 true
          }
          // 然后恢复用户信息
          if (state.user) {
            store.commit('setUser', state.user);
            if (state.user.pic) {
              store.commit('setUserPic', state.user.pic);
            }
          }
        } catch (error) {
          console.error('恢复状态失败:', error);
          // 错误时清理状态
          sessionStorage.removeItem('dataStore');
          store.commit('setToken', false);
          store.commit('setUser', null);
        }
      }
    });

    // 修改 token 监听逻辑
    watch(() => token.value, (newToken, oldToken) => {
      if (newToken && !oldToken) {  // 只在首次登录时触发
        const userInfo = store.getters.userInfo;
        if (userInfo && userInfo.pic) {
          // 先保存状态
          store.commit('setUserPic', userInfo.pic);
          // 保存完整状态到 sessionStorage
          const stateToSave = {
            ...store.state,
            token: true, // 确保保存 true 而不是 token 值
            user: userInfo
          };
          sessionStorage.setItem('dataStore', JSON.stringify(stateToSave));
          // 延迟刷新页面
          setTimeout(() => {
            window.location.reload();
          }, 100);
        }
      } else if (!newToken) {
        // 退出登录时清理所有状态
        store.commit('setUserPic', '');
        store.commit('setUser', null);
        sessionStorage.removeItem('dataStore');
      }
    });

    // 修改退出登录的处理函数
    function handleCommand(command) {
      if (command === "logout") {
        // 先清除 sessionStorage
        sessionStorage.removeItem('dataStore');
        // 再清除 store 状态
        store.commit('setToken', false);
        store.commit('setUser', null);
        store.commit('setUserPic', '');
        changeIndex(NavName.Home);
        routerManager(RouterName.Home, { path: RouterName.Home });
      } else if (command === "personal") {
        routerManager(RouterName.Personal, { path: RouterName.Personal });
      } else if (command === "setting") {
        routerManager(RouterName.Setting, { path: RouterName.Setting });
      }
    }

    // 简化图片错误处理
    function handleImageError() {
      store.commit('setUserPic', defaultAvatar.value);
    }

    // 处理 logo 点击
    function handleLogoClick() {
      changeIndex(NavName.Home);
      routerManager(RouterName.Home, { path: RouterName.Home });
    }

    return {
      musicName,
      headerNavList,
      signList,
      menuList,
      iconList,
      keywords,
      activeNavName,
      userPic,
      token,
      Search,
      Avatar,
      goPage,
      goMenuList,
      goSearch,
      handleCommand,
      handleImageError,
      handleLogoClick,
      defaultAvatar,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

@media screen and (min-width: $sm) {
  .header-logo {
    margin: 0 1rem;
  }
}

@media screen and (max-width: $sm) {
  .header-logo {
    margin: 0 1rem;
    span {
      display: none;
    }
  }
  .header-search {
    display: none;
  }
}

.jdwa-header {
  position: fixed;
  width: 100%;
  height: $header-height;
  line-height: $header-height;
  padding: $header-padding;
  margin: $header-margin;
  background-color: $theme-header-color;
  box-shadow: $box-shadow;
  box-sizing: border-box;
  z-index: 100;
  display: flex;
  white-space: nowrap;
  flex-wrap: nowrap;
}

/* LOGO */
.header-logo {
  font-size: $font-size-logo;
  font-weight: bold;
  cursor: pointer;
  .icon {
    @include icon(1.9rem, $color-black);
    vertical-align: middle;
  }
  span {
    margin-left: 1rem;
  }
}

.jdwa-header-nav {
  flex: 1;
}

/*搜索输入框*/
.header-search {
  margin: 0 20px;
  width: 100%;
  &::v-deep input {
    text-indent: 5px;
    max-width: $header-search-max-width;
    min-width: $header-search-min-width;
    border-radius: $header-search-radius;
    box-shadow: none;
    background-color: $color-light-grey;
    color: $color-black;
  }
}

/*用户*/
.user-wrap {
  position: relative;
  display: flex;
  align-items: center;

  .user {
    width: $header-user-width;
    height: $header-user-width;
    border-radius: 50%;
    margin-right: $header-user-margin;
    cursor: pointer;
    background-color: #f0f2f5;
    transition: all 0.3s ease;
    overflow: hidden;

    &:hover {
      transform: scale(1.05);
    }

    :deep(.el-image__inner) {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    :deep(.el-image__error) {
      width: 100%;
      height: 100%;
    }
  }
}

.image-error {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f0f2f5;
  color: #909399;

  .el-icon {
    font-size: 24px;
    color: #c0c4cc;
  }
}
</style>
