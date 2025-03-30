<template>
  <jdwa-header></jdwa-header>
  <jdwa-aside></jdwa-aside>
  <div class="content-box" :class="{ 'content-collapse': collapse }">
    <router-view></router-view>
  </div>
  <jdwa-audio></jdwa-audio>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import JdwaHeader from "@/components/layouts/JdwaHeader.vue";
import JdwaAudio from "@/components/layouts/JdwaAudio.vue";
import JdwaAside from "@/components/layouts/JdwaAside.vue";
import emitter from "@/utils/emitter";

const collapse = ref(false);
emitter.on("collapse", (msg) => {
  collapse.value = msg as boolean;
});
</script>

<style scoped>
.content-box {
  position: absolute;
  left: 150px;
  right: 0;
  top: 60px;
  bottom: 0;
  overflow-y: scroll;
  transition: left 0.3s ease-in-out;
  padding: 20px;
}

.content-collapse {
  left: 65px;
}
</style>
