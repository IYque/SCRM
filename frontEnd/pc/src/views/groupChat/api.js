import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/yjaiscrmChat'
const tagService = '/yjaiscrmTag'

/**
 * 列表
 * @param {*} data
{
  pageNum:
  pageSize:
  type:''
 }
 */
export const getList = (data) => get(`${serve}/findYjaiscrmChatPage`, data)



/**
 * 客群同步
 * @returns 
 */
export const synchYjaiscrmChat = () => post(`${serve}/synchYjaiscrmChat`)

/**
 * 获取客群标签列表
 * @returns
 */
export const getGroupTags = (data) => get(`${tagService}/findYjaiscrmTag`, { ...data, groupTagType: 2 })

/**
 * 为客群打标签
 * @param {*} params
 * externalUserid: 客群的msgId
 * tagIds: 标签ID数组
 */
export const tagGroups = (data) => post(`${serve}/tagGroups`, data)

