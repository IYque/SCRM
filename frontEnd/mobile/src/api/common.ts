import request from '@/utils/request'
const service = window.sysConfig.services.wecom
const weChat = window.sysConfig.services.weChat
// import { decryptAES } from '@/utils/jsencrypt'

export function upload(data) {
  return request({
    url: '/file/openUpload',
    method: 'POST',
    data,
  })
}

// 获取文件服务器配置
export function getCosConfig(data) {
  return request({
    url: '/file/get/config',
    params: data,
  }).then(({ data }) => {
    const res = {}
    Object.keys(data).forEach((key) => {
      res[key] = decryptAES(data[key])
    })
    return res
  })
}

// 获取临时素材media_id
export function getMaterialMediaId(params) {
  return request({
    url: '/yjaiscrmSys/uploadMediaId',
    params,
  })
}

/**
 * 获取应用的jsapi_ticket
 * @param {*} url 页面url
 */
export function getAgentTicket(url) {
  return request({
    url: '/yjaiscrmSys/getAgentTicket',
    params: {
      url,
    },
  })
}

export function getWxTicket(data) {
  return request({
    url: weChat + '/ticket/getWxTicket',
    params: {
      url: data,
    },
  })
}

// /**
//  * 获取企业的jsapi_ticket
//  * @param {*} url 页面url
//  */
// export function getAppTicket(url) {
//   return request({
//     url: service + '/ticket/getAppTicket',
//     params: {
//       url,
//       appType: 1,
//     },
//   })
// }

/**
 * 企业微信端登录 / 获取token接口
 * @param {*} authCode
 */
export function login(authCode) {
  return request({
    url: '/yjaiscrmSys/weComLogin',
    method: 'get',
    params: {
      authCode,
    },
  })
}

/**
 * 企微端获取登录用户信息
 */
export function getUserInfo() {
  return request({
    url: '/yjaiscrmSys/getBaseInfo',
  })
}

/**
 * 获取微信授权链接
 * @param {*} redirectUrl 回调跳转地址
 * @returns
 */
export function getWxRedirect(redirectUrl = location.href) {
  return request({
    url: '/auth/wxRedirect',
    method: 'get',
    params: {
      redirectUrl,
    },
  })
}

/**
 * 获取企业授权链接
 * @param {*} redirectUrl 回调跳转地址
 * @returns
 */
export function getWcRedirect(redirectUrl = location.href) {
  return request({
    url: '/yjaiscrmSys/weComRedirect',
    method: 'get',
    params: {
      redirectUrl,
    },
  })
}

/**
 * 微信获取token接口
 * @param {*} code 是 code码 必填
 * @param {*} openId 否 微信用户ID 非必填
 * @returns
 */
export function wxLogin(code, openId) {
  return request({
    url: '/auth/wxLogin',
    method: 'get',
    params: {
      code,
      openId,
    },
  })
}

/**
 * 获取微信授权用户信息
 * @param {*} openId
 */
export function getWechatUserInfo() {
  return request({
    url: `/system/user/getWxInfo`,
  })
}

/**
 * 行政区划
 * @param {*} param
 {
  id,integer,false,,区域ID,
parentId,integer,false,,父ID,
name,string,false,,区域名称,
ePrefix,string,false,,拼音首字母,
 }
 * @returns
 */
export function getAreaList(params) {
  return request({
    url: `/system/area/list`,
    params,
  })
}

// 获取商机阶段
export function getStageList() {
  return request({
    url: service + '/strackStage/findWeStrackStage',
  })
}

// 获取员工列表
export function getStaffAll(params) {
  return request({
    url: '/system/user/listAll',
    params,
  })
}
// 获取部门列表-客户画像
export function getDeptList(params) {
  return request({
    url: '/system/dept/list',
    params,
  })
}

// 获取员工列表-客户画像
export function getUserList(params) {
  return request({
    url: '/system/user/list',
    params,
  })
}
