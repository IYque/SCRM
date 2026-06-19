import request from '@/utils/request'

export function chatWithMemory(data) {
  return request({
    url: '/yjaiscrmAi/chatWithMemory',
    method: 'post',
    data
  })
}

export function getAvailableModels() {
  return request({
    url: '/yjaiscrmAi/models',
    method: 'get'
  })
}

export function getFunctionRoutes() {
  return request({
    url: '/yjaiscrmAi/functionRoutes',
    method: 'get'
  })
}

export function chatWithMemoryStream(data, onMessage, onError, onComplete) {
  const url = '/api/yjaiscrmAi/chatWithMemoryStream'
  
  return new Promise((resolve, reject) => {
    let fullResponse = ''
    const TIMEOUT_DURATION = 120000
    let timeoutId = null
    
    const token = localStorage.getItem('Admin-Token') || ''
    
    const headers = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      ...window.sysConfig.headers
    }
    
    console.log('[SSE] 开始请求:', { url, data })
    
    timeoutId = setTimeout(() => {
      console.error('[SSE] 请求超时')
      const error = new Error('响应超时，请稍后重试')
      if (onError) onError(error)
      reject(error)
    }, TIMEOUT_DURATION)
    
    fetch(url, {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(data)
    })
    .then(response => {
      console.log('[SSE] 响应状态:', response.status, response.ok)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''
      
      function processText(text) {
        buffer += text
        console.log('[SSE] 当前buffer:', JSON.stringify(buffer))
        
        const lines = buffer.split('\n\n')
        buffer = lines.pop() || ''
        
        console.log('[SSE] 分割后的lines:', lines)
        console.log('[SSE] 剩余buffer:', JSON.stringify(buffer))
        
        for (const line of lines) {
          console.log('[SSE] 处理line:', JSON.stringify(line))
          
          let content = line
          if (line.startsWith('data:')) {
            content = line.replace(/^data:\s*/, '')
          }
          
          console.log('[SSE] 处理后的content:', JSON.stringify(content))
          
          if (content.trim() === '[DONE]') {
            console.log('[SSE] 收到结束标记')
            clearTimeout(timeoutId)
            if (onComplete) onComplete(fullResponse)
            resolve(fullResponse)
            return true
          }
          
          if (content.includes('"error"')) {
            try {
              const errorObj = JSON.parse(content)
              console.error('[SSE] 收到错误:', errorObj)
              clearTimeout(timeoutId)
              const error = new Error(errorObj.error || '未知错误')
              if (onError) onError(error)
              reject(error)
              return true
            } catch (e) {
              console.log('[SSE] 解析错误失败，作为普通数据处理')
            }
          }
          
          if (content.trim()) {
            clearTimeout(timeoutId)
            timeoutId = setTimeout(() => {
              console.error('[SSE] 请求超时')
              const error = new Error('响应超时，请稍后重试')
              if (onError) onError(error)
              reject(error)
            }, TIMEOUT_DURATION)
            
            fullResponse += content
            console.log('[SSE] 调用onMessage, 内容:', JSON.stringify(content))
            console.log('[SSE] 当前完整响应:', JSON.stringify(fullResponse))
            if (onMessage) onMessage(content)
          }
        }
        return false
      }
      
      function read() {
        reader.read().then(({ done, value }) => {
          console.log('[SSE] read状态:', { done, valueLength: value?.length })
          
          if (done) {
            console.log('[SSE] 流结束')
            if (buffer.trim()) {
              processText('\n\n')
            }
            clearTimeout(timeoutId)
            console.log('[SSE] 最终响应:', JSON.stringify(fullResponse))
            if (onComplete) onComplete(fullResponse)
            resolve(fullResponse)
            return
          }
          
          const chunk = decoder.decode(value, { stream: true })
          console.log('[SSE] 收到chunk:', JSON.stringify(chunk))
          
          const shouldStop = processText(chunk)
          if (!shouldStop) {
            read()
          }
        })
        .catch(error => {
          console.error('[SSE] 读取错误:', error)
          clearTimeout(timeoutId)
          if (onError) onError(error)
          reject(error)
        })
      }
      
      read()
    })
    .catch(error => {
      console.error('[SSE] 请求错误:', error)
      clearTimeout(timeoutId)
      if (onError) onError(error)
      reject(error)
    })
  })
}

