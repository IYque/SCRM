import request from '@/utils/request'
const { get, post, put, delt } = request

export const upload = (data) => post(`/file/upload`, data)

export const getUserList = (data) =>
	get(`/yjaiscrmUser/findYjaiscrmUser`, data).then((res) => {
		if (res.code == 200) {
			res.data?.forEach((element) => {
				element.id = element.userId
			})
		}
		return res
	})

export const getTagList = (data) => get(`/yjaiscrmTag/findYjaiscrmTag`, data)

export function getRemarkList() {
	return request({
		url: '/yjaiscrmCommon/findRemarksType',
	})
}
