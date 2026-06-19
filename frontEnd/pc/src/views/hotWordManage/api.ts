import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/hotWord'

/** 列表
 * @param {*} params
tplName,string,true,,false,模版名称
status,string,true,,false,状态(1:启用；0:停用)
 */
export const getList = (data) => get(`${service}/findYjaiscrmHotWord`, data)

/** 详情
 * @param {*} params
 */
export const getDetail = (id) => get(`${service}/findSvipGroupTplById/${id}`)

/** 添加更新
 * @param {Object} data
{
    "id": "string",
    "categoryId": "string",
    "hotWord": "string",
    "nearHotWord": "string"
}
 */
export const save = (data) => post(`${service}/saveOrUpdate`, data)

/** 删除
 * @param {*} ids
 */
export const del = (ids) => _del(`${service}/${ids}`)

//
const service1 = '/analysisHotWord'

// 头部tab
export const getStatistic = () => get(`${service1}/findHotWordTab`)

/** 热词top5
 * @param data
startTime,string,false,,,开始时间
endTime,string,false,,,结束时间
 */
export const hotWordTop5 = (data) => get(`${service1}/hotWordTop5`, data)

/** 热词分类top5
 * @param data
categoryId,string,false,,,分类id
startTime,string,false,,,开始时间
endTime,string,false,,,结束时间
 */
export const hotWordCategoryTop5 = (data) => get(`${service1}/hotWordCategoryTop5`, data)

/** 获取热词讨论明细
 * @param data
categoryId,string,false,,,分类id
hotWordId,string,false,,,热词id
startTime,string,false,,,开始时间
endTime,string,false,,,结束时间
pageNum,number,false,,,当前页从1开始
pageSize,number,false,,,每页显示条数
 */
export const findAll = (data) => get(`${service1}/findAll`, data)


/**
 * 
 * @returns ai热词分析
 */
export const aiHotWordAnalysis = (data) => get(`${service1}/aiHotWordAnalysis`,data)
