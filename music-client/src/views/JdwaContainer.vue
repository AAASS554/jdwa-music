<template>
  <el-container>
    <el-header>
      <jdwa-header></jdwa-header>
    </el-header>
    <el-main>
      <router-view />
      <jdwa-footer></jdwa-footer>
      <jdwa-play-bar></jdwa-play-bar>
      <jdwa-scroll-top></jdwa-scroll-top>
      <jdwa-audio></jdwa-audio>
      <jdwa-current-play></jdwa-current-play>
    </el-main>
    <el-footer>
      <jdwa-footer></jdwa-footer>
    </el-footer>
  </el-container>
</template>

<script lang="ts" setup>
import { getCurrentInstance, onBeforeMount, ref, onUnmounted, provide } from "vue";
import JdwaHeader from "@/components/layouts/JdwaHeader.vue";
import JdwaCurrentPlay from "@/components/layouts/JdwaCurrentPlay.vue";
import JdwaPlayBar from "@/components/layouts/JdwaPlayBar.vue";
import JdwaScrollTop from "@/components/layouts/JdwaScrollTop.vue";
import JdwaFooter from "@/components/layouts/JdwaFooter.vue";
import JdwaAudio from "@/components/layouts/JdwaAudio.vue";

const { proxy } = getCurrentInstance();

// 添加用户交互状态
const hasInteracted = ref(false);

// 监听用户交互
const handleUserInteraction = () => {
  hasInteracted.value = true;
  // 移除事件监听
  document.removeEventListener('click', handleUserInteraction);
  document.removeEventListener('keydown', handleUserInteraction);
  document.removeEventListener('touchstart', handleUserInteraction);
};

// 添加用户交互事件监听
document.addEventListener('click', handleUserInteraction);
document.addEventListener('keydown', handleUserInteraction);
document.addEventListener('touchstart', handleUserInteraction);

onBeforeMount(() => {
  const dataStore = sessionStorage.getItem("dataStore");
  if (dataStore) {
    const state = JSON.parse(dataStore);
    proxy.$store.replaceState(
      Object.assign({}, proxy.$store.state, state)
    );
  }
});

// 使用防抖来保存状态，避免频繁写入
let saveTimeout: any = null;
const saveState = () => {
  if (saveTimeout) clearTimeout(saveTimeout);
  saveTimeout = setTimeout(() => {
    sessionStorage.setItem("dataStore", JSON.stringify(proxy.$store.state));
  }, 1000);
};

// 监听状态变化
window.addEventListener("beforeunload", saveState);

// 在组件卸载时清理事件监听
onUnmounted(() => {
  document.removeEventListener('click', handleUserInteraction);
  document.removeEventListener('keydown', handleUserInteraction);
  document.removeEventListener('touchstart', handleUserInteraction);
  window.removeEventListener("beforeunload", saveState);
});

// 将交互状态提供给子组件
provide('hasInteracted', hasInteracted);
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.el-container {
  min-height: calc(100% - 60px);
}
.el-header {
  padding: 0;
}
.el-main {
  padding-left: 0;
  padding-right: 0;
}
</style>
