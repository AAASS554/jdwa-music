<template>
  <div class="play-bar" :class="{ show: !toggle }">
    <div class="fold" :class="{ turn: toggle }">
      <jdwa-icon :icon="iconList.ZHEDIE" @click="toggle = !toggle"></jdwa-icon>
    </div>
    <!--播放进度-->
    <el-slider class="progress" v-model="nowTime" @change="changeTime" size="small"></el-slider>
    <div class="control-box">
      <div class="info-box">
        <!--歌曲图片-->
      <div @click="goPlayerPage">
         <el-image class="song-bar-img" fit="contain"/>
      </div>
        <!--播放开始结束时间-->
        <div v-if="songId">
          <div class="song-info">{{ this.songTitle }} - {{ this.singerName }}</div>
          <div class="time-info">{{ startTime }} / {{ endTime }}</div>
        </div>
      </div>
      <div class="song-ctr">
        <jdwa-icon class="jdwa-play-show" :icon="playStateList[playStateIndex]" @click="changePlayState"></jdwa-icon>
        <!--上一首-->
        <jdwa-icon class="jdwa-play-show" :icon="iconList.SHANGYISHOU" @click="prev"></jdwa-icon>
        <!--播放-->
        <jdwa-icon :icon="playBtnIcon" @click="togglePlay"></jdwa-icon>
        <!--下一首-->
        <jdwa-icon class="jdwa-play-show" :icon="iconList.XIAYISHOU" @click="next"></jdwa-icon>
        <!--音量-->
        <el-dropdown class="jdwa-play-show" trigger="click">
          <jdwa-icon v-if="volume !== 0" :icon="iconList.YINLIANG"></jdwa-icon>
          <jdwa-icon v-else :icon="iconList.JINGYIN"></jdwa-icon>
          <template #dropdown>
            <el-dropdown-menu>
              <el-slider class="jdwa-slider" style="height: 150px; margin: 10px 0" v-model="volume"
                         :vertical="true"></el-slider>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="song-ctr song-edit">
        <!--收藏-->
        <jdwa-icon
            class="jdwa-play-show"
            :class="{ active: isCollection }"
            :icon="isCollection ? iconList.like : iconList.dislike"
            @click="changeCollection"
        ></jdwa-icon>
        <!--下载-->
        <jdwa-icon
            class="jdwa-play-show"
            :icon="iconList.download"
            @click="
            downloadMusic({
              songUrl,
              songName: singerName + '-' + songTitle,
            })
          "
        ></jdwa-icon>
        <!--歌曲列表-->
        <jdwa-icon :icon="iconList.LIEBIAO" @click="changeAside"></jdwa-icon>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {computed, defineComponent, getCurrentInstance, onMounted, ref, watch} from "vue";
import {mapGetters, useStore} from "vuex";
import mixin from "@/mixins/mixin";
import {HttpManager} from "@/api";
import {formatSeconds} from "@/utils";
import {Icon, RouterName} from "@/enums";
import JdwaIcon from './JdwaIcon.vue'

