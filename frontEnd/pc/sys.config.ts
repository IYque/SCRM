// 环境变量
const envs = {
  development: {
    DOMAIN: 'http://127.0.0.1:8085',
    BASE_URL: '/tools/',
    BASE_API: 'http://127.0.0.1:8085',
  },
  test: {
    DOMAIN: 'https://show.yjaiscrm.cn',
    BASE_URL: '/tools/',
    BASE_API: 'https://show.yjaiscrm.cn/yjaiscrm',
  },
  production: {
    DOMAIN: 'https://yjaiscrm.cn',
    BASE_URL: '/tools/',
    BASE_API: 'https://yjaiscrm.cn/yjaiscrm',
  },
}

let mode =
  process.env.NODE_ENV == 'development' || !globalThis.document
    ? process.env.VUE_APP_ENV
    : Object.keys(envs).find((e) => envs[e].DOMAIN === window?.location.origin)

export const env = { ...envs[mode], ENV: mode }

// 系统常量配置
export const common = {
  SYSTEM_NAME: 'yjaiscrm', // 系统简称
  SYSTEM_SLOGAN:
    '<a href="https://www.yjaiscrm.cn?utm_source=yjaiscrmcode" target="_blank">yjaiscrm Scrm-是基于Java源码交付的企微SCRM,帮助企业构建高度自由安全的私域平台.</a> ', // 系统标语
  COPYRIGHT: 'Copyright © 2022-2025 yjaiscrm All Rights Reserved.', // 版权信息
  LOGO: env.BASE_URL + 'static/logo.png', // 深色logo
}
