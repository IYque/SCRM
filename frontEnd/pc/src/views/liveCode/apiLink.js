import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/yjaiscrm/shortLink'

/**
 * 列表
 * @param {*} data
 {
 pageNum:
 pageSize:
 type:''
 }
 */
export const getList = (data) => get(`${serve}/findYjaiscrmShortLink`, data)

// 详情
export const getDetail = (id) => get(`${serve}/findYjaiscrmUserCode/${id}`)

// 删除
export const del = (ids) => delt(`${serve}/${ids}`)

/**
 * 新增
 * @param {*} data
 * id	integer	主键为id且自增
 codeName	string	名称
 userId	string	员工id,多个使用逗号隔开
 userName	string	员工名称,多个使用逗号隔开
 skipVerify	boolean	是否免验证:true:免验证 false:需验证
 isExclusive	boolean	是否可重复添加: true:可重复添加 false:不可重复添加
 tagId	string	标签id,多个使用逗号隔开
 tagName	string	标签名,多个使用逗号隔开
 weclomeMsg	string	欢迎语
 codeState	string	渠道标识
 codeUrl	string	活码地址
 configId	string	联系方式的配置id
 createTime	string	创建时间
 updateTime	string	更新时间
 delFlag	integer	是否删除标识
 * @returns
 */
export const add = (data) => post(`${serve}/save`, data)

// 修改
export function update(data) {
    return put(`${serve}/update`, data)
}

export const findYjaiscrmMsgAnnexByMsgId = (id) => get(`${serve}/findYjaiscrmMsgAnnexByMsgId/${id}`)
export const findYjaiscrmMsgPeriodAnnexByMsgId = (id) => get(`${serve}/findYjaiscrmMsgPeriodAnnexByMsgId/${id}`)

//获取所有活码id与name
export const findYjaiscrmUserCodeKvs = () => get(`${serve}/findYjaiscrmUserCodeKvs`)

export const countTotalTab = (data) => get(`${serve}/countTotalTab`, data)

export const countTrend = (data) => get(`${serve}/countTrend`, data)

/**
 * 同步获客外链
 * @returns
 */
export const synchShortLink = () => post(`${serve}/synchShortLink`)

/**
 * 同步指定的获客外链
 * @param {string} linkIds 获客外链ID列表，用逗号分隔
 * @returns
 */
export const synchShortLinkByLinkIds = (linkIds) => post(`${serve}/synchShortLinkByLinkIds`, null, { params: { linkIds } })

/**
 * 获取所有获客外链的configId列表
 * @returns
 */
export const getShortLinkConfigIds = () => get(`${serve}/getShortLinkConfigIds`)

/**
 * 同步员工活码（联系我配置）
 * @returns
 */
export const synchUserCode = () => post(`/yjaiscrm/userCode/synchUserCode`)

/**
 * 同步指定的员工活码配置
 * @param {string} configIds 配置ID列表，用逗号分隔
 * @returns
 */
export const synchUserCodeByConfigIds = (configIds) => post(`/yjaiscrm/userCode/synchUserCodeByConfigIds`, null, { params: { configIds } })

/**
 * 获取所有员工活码的configId列表
 * @returns
 */
export const getUserCodeConfigIds = () => get(`/yjaiscrm/userCode/getUserCodeConfigIds`)

/**
 * 获取获客链接的客户列表
 * @param {string} linkId 获客链接ID
 * @param {number} limit 返回的最大记录数，默认100
 * @param {string} cursor 用于分页查询的游标
 * @param {object} searchParams 搜索参数 { customerName, followUser, startDate, endDate }
 * @returns
 */
export const getCustomerList = (linkId, limit = 100, cursor, searchParams = {}) => {
    const params = { linkId, limit }
    if (cursor) {
        params.cursor = cursor
    }

    // 添加搜索参数
    if (searchParams.customerName) {
        params.customerName = searchParams.customerName
    }
    if (searchParams.followUser) {
        params.followUser = searchParams.followUser
    }
    if (searchParams.startDate) {
        params.startDate = searchParams.startDate
    }
    if (searchParams.endDate) {
        params.endDate = searchParams.endDate
    }

    return get(`${serve}/getCustomerList`, params)
}
