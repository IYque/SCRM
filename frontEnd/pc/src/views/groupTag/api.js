import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/yjaiscrmTag'

/**
 * 列表
 * @param {*} data
 */
export const getList = (data) => {
  // 添加标签分组类型(1:客户企业标签;2:客群标签)
  return get(`${serve}/findYjaiscrmTagGroups`, { ...data, groupTagType: 2 })
}

// 详情
export const getDetail = (id) => get(`${serve}/getKeyWordGroupBaseInfo/${id}`)

// 删除
export const del = (ids) => delt(`${serve}/${ids}`)

/**
 * 新增
 * @param {*} data
 * @returns
 */
export const add = (data) => {
  // 添加标签分组类型(1:客户企业标签;2:客群标签)
  return post(`${serve}`, { ...data, groupTagType: 2 })
}

// 修改
export function update(data) {
  // 添加标签分组类型(1:客户企业标签;2:客群标签)
  return put(`${serve}`, { ...data, groupTagType: 2 })
}

/**
 * AI生成标签
 * @param {*} data { prompt: string }
 * @returns
 */
export const generateTagsByAi = (data) => {
  return post(`${serve}/generateTagsByAi`, data)
}