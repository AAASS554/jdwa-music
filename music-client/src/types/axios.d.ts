import axios from 'axios'

declare module 'axios' {
  export interface AxiosRequestConfig {
    retry?: number;
    retryDelay?: number;
    __retryCount?: number;
  }

  export interface AxiosDefaults {
    retry?: number;
    retryDelay?: number;
    shouldRetry?: (error: any) => boolean;
  }
}

export default axios; 