import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/iYqueConfig'

export const getDetail = (id) => get(`${serve}/findIYqueConfig`)

export const addOrUpdate = (data) => post(`${serve}/saveOrUpdate`, data)

const serveWel = '/iYqueDefaultMsg'
export const getDetailWel = (id) => get(`${serveWel}/findDefaultMsg`)

/**
 *
 * @param {*} data
 * {
    "defaultContent": "string",
    "annexLists": [
        {
            "msgtype": "string", 当前分为四种类型:image(图片);like(链接);miniprogram(小程序);video(视频);file(文件)。 根据当前类型的不同传入的对象不同，比如当前类型为image则传image对象。其附件返回也是一样
            "image": {
                "picUrl": "string"
            },
            "link": {
                "title": "string",
                "picurl": "string",
                "desc": "string",
                "url": "string"
            },
            "miniprogram": {
                "title": "string",
                "picurl": "string",
                "appid": "string",
                "page": "string"
            },
            "video": {
                "videoUrl": "string"
            },
            "file": {
                "fileUrl": "string"
            }
        }
    ]
}
 * @returns
 */
export const addOrUpdateWel = (data) => post(`${serveWel}/saveOrUpdate`, data)
