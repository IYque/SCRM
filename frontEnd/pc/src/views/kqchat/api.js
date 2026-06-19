import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/msg'

/**
 * 列表
 * @param {*} data
{
  pageNum:
  pageSize:
  type:''
 }
 */
export const getList = (data) => get(`${serve}/findMsgAuditByPage`, data)


/**
 * AI预审列表
 * @param {*} data 
 * @returns 
 */
export const findAiAnalysisMsgAudits = (data) => get(`${serve}/findAiAnalysisMsgAudits`, data)


/**
 * AI预审规则列表
 * @param {*} data 
 * @returns 
 */
export const findYjaiscrmMsgRules = (data) => get(`${serve}/findYjaiscrmMsgRules`, data)



/**
 * 新增或编辑ai预审规则
 * @param {*} data 
 * @returns 
 */

export const saveOrUpdateMsgRule = (data) => post(`${serve}/saveOrUpdateMsgRule`, data)


/**
 * 会话同步
 * @returns 
 */
export const synchMsg = () => get(`${serve}/synchMsg`)



/**
 * 
 * @returns ai会话预审
 */
export const buildAISessionWarning = (data) => get(`${serve}/buildAISessionWarning`,data)



// 删除
export const del = (ids) => delt(`${serve}/${ids}`)


//启用或停用

export const batchStartOrStop = (ids) => post(`${serve}/${ids}`)