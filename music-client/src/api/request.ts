import axios, { AxiosError } from "axios";
import router from "@/router";

const BASE_URL = process.env.NODE_HOST;

// 创建自定义axios实例
const instance = axios.create({
  baseURL: BASE_URL,
  timeout: 15000, // 增加超时时间到15秒
  withCredentials: true, // true允许跨域
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
});

// 请求重试配置
const MAX_RETRIES = 3;
const RETRY_DELAY = 1000;

// 请求拦截器
instance.interceptors.request.use(
  config => {
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
    // 否则的话抛出错误
    console.log('Response:', response);
    if (response && response.status === 200) {
      return Promise.resolve(response);
    } else {
      return Promise.reject(response);
    }
  },
  async (error: AxiosError) => {
    console.log('Error:', error);
    
    const config = error.config;
    if (!config) {
      return Promise.reject(error);
    }

    // 判断是否需要重试
    const shouldRetry = (error: AxiosError) => {
      return error.code === 'ECONNABORTED' || 
             error.message.includes('timeout') || 
             error.message.includes('Network Error');
    };

    config.__retryCount = config.__retryCount || 0;

    if (config.__retryCount >= MAX_RETRIES || !shouldRetry(error)) {
      if (error.response?.status) {
        switch (error.response.status) {
          // 401: 未登录
          case 401:
            router.replace({
              path: "/",
              query: {},
            });
            break;
          case 403:
            setTimeout(() => {
              router.replace({
                path: "/",
                query: {},
              });
            }, 1000);
            break;
          case 404:
            break;
        }
        return Promise.reject(error.response);
      }
      return Promise.reject(error);
    }

    config.__retryCount += 1;

    // 延时重试
    await new Promise(resolve => {
      setTimeout(resolve, RETRY_DELAY);
    });

    // 重新发起请求
    return instance(config);
  }
);

export function getBaseURL() {
  return BASE_URL;
}

/**
 * 封装get方法
 * @param url
 * @param params
 * @returns {Promise}
 */
export function get(url: string, params?: object) {
  return new Promise((resolve, reject) => {
    instance.get(url, { params }).then(
      (response) => resolve(response.data),
      (error) => reject(error)
    );
  });
}

/**
 * 封装post请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export function post(url: string, data = {}) {
  return new Promise((resolve, reject) => {
    instance.post(url, data).then(
      (response) => resolve(response.data),
      (error) => reject(error)
    );
  });
}

/**
 * 封装delete请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export function deletes(url: string, data = {}) {
  return new Promise((resolve, reject) => {
    instance.delete(url, { data }).then(
      (response) => resolve(response.data),
      (error) => reject(error)
    );
  });
}

/**
 * 封装put请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export function put(url: string, data = {}) {
  return new Promise((resolve, reject) => {
    instance.put(url, data).then(
      (response) => resolve(response.data),
      (error) => reject(error)
    );
  });
}