export default defineComponent({
  components: {
    JdwaIcon
  },
  setup() {
    const {proxy} = getCurrentInstance();
    const store = useStore();
    const {routerManager, playMusic, checkStatus, downloadMusic} = mixin();

    const isCollection = ref(false); // 是否收藏

    const userIdVO = computed(() => store.getters.userId);
    const songIdVO = computed(() => store.getters.songId);
    const token = computed(() => store.getters.token);

    watch(songIdVO, () => {
      initCollection();
    });
    watch(token, (value) => {
      if (!value) isCollection.value = false;
    });

    async function initCollection() {
      if (!checkStatus(false)) return;

      const userId = userIdVO.value;
      const type = '0';
      const songId = songIdVO.value;
      isCollection.value = ((await HttpManager.isCollection({userId, type, songId})) as ResponseBody).data;
    }

    async function changeCollection() {
      if (!checkStatus()) return;

      const userId = userIdVO.value;
      const type = '0'; //这里要看看 不能直接写死
      const songId = songIdVO.value;

      const result = isCollection.value
          ? ((await HttpManager.deleteCollection(userIdVO.value, songIdVO.value)) as ResponseBody)
          : ((await HttpManager.setCollection({userId, type, songId})) as ResponseBody);
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.data == true || result.data == false) isCollection.value = result.data;
    }

    onMounted(() => {
      if (songIdVO.value) initCollection();
    });

    return {
      isCollection,
      playMusic,
      routerManager,
      checkStatus,
      attachImageUrl: HttpManager.attachImageUrl,
      changeCollection,
      downloadMusic
    };
  },
  data() {
    return {
      startTime: "00:00",
      endTime: "00:00",
      nowTime: 0, // 进度条的位置
      toggle: true,
      volume: 50,
      playState: Icon.XUNHUAN,
      playStateList: [Icon.XUNHUAN, Icon.LUANXU],
      playStateIndex: 0,
      iconList: {
        download: Icon.XIAZAI,
        ZHEDIE: Icon.ZHEDIE,
        SHANGYISHOU: Icon.SHANGYISHOU,
        XIAYISHOU: Icon.XIAYISHOU,
        YINLIANG: Icon.YINLIANG1,
        JINGYIN: Icon.JINGYIN,
        LIEBIAO: Icon.LIEBIAO,
        dislike: Icon.Dislike,
        like: Icon.Like,
      },
    };
  },
  computed: {
    ...mapGetters([
      "userId",
      "isPlay", // 播放状态
      "playBtnIcon", // 播放状态的图标
      "songId", // 音乐id
      "songUrl", // 音乐地址
      "songTitle", // 歌名
      "singerName", // 歌手名
      "songPic", // 歌曲图片
      "curTime", // 当前音乐的播放位置
      "duration", // 音乐时长
      "currentPlayList",
      "currentPlayIndex", // 当前歌曲在歌曲列表的位置
      "showAside", // 是否显示侧边栏
      "autoNext", // 用于触发自动播放下一首
    ]),
  },
  watch: {
    // 切换播放状态的图标
    isPlay(value) {
      this.$store.commit("setPlayBtnIcon", value ? Icon.ZANTING : Icon.BOFANG);
    },
    volume() {
      this.$store.commit("setVolume", this.volume / 100);
    },
    // 播放时间的开始和结束
    curTime() {
      this.startTime = formatSeconds(this.curTime);
      this.endTime = formatSeconds(this.duration);
      // 移动进度条
      this.nowTime = (this.curTime / this.duration) * 100;
    },
    // 自动播放下一首
    autoNext() {
      this.next();
    },
  },
  methods: {
    changeAside() {
      this.$store.commit("setShowAside", !this.showAside);
    },
    // 控制音乐播放 / 暂停
    togglePlay() {
      this.$store.commit("setIsPlay", this.isPlay ? false : true);
    },
    changeTime() {
      this.$store.commit("setChangeTime", this.duration * (this.nowTime * 0.01));
    },
    changePlayState() {
      this.playStateIndex = this.playStateIndex >= this.playStateList.length - 1 ? 0 : ++this.playStateIndex;
      this.playState = this.playStateList[this.playStateIndex];
    },
    // 上一首
    prev() {
      if (this.playState === Icon.LUANXU) {
        let playIndex = Math.floor(Math.random() * this.currentPlayList.length);
        playIndex = playIndex === this.currentPlayIndex ? playIndex + 1 : playIndex;
        this.$store.commit("setCurrentPlayIndex", playIndex);
        this.toPlay(this.currentPlayList[playIndex].url);
      } else if (this.currentPlayIndex !== -1 && this.currentPlayList.length > 1) {
        if (this.currentPlayIndex > 0) {
          this.$store.commit("setCurrentPlayIndex", this.currentPlayIndex - 1);
          this.toPlay(this.currentPlayList[this.currentPlayIndex].url);
        } else {
          this.$store.commit("setCurrentPlayIndex", this.currentPlayList.length - 1);
          this.toPlay(this.currentPlayList[this.currentPlayIndex].url);
        }
      }
    },
    // 下一首
    next() {
      if (this.playState === Icon.LUANXU) {
        let playIndex = Math.floor(Math.random() * this.currentPlayList.length);
        playIndex = playIndex === this.currentPlayIndex ? playIndex + 1 : playIndex;
        this.$store.commit("setCurrentPlayIndex", playIndex);
        this.toPlay(this.currentPlayList[playIndex].url);
      } else if (this.currentPlayIndex !== -1 && this.currentPlayList.length > 1) {
        if (this.currentPlayIndex < this.currentPlayList.length - 1) {
          this.$store.commit("setCurrentPlayIndex", this.currentPlayIndex + 1);
          this.toPlay(this.currentPlayList[this.currentPlayIndex].url);
        } else {
          this.$store.commit("setCurrentPlayIndex", 0);
          this.toPlay(this.currentPlayList[0].url);
        }
      }
    },
    // 选中播放
    toPlay(url) {
      if (url && url !== this.songUrl) {
        const song = this.currentPlayList[this.currentPlayIndex];
        this.playMusic({
          id: song.id,
          url,
          pic: song.pic,
          index: this.currentPlayIndex,
          name: song.name,
          lyric: song.lyric,
          currentSongList: this.currentPlayList,
        });
      }
    },
    goPlayerPage() {
      this.routerManager(RouterName.Lyric, {path: `${RouterName.Lyric}/${this.songId}`});
    },
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/jdwa-play-bar.scss";
</style>
