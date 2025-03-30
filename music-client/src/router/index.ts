import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Home from '@/views/Home.vue'
import store from '@/store' // 直接导入 store 实例

const routes: Array<RouteRecordRaw> = [
  {
    path: "/:pathMatch(.*)*",
    redirect: "/404",
  },
  {
    path: "/404",
    component: () => import("@/views/error/404.vue"),
  },
  {
    path: "/",
    name: "jdwa-container",
    component: () => import("@/views/JdwaContainer.vue"), 
    children: [
      {
        path: "/",
        name: "home",
        component: Home
      },
      {
        path: "/sign-in",
        name: "sign-in",
        component: () => import("@/views/SignIn.vue"),
      },
      {
        path: "/sign-up",
        name: "sign-up",
        component: () => import("@/views/SignUp.vue"),
      },
      {
        path: "/personal",
        name: "personal",
        meta: {
          requireAuth: true,
        },
        component: () => import("@/views/personal/Personal.vue"),
      },
      {
        path: "/song-sheet",
        name: "song-sheet",
        component: () => import("@/views/song-sheet/SongSheet.vue"),
      },
      {
        path: "/song-sheet-detail/:id",
        name: "song-sheet-detail",
        component: () => import("@/views/song-sheet/SongSheetDetail.vue"),
      },
      {
        path: "/singer",
        name: "singer",
        component: () => import("@/views/singer/Singer.vue"),
      },
      {
        path: "/singer-detail/:id",
        name: "singer-detail",
        component: () => import("@/views/singer/SingerDetail.vue"),
      },
      {
        path: "/lyric/:id",
        name: "lyric",
        component: () => import("@/views/Lyric.vue"),
      },
      {
        path: "/search",
        name: "search",
        component: () => import("@/views/search/Search.vue"),
      },
      {
        path: "/personal-data",
        name: "personal-data",
        component: () => import("@/views/setting/PersonalData.vue"),
      },
      {
        path: "/FPassword",
        name: "FPassword",
        component: ()=> import("@/views/FPassword.vue"),
      },
      {
        path: "/loginByemail",
        name: "loginByemail",
        component: ()=> import("@/views/loginByemail.vue"),
      },
      {
        path: "/setting",
        name: "setting",
        meta: {
          requireAuth: true,
        },
        component: () => import("@/views/setting/Setting.vue"),
        children: [
          {
            path: "/setting/PersonalData",
            name: "personalData",
            meta: {
              requireAuth: true,
            },
            component: () => import("@/views/setting/PersonalData.vue"),
          }
        ]
      }
    ],
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

// 修改路由守卫
router.beforeEach(async (to, from, next) => {
  const dataStore = sessionStorage.getItem("dataStore");
  
  if (dataStore) {
    const state = JSON.parse(dataStore);
    // 确保用户信息存在
    if (state.user && state.user.id) {
      // 直接更新整个用户状态
      await store.commit('setUser', state.user);
      
      // 如果是需要认证的页面，确保用户已登录
      if (to.matched.some(record => record.meta.requireAuth)) {
        if (!state.user.id) {
          next({ path: '/sign-in' });
        }
      }
    }
  }
  
  next();
});

export default router;
