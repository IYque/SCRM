// 导入request模块（已在request.d.ts中定义类型）
import request from '@/utils/request'

// 解构request方法
const { get, post, put, delt: _del } = request

const service = '/customerInfo'
const tagService = '/yjaiscrmTag'

// 定义API响应类型
interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
  rows?: T[]
  total?: number
  count?: number
}

// 定义标签类型
interface CustomerTag {
  id: string
  name: string
  createTime?: any
  order?: any
  deleted?: any
  [key: string]: any
}

// 定义客户类型
interface Customer {
  customerId: string
  customerName: string
  tagNames?: string[]
  [key: string]: any
}

/** 列表
 * @param {*} params
customerName,string,false,客户名
 */
export const getList = (data?: any): Promise<ApiResponse<Customer[]>> => get(`${service}/findAll`, data)

/** synchCustomer
 * @returns
 */
export const synchCustomer = (data?: any): Promise<ApiResponse> => post(`${service}/synchCustomer`, data)

/** 获取客户标签列表
 * @returns
 */
export const getCustomerTags = (data?: any): Promise<ApiResponse<CustomerTag[]>> => get(`${tagService}/findYjaiscrmTag`, data)

/** 为客户打标签
 * @param {*} params
 * externalUserid: 客户的externalUserid
 * tagIds: 标签ID数组
 * userId: 用户ID
 */
export const tagCustomers = (data: { externalUserid: string; tagIds: string[]; userId: string }): Promise<ApiResponse> => post(`${service}/tagCustomers`, data)
