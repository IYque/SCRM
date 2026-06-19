import request from '@/utils/request'
const { get, post, put, del: _del } = request
const serve = '/yjaiscrmComplaint'

/**
 * 列表
 * @param {*} data
   {
    page:
    size:
    handleState:1  处理状态(1:未处理;2:已处理)
   }
 */
export function getList(data) {
  return get(`${serve}/findComplaintByPage`, data)
}

export const getDetail = (id) => get(`${serve}/findYjaiscrmComplainById/${id}`)

/**
 * 处理投诉意见
 * @param {*} data
{
    "id": "string",
    "handleContent": "string",
    "complainAnnexList": [
        {
            "annexUrl": "附件地址"
        }
    ]
}
 * @returns
 */
export function handleComplaint(data) {
  return post(`${serve}/handleComplaint`, data)
}

export function addComplaint(data) {
  return post(`${serve}/addComplaint`, data)
}

// 获取投诉类型列表
export function findComplaint(data) {
  return get(`${serve}/findComplaint`)
}
