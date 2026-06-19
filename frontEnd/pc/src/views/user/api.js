import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/yjaiscrmUser'

/**
 * 列表
 * @param {*} data
{
  pageNum:
  pageSize:
  type:''
 }
 */
export const getList = (data) => get(`${serve}/findYjaiscrmUserPage`, data)



/**
 * 成员同步
 * @returns 
 */
export const synchYjaiscrmUser = () => post(`${serve}/synchYjaiscrmUser`)

