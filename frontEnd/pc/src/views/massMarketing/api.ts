import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/groupMsg'

/** 列表
 * @param {*} params
tplName,string,true,,false,模版名称
status,string,true,,false,状态(1:启用；0:停用)
 */
export const getList = (data) => get(`${service}/findYjaiscrmGroupMsgPage`, data)

/** 详情
 * @param {*} params
 */
export const getDetail = (id) => get(`${service}/findYjaiscrmGroupMsgById/${id}`)

/** 删除
 * @param {*} ids
 */
export const del = (ids) => _del(`${service}/${ids}`)

/** save
 * @returns
 */
export const save = (data) => post(`${service}/buildGroupMsg`, data)

// 建群统计-头部tab
export const getStatistic = (svipGroupId) => get(`${service}/countTotalTab`, { svipGroupId })

// 建群统计-折线图
export const getDataTrend = (data) => get(`${service}/countTrend`, data)
