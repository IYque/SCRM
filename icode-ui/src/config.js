import { getToken } from '@/utils/auth'
import { env, common } from '../sys.config'

let config = {
  get headers() {
    return { Authorization: 'Bearer ' + getToken() }
  },
}

window.sysConfig = Object.assign(config, env, common)

// 主题回显
const [h, s, l] = localStorage.hsl?.split(',')?.map((e) => e.trim()) || []
if (h) {
  const style = document.documentElement.style
  style.setProperty('--h', h)
  style.setProperty('--s', s)
  style.setProperty('--l', l)
}

// 阻止表单默认提交行为
document.addEventListener('submit', (event) => {
  event.preventDefault()
})

// 统一为img的src不是绝对地址的拼接接口地址
document.addEventListener(
  'error',
  function (e) {
    let target = e.target
    let src = target.attributes.getNamedItem('src').value
    if (target.tagName.toUpperCase() === 'IMG' && src && !src.includes('http')) {
      target.src = window.sysConfig.BASE_API + '/file/fileView/' + src
      e.stopPropagation()
    }
  },
  true,
)
