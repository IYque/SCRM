/**
 * 环境变量
 */
const envs = {
  development: {
    DOMAIN: 'http://127.0.0.1:8085', // 站点域名，会根据此处域名判断应用环境
    BASE_URL: '/openmobile/', // 路由基础路径
    BASE_API: 'http://127.0.0.1:8085', // 接口基础路径
  },
  production: {
    DOMAIN: 'https://yjaiscrm.cn',
    BASE_URL: '/openmobile/',
    BASE_API: 'https://yjaiscrm.cn/yjaiscrm',
  },
}

const packMode = globalThis.MODE || import.meta.env.MODE
const mode =
  process.env.NODE_ENV == 'development' || !globalThis.document
    ? packMode // 本地开发和vite中使用
    : Object.keys(envs).find((e) => envs[e].DOMAIN === window?.location.origin) || 'diy' // 打包后，根据访问域名动态判断环境

const BASE_URL = envs[packMode].BASE_URL
const env = envs[mode] || {}

// 配置项
export const config = {
  ...env,
  SYSTEM_NAME: 'yjaiscrm', // 系统简称
}
Object.assign(config, { BASE_URL, RUN_ENV: mode })
