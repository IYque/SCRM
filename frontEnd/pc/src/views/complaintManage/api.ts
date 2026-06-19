import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/yjaiscrmComplaint'

/** 列表
 * @param {*} params
tplName,string,true,,false,模版名称
status,string,true,,false,状态(1:启用；0:停用)
 */
export const getList = (data) => get(`${service}/findComplaintByPage`, data)

/** 通知
 * @param {*} params
 */
export const distributeHandle = (id) => get(`${service}/distributeHandle/${id}`)

/** 设置通知人
 * @param {Object} data
[
    {
        "userId": "string"
    }
]
 */
export const setYjaiscrmComplaintTip = (data) => {
  return post(`${service}/setYjaiscrmComplaintTip`, data)
}

/**
 * 获取投诉通知人
 * @returns
 */
export const findYjaiscrmComplaintTips = () => get(`${service}/findYjaiscrmComplaintTips`)

/** 删除
 * @param {*} ids
 */
export const del = (ids) => _del(`${service}/${ids}`)

/** 启用或者停用
{
    "svipGroupIds": "", //一客一群模版id，多个使用逗号隔开
    "status": 0, //状态(1:启用；0:停用)
    "chatId": "", //群id
    "weUserId": "", //成员id
    "externalUserid": "" //客户id
}
 * @returns
 */
export const updateStatus = (data) => post(`${service}/updateStatus`, data)

// 建群统计-头部tab
export const getStatistic = (svipGroupId) => get(`${service}/countTotalTab`, { svipGroupId })

// 建群统计-折线图
export const getDataTrend = (data) => get(`${service}/countTrend`, data)
