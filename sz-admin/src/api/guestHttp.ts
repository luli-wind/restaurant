import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios';

import { checkStatus, CODE_SUCCESS } from '@/api/helper';
import type { IResultData } from '@/api/types';
import { ElMessage } from 'element-plus';

export interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  loading?: boolean;
  cancel?: boolean;
}

const config = {
  // 默认地址请求地址，可在 .env.** 文件中修改
  baseURL: import.meta.env.VITE_API_URL as string,
  // 设置超时时间，默认超时时间60s
  timeout: import.meta.env.VITE_APP_HTTP_TIMEOUT || 60000
  // 跨域时候允许携带凭证
  // withCredentials: true
};

class GuestRequestHttp {
  instance: AxiosInstance;

  constructor(config: AxiosRequestConfig) {
    // instantiation
    this.instance = axios.create(config);

    /**
     * @description 请求拦截器
     * 客户端发送请求 -> [请求拦截器] -> 服务器
     * 访客请求不需要token
     */
    this.instance.interceptors.request.use(
      (config: CustomAxiosRequestConfig) => {
        // 访客请求不添加Authorization头
        return config;
      },
      error => {
        return Promise.reject(error);
      }
    );

    /**
     * @description 响应拦截器
     *  服务器换返回信息 -> [拦截统一处理] -> 客户端JS获取到信息
     */
    this.instance.interceptors.response.use(
      response => {
        const { data } = response;
        // 如果是文件流，直接返回整个响应对象
        if (response.config.responseType === 'blob') {
          return response;
        }

        // 全局错误信息拦截（防止下载文件的时候返回数据流，没有 code 直接报错）
        if (data.code && data.code !== CODE_SUCCESS) {
          ElMessage.error({ message: data.message, dangerouslyUseHTMLString: true });
          return Promise.reject(data);
        }
        // 成功请求（在页面上除非特殊情况，否则不用处理失败逻辑）
        return data;
      },
      async error => {
        const { response } = error;
        // 请求超时 && 网络错误单独判断，没有 response
        if (error.message.indexOf('timeout') !== -1) {
          ElMessage.error('请求超时！请您稍后重试');
        }
        if (error.message.indexOf('Network Error') !== -1) {
          ElMessage.error('网络错误！请您稍后重试');
        }
        // 根据服务器响应的错误状态码，做不同的处理
        if (response) {
          checkStatus(response?.status, response?.data?.message);
        }
        // 服务器结果都没有返回(可能服务器错误可能客户端断网)，断网处理:可以跳转到断网页面
        if (!window.navigator.onLine) {
          // 访客模式下不跳转到错误页面
        }
        return Promise.reject(error);
      }
    );
  }

  /**
   * @description 常用请求方法封装
   */
  get<T>(url: string, params: any = {}, config?: AxiosRequestConfig<any> | undefined): Promise<IResultData<T>> {
    return this.instance.get(url, { params, ...config });
  }

  post<T>(url: string, params: any = {}, config?: AxiosRequestConfig<any> | undefined): Promise<IResultData<T>> {
    return this.instance.post(url, params, config);
  }

  put<T>(url: string, params: any = {}, config?: AxiosRequestConfig<any> | undefined): Promise<IResultData<T>> {
    return this.instance.put(url, params, config);
  }

  delete<T>(url: string, data: any = {}, config?: AxiosRequestConfig<any> | undefined): Promise<IResultData<T>> {
    return this.instance.delete(url, { data, ...config });
  }

  download(url: string, params = {}, config?: AxiosRequestConfig<any> | undefined): Promise<BlobPart> {
    return this.instance.post(url, params, { ...config, responseType: 'blob' });
  }

  template(url: string, params = {}, config?: AxiosRequestConfig<any> | undefined): Promise<BlobPart> {
    return this.instance.get(url, { params, ...config, responseType: 'blob' });
  }

  upload<T>(url: string, params: any = {}, config?: AxiosRequestConfig<any> | undefined): Promise<IResultData<T>> {
    return this.instance.post(url, params, {
      ...config,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
}

export default new GuestRequestHttp(config);