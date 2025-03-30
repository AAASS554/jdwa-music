import { createStore } from "vuex";
import configure from "./configure";
import user from "./user";
import song from "./song";

export interface Song {
  id: number
  name: string
  artist: string
  url: string
  cover: string
  duration: number
}

interface State {
  currentSong: Song | null
  playlist: Song[]
  isPlaying: boolean
  currentTime: number
}

export default createStore({
  modules: {
    configure,
    user,
    song,
  },
});
