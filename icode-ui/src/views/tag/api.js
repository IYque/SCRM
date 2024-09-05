import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/iYqueTag'

/**
 * 列表
 * @param {*} data
{
  pageNum:
  pageSize:
  type:''
 }
 */
export const getList = (data) => get(`${serve}/findIYqueTag`, data)

// 详情
export const getDetail = (id) => get(`${serve}/getKeyWordGroupBaseInfo/${id}`)

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
