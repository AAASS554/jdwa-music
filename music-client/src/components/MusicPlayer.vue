<template>
  <div class="music-player">
    <audio
      ref="audioPlayer"
      :src="currentSong?.url"
      @timeupdate="onTimeUpdate"
      @ended="onSongEnded"
      @canplay="onCanPlay"
    ></audio>
    
    <div class="player-controls">
      <el-button circle @click="previousSong">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <el-button circle class="play-btn" @click="togglePlay">
        <el-icon><component :is="isPlaying ? 'Pause' : 'VideoPlay'" /></el-icon>
      </el-button>
      <el-button circle @click="nextSong">
        <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>

    <div class="player-progress">
      <div class="song-info" v-if="currentSong">
        <img :src="attachImageUrl(currentSong.pic || store.getters.songPic)" class="cover" alt="cover" />
        <div class="info">
          <div class="song-name">{{ currentSong.name || songTitle }}</div>
          <div class="artist">{{ currentSong.artist || singerName }}</div>
        </div>
      </div>
      <el-slider
        v-model="progress"
        :show-tooltip="false"
        @change="onProgressChange"
      />
      <div class="time">
        <span>{{ formatTime(currentTime) }}</span> /
        <span>{{ formatTime(duration) }}</span>
      </div>
    </div>

    <div class="player-options">
      <el-button circle @click="togglePlaylist">
        <el-icon><List /></el-icon>
      </el-button>
      <el-button circle @click="toggleSettings">
        <el-icon><Setting /></el-icon>
      </el-button>
    </div>

    <!-- 播放列表抽屉 -->
    <el-drawer
      v-model="showPlaylist"
      title="播放列表"
      direction="rtl"
      size="300px"
    >
      <div class="playlist">
        <div
          v-for="song in playlist"
          :key="song.id"
          class="playlist-item"
          :class="{ active: currentSong?.id === song.id }"
          @click="playSong(song)"
        >
          <div class="song-info">
            <div class="name">{{ song.name }}</div>
            <div class="artist">{{ song.artist }}</div>
          </div>
          <el-button
            circle
            size="small"
            @click.stop="removeFromPlaylist(song.id)"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useStore } from 'vuex'
import {
  ArrowLeft,
  ArrowRight,
  VideoPlay,
  Pause,
  List,
  Setting,
  Delete
} from '@element-plus/icons-vue'
import type { Song } from '@/store'
import { HttpManager } from '@/api'

const store = useStore()
const { attachImageUrl } = HttpManager
const audioPlayer = ref<HTMLAudioElement | null>(null)
const showPlaylist = ref(false)
const duration = ref(0)

const songPic = computed(() => store.state.song.songPic)
const songTitle = computed(() => store.state.song.songTitle)
const singerName = computed(() => store.state.song.singerName)
const currentSong = computed(() => store.getters.currentSong || {
  id: store.getters.songId,
  name: store.getters.songTitle,
  artist: store.getters.singerName,
  url: store.getters.songUrl,
  pic: store.getters.songPic
})
const isPlaying = computed(() => store.getters.isPlay)
const playlist = computed(() => store.getters.currentPlayList)
const currentTime = computed(() => store.getters.curTime)

const progress = computed({
  get: () => (currentTime.value / duration.value) * 100 || 0,
  set: (value) => {
    if (audioPlayer.value) {
      const time = (value / 100) * duration.value
      audioPlayer.value.currentTime = time
      store.dispatch('updateTime', time)
    }
  }
})

watch(isPlaying, (newValue) => {
  if (audioPlayer.value) {
    if (newValue) {
      audioPlayer.value.play()
    } else {
      audioPlayer.value.pause()
    }
  }
})

const formatTime = (time: number) => {
  const minutes = Math.floor(time / 60)
  const seconds = Math.floor(time % 60)
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

const togglePlay = () => {
  if (isPlaying.value) {
    store.dispatch('pauseSong')
  } else {
    store.dispatch('resumeSong')
  }
}

const onTimeUpdate = () => {
  if (audioPlayer.value) {
    store.dispatch('updateTime', audioPlayer.value.currentTime)
  }
}

const onCanPlay = () => {
  if (audioPlayer.value) {
    duration.value = audioPlayer.value.duration
  }
}

const onSongEnded = () => {
  nextSong()
}

const previousSong = () => {
  const currentIndex = playlist.value.findIndex(song => song.id === currentSong.value?.id)
  const newIndex = currentIndex > 0 ? currentIndex - 1 : playlist.value.length - 1
  store.dispatch('playSong', playlist.value[newIndex])
}

const nextSong = () => {
  const currentIndex = playlist.value.findIndex(song => song.id === currentSong.value?.id)
  const newIndex = currentIndex < playlist.value.length - 1 ? currentIndex + 1 : 0
  store.dispatch('playSong', playlist.value[newIndex])
}

const playSong = (song: Song) => {
  store.dispatch('playSong', song)
}

const removeFromPlaylist = (songId: number) => {
  store.commit('removeFromPlaylist', songId)
}

const togglePlaylist = () => {
  showPlaylist.value = !showPlaylist.value
}

const toggleSettings = () => {
  // 实现设置功能
}

onMounted(() => {
  // 获取歌曲列表
  store.dispatch('fetchSongs')
})
</script>

<style lang="scss" scoped>
.music-player {
  width: 100%;
  
  .player-progress {
    display: flex;
    align-items: center;
    padding: 0 20px;
    
    .song-info {
      display: flex;
      align-items: center;
      margin-right: 20px;
      
      .cover {
        width: 40px;
        height: 40px;
        border-radius: 4px;
        margin-right: 10px;
        object-fit: cover;
      }
      
      .info {
        .song-name {
          font-size: 14px;
          font-weight: 500;
          margin-bottom: 4px;
        }
        
        .artist {
          font-size: 12px;
          color: #666;
        }
      }
    }
    
    .el-slider {
      flex: 1;
      margin: 0 20px;
    }
    
    .time {
      font-size: 12px;
      color: #666;
      min-width: 80px;
      text-align: right;
    }
  }
  
  .playlist {
    .playlist-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px;
      cursor: pointer;
      
      &:hover {
        background-color: #f5f5f5;
      }
      
      &.active {
        color: var(--el-color-primary);
      }
      
      .song-info {
        .name {
          font-size: 14px;
          margin-bottom: 4px;
        }
        
        .artist {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
}
</style> 