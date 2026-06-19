import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/iychatCode'

/**
 * 列表
 * @param {*} data
{
  pageNum:
  pageSize:
  type:''
 }
 */
export const getList = (data) => get(`${serve}/findYjaiscrmChatCode`, data)

// 详情
// export const getDetail = (id) => get(`${serve}/findYjaiscrmUserCode/${id}`)

// // 删除
export const del = (ids) => delt(`${serve}/${ids}`)

/**
 * 新增
 * @param {*} data
id	integer
chatCodeName	string	群码名称
chatCodeUrl	string	群码url
chatCodeState	string	群码渠道标识
configId	string	群码config
remark	string	联系方式的备注信息，用于助记，超过30个字符将被截断
autoCreateRoom	integer	当群满了后，是否自动新建群。0-否；1-是。 默认为1
roomBaseName	string	自动建群的群名前缀，当auto_create_room为1时有效。最长40个utf8字符
roomBaseId	integer	自动建群的群起始序号，当auto_create_room为1时有效
chatIds	string	使用该配置的客户群ID列表，最多支持5个
createTime	string
updateTime	string	更新时间
delFlag	integer	是否删除标识
 * @returns
 */
export const add = (data) => post(`${serve}/save`, data)

// 修改
export function update(data) {
	return put(`${serve}/update`, data)
}

//获取企业微信群
export const getGroupList = () => get(`${serve}/findYjaiscrmChat`)
