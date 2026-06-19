import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/category'

/** 列表
 */
export const getList = (mediaType) => get(`${service}/findYjaiscrmCategory`, { mediaType })

/** 详情
 * @param {*} params
 */
export const getDetail = (id) => get(`${service}/findSvipGroupTplById/${id}`)

/** 新增或更新
 * @param {Object} data
{
    "id": "string",
    "name": "string",
    "mediaType": 0 // 默认传7
}
 */
export const save = (data) => post(`${service}/saveOrUpdate`, data)

/** 删除
 * @param {*} ids
 */
export const del = (ids) => _del(`${service}/${ids}`)