export function navigationChatStream(data, onMessage, onError, onComplete) {
  const url = '/api/yjaiscrmAi/navigationChatStream'
  
  return new Promise((resolve, reject) => {
    let fullResponse = ''
    const TIMEOUT_DURATION = 120000
    let timeoutId = null
    
    const token = localStorage.getItem('Admin-Token') || ''
    
    const headers = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      ...window.sysConfig.headers
    }
    
    console.log('[Navigation SSE] 开始请求:', { url, data })
    
    timeoutId = setTimeout(() => {
      console.error('[Navigation SSE] 请求超时')
      const error = new Error('响应超时，请稍后重试')
      if (onError) onError(error)
      reject(error)
    }, TIMEOUT_DURATION)
    
    fetch(url, {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(data)
    })
    .then(response => {
      console.log('[Navigation SSE] 响应状态:', response.status, response.ok)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''
      
      function processText(text) {
        buffer += text
        
        const lines = buffer.split('\n\n')
        buffer = lines.pop() || ''
        
        for (const line of lines) {
          let content = line
          if (line.startsWith('data:')) {
            content = line.replace(/^data:\s*/, '')
          }
          
          if (content.trim() === '[DONE]') {
            clearTimeout(timeoutId)
            if (onComplete) onComplete(fullResponse)
            resolve(fullResponse)
            return true
          }
          
          if (content.includes('"error"')) {
            try {
              const errorObj = JSON.parse(content)
              clearTimeout(timeoutId)
              const error = new Error(errorObj.error || '未知错误')
              if (onError) onError(error)
              reject(error)
              return true
            } catch (e) {
            }
          }
          
          if (content.trim()) {
            clearTimeout(timeoutId)
            timeoutId = setTimeout(() => {
              const error = new Error('响应超时，请稍后重试')
              if (onError) onError(error)
              reject(error)
            }, TIMEOUT_DURATION)
            
            fullResponse += content
            if (onMessage) onMessage(content)
          }
        }
        return false
      }
      
      function read() {
        reader.read().then(({ done, value }) => {
          if (done) {
            if (buffer.trim()) {
              processText('\n\n')
            }
            clearTimeout(timeoutId)
            if (onComplete) onComplete(fullResponse)
            resolve(fullResponse)
            return
          }
          
          const chunk = decoder.decode(value, { stream: true })
          
          const shouldStop = processText(chunk)
          if (!shouldStop) {
            read()
          }
        })
        .catch(error => {
          console.error('[Navigation SSE] 读取错误:', error)
          clearTimeout(timeoutId)
          if (onError) onError(error)
          reject(error)
        })
      }
      
      read()
    })
    .catch(error => {
      console.error('[Navigation SSE] 请求错误:', error)
      clearTimeout(timeoutId)
      if (onError) onError(error)
      reject(error)
    })
  })
}

export function getConversationList() {
  return request({
    url: '/ai/conversation/list',
    method: 'get',
    params: { deviceType: 1 }
  })
}

export function createConversation(data) {
  return request({
    url: '/ai/conversation/create',
    method: 'post',
    data: { ...data, deviceType: 1 }
  })
}

export function updateConversation(data) {
  return request({
    url: '/ai/conversation/update',
    method: 'put',
    data: { ...data, deviceType: 1 }
  })
}

export function deleteConversation(conversationId) {
  return request({
    url: `/ai/conversation/delete/${conversationId}`,
    method: 'delete',
    params: { deviceType: 1 }
  })
}

export function getConversationMessages(conversationId) {
  return request({
    url: `/ai/conversation/messages/${conversationId}`,
    method: 'get',
    params: { deviceType: 1 }
  })
}

export function saveConversationMessage(data) {
  return request({
    url: '/ai/conversation/message/save',
    method: 'post',
    data: { ...data, deviceType: 1 }
  })
}

export function saveConversationMessages(conversationId, messages) {
  return request({
    url: `/ai/conversation/messages/save/${conversationId}`,
    method: 'post',
    data: { messages, deviceType: 1 }
  })
}

export function getConversationDetail(conversationId) {
  return request({
    url: `/ai/conversation/detail/${conversationId}`,
    method: 'get',
    params: { deviceType: 1 }
  })
}
