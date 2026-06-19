<template>
  <div class="ai-chat-wrapper" ref="chatWrapper" :class="{ 'chat-open': isChatOpen }">
    <el-tooltip content="AI智能助手" placement="left" v-if="!isChatOpen">
      <div class="ai-chat-button" @click="toggleChat">
        <svg class="ai-icon-svg" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="32" cy="32" r="28" fill="var(--Color)"/>
          <path d="M32 14V20M32 44V50M14 32H20M44 32H50" stroke="white" stroke-width="3" stroke-linecap="round"/>
          <circle cx="32" cy="32" r="10" fill="white"/>
          <circle cx="32" cy="32" r="6" fill="var(--Color)"/>
          <circle cx="32" cy="20" r="2" fill="white"/>
          <circle cx="32" cy="44" r="2" fill="white"/>
          <circle cx="20" cy="32" r="2" fill="white"/>
          <circle cx="44" cy="32" r="2" fill="white"/>
          <circle cx="24" cy="24" r="2" fill="white"/>
          <circle cx="40" cy="24" r="2" fill="white"/>
          <circle cx="24" cy="40" r="2" fill="white"/>
          <circle cx="40" cy="40" r="2" fill="white"/>
          <path d="M32 26V38M26 32H38" stroke="white" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
    </el-tooltip>
    
    <div class="ai-chat-window" v-if="isChatOpen" :style="{ width: chatWidth + 'px' }">
      <div class="resize-handle" @mousedown="startResize">
        <div class="resize-icon">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M8 5h2v14H8zm6 0h2v14h-2z"/>
          </svg>
        </div>
      </div>
      <div class="ai-chat-sidebar" :class="{ 'sidebar-open': showSidebar }">
        <div class="sidebar-header">
          <h4>yjaiscrm SCRM</h4>
          <button class="new-chat-button sidebar-new-chat" @click="createNewChat">+ 新会话</button>
        </div>
        <div class="sidebar-body">
          <div 
            v-for="chat in chats" 
            :key="chat.id" 
            :class="['chat-item', { 'active': chat.id === currentChatId }]"
          >
            <div class="chat-item-content" @click="switchChat(chat.id)">
              <div class="chat-item-title-row">
                <div class="chat-item-title">{{ chat.title }}</div>
                <span class="chat-item-mode" :class="chat.mode || 'general'">
                  {{ chat.mode === 'navigation' ? 'AI导航' : '通用' }}
                </span>
              </div>
            </div>
            <div class="chat-item-actions">
              <button class="chat-item-edit" @click.stop="openEditDialog(chat)" title="编辑">
                <el-icon><svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg></el-icon>
              </button>
              <button class="chat-item-delete" @click.stop="deleteChat(chat.id)" title="删除">
                <el-icon><svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"></path></svg></el-icon>
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div class="ai-chat-main">
        <div class="ai-chat-header">
          <div class="header-left">
            <button class="sidebar-toggle" @click="toggleSidebar">
              <el-icon><svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"></path></svg></el-icon>
            </button>
            <span class="header-title">AI营销助手</span>
            <span class="header-mode-tag" :class="currentChatMode">
              {{ currentChatMode === 'navigation' ? 'AI导航' : '通用' }}
            </span>
          </div>
          <div class="header-actions">
            <button class="close-button" @click="closeChat">×</button>
          </div>
        </div>
        <div class="ai-chat-body" ref="chatBodyRef" @scroll="saveScrollPosition">
          <div class="watermark-container">
            <div v-for="(wm, index) in watermarks" :key="index" class="watermark" :style="wm.style">
              {{ wm.text }}
            </div>
          </div>
          <div v-for="(message, index) in currentMessages" :key="index" :class="['message', message.type]">
            <div class="message-content">
              <div class="message-text" v-html="renderMessage(message.content)"></div>
              <div class="message-footer">
                <div class="message-time">{{ message.timestamp }}</div>
                <div class="message-actions">
                  <el-tooltip content="复制" placement="top">
                    <button class="action-btn" @click="copyMessage(message.content)">
                      <el-icon><svg viewBox="0 0 24 24" fill="currentColor"><path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/></svg></el-icon>
                    </button>
                  </el-tooltip>
                  <el-tooltip v-if="message.type === 'user'" content="编辑" placement="top">
                    <button class="action-btn" @click="editMessage(message.content)">
                      <el-icon><svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg></el-icon>
                    </button>
                  </el-tooltip>
                </div>
              </div>
              <div v-if="message.type === 'ai'" class="ai-disclaimer">
                <el-icon><svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/></svg></el-icon>
                <span>以上内容由AI生成，仅供参考</span>
              </div>
            </div>
          </div>
          <div v-if="isLoading" class="loading-message">
            <el-icon class="is-loading"><svg viewBox="0 0 24 24"><path fill="currentColor" d="M12,4a8,8,0,0,1,7.89,6.78l-3.2-2.14A5,5,0,0,0,12,6a5,5,0,0,0-4.69,3.33L4.11,10.78A8,8,0,0,1,12,4m0,16a8,8,0,0,1-7.89-6.78l3.2,2.14A5,5,0,0,0,12,18a5,5,0,0,0,4.69-3.33L19.89,13.22A8,8,0,0,1,12,20Z" /></svg></el-icon>
            <span>AI正在思考...</span>
          </div>
        </div>
        <div class="ai-chat-footer">
          <div class="input-row">
            <div class="input-wrapper">
              <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="3"
                placeholder="请输入您的问题... (↑↓键浏览历史)"
                @keyup.enter.exact="sendMessage"
                @keyup.enter.shift.exact="() => {}"
                @keyup.up.exact="navigateHistory('up')"
                @keyup.down.exact="navigateHistory('down')"
              />
              <div class="input-actions">
                <el-tooltip content="清空输入" placement="top">
                  <button class="action-btn" @click="clearInput" :disabled="!inputMessage.trim()">
                    <el-icon><svg viewBox="0 0 24 24" fill="currentColor"><path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg></el-icon>
                  </button>
                </el-tooltip>
              </div>
            </div>
            <button class="send-button" @click="sendMessage" :disabled="!inputMessage.trim()">
              <el-icon><svg viewBox="0 0 24 24" fill="currentColor"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"></path></svg></el-icon>
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <el-dialog v-model="showSettings" title="设置" width="500px" :close-on-click-modal="false">
      <el-form :model="settings" label-width="120px">
        <el-form-item label="模型选择">
          <el-select v-model="settings.modelName" placeholder="选择模型" style="width: 100%;">
            <el-option
              v-for="model in availableModels"
              :key="model"
              :label="model"
              :value="model"
            />
          </el-select>
          <div class="param-description">选择用于对话的AI模型</div>
        </el-form-item>
        <el-form-item label="AI角色设定">
          <el-input
            v-model="settings.role"
            type="textarea"
            :rows="4"
            placeholder="请输入AI的角色设定，例如：你是一个SCRM营销助手..."
          />
        </el-form-item>
        <el-form-item label="温度参数">
          <div class="slider-wrapper">
            <el-slider v-model="settings.temperature" :min="0" :max="1" :step="0.1" />
            <span class="slider-value">{{ settings.temperature }}</span>
          </div>
          <div class="param-description">控制回答的随机性。值越小，回答越确定和一致；值越大，回答越随机和多样。推荐范围：0.5-0.9</div>
        </el-form-item>
        <el-form-item label="核采样参数">
          <div class="slider-wrapper">
            <el-slider v-model="settings.topP" :min="0" :max="1" :step="0.1" />
            <span class="slider-value">{{ settings.topP }}</span>
          </div>
          <div class="param-description">控制回答的多样性。值越小，回答越集中和保守；值越大，回答越多样和开放。推荐范围：0.8-1.0</div>
        </el-form-item>
        <el-form-item label="历史对话轮数">
          <div class="slider-wrapper">
            <el-slider v-model="settings.maxHistoryRounds" :min="1" :max="20" :step="1" />
            <span class="slider-value">{{ settings.maxHistoryRounds }}轮</span>
          </div>
          <div class="param-description">历史对话淘汰策略：保留的最大对话轮数。轮数越多，上下文越长，但Token消耗也越大。推荐范围：5-15轮</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetSettings">恢复默认</el-button>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
        </span>
      </template>
    </el-dialog>
    
    <AiChatDialog
      v-model="showFunctionDialog"
      dialog-type="create"
      @confirm="handleDialogConfirm"
    />

    <AiChatDialog
      v-model="showEditDialog"
      dialog-type="edit"
      :chat-data="editingChatData"
      @confirm="handleDialogConfirm"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AiChatDialog from './AiChatDialog.vue'
import { 
  chatWithMemoryStream, 
  getAvailableModels, 
  getFunctionRoutes, 
  navigationChatStream,
  getConversationList,
  createConversation,
  updateConversation,
  deleteConversation as deleteConversationApi,
  getConversationMessages,
  saveConversationMessage,
  saveConversationMessages
} from '@/api/ai'

const router = useRouter()
const isChatOpen = ref(false)
const showSidebar = ref(false)
const showSettings = ref(false)
const showFunctionDialog = ref(false)
const showEditDialog = ref(false)
const editingChatId = ref(null)
const editingChatData = ref(null)
const chats = ref([])
const currentChatId = ref(null)
const inputMessage = ref('')
const isLoading = ref(false)
const chatBodyRef = ref(null)
const availableModels = ref([])
const functionRoutes = ref([])
const chatWidth = ref(800)
const isResizing = ref(false)

const inputHistory = ref([])
const historyIndex = ref(-1)
const tempInput = ref('')

const watermarks = ref([])

const generateWatermarks = () => {
  const watermarkTexts = ['yjaiscrm SCRM']
  const count = 8
  const newWatermarks = []
  const usedPositions = []
  
  const checkOverlap = (top, left, fontSize) => {
    const minDistance = fontSize * 3.5
    for (const pos of usedPositions) {
      const distance = Math.sqrt(Math.pow(top - pos.top, 2) + Math.pow(left - pos.left, 2))
      if (distance < minDistance) {
        return true
      }
    }
    return false
  }
  
  for (let i = 0; i < count; i++) {
    const text = watermarkTexts[0]
    const fontSize = 24 + Math.floor(Math.random() * 6)
    let top, left
    let attempts = 0
    
    do {
      top = Math.random() * 80 + 10
      left = Math.random() * 80 + 10
      attempts++
    } while (checkOverlap(top, left, fontSize) && attempts < 50)
    
    usedPositions.push({ top, left })
    
    const rotate = -20 + Math.random() * 40
    
    newWatermarks.push({
      text,
      style: {
        top: `${top}%`,
        left: `${left}%`,
        transform: `rotate(${rotate}deg)`,
        fontSize: `${fontSize}px`,
        opacity: 0.25
      }
    })
  }
  
  watermarks.value = newWatermarks
}

const settings = reactive({
  modelName: '',
  role: '你是一个SCRM营销助手，专注于客户关系管理和营销策略。请用专业、友好的语言回答用户的问题，提供实用的营销建议和客户管理技巧。',
  temperature: 0.7,
  topP: 0.9,
  maxHistoryRounds: 10
})

const currentChat = computed(() => {
  return chats.value.find(chat => chat.id === currentChatId.value) || chats.value[0]
})

const currentMessages = computed(() => {
  return currentChat.value?.messages || []
})

const currentChatMode = computed(() => {
  return currentChat.value?.mode || 'general'
})

const toggleChat = () => {
  isChatOpen.value = !isChatOpen.value
  if (isChatOpen.value) {
    showSidebar.value = false
    loadChats()
    nextTick(() => scrollToBottom())
  }
}

const closeChat = () => {
  isChatOpen.value = false
}

const toggleSidebar = () => {
  showSidebar.value = !showSidebar.value
}

const createNewChat = async () => {
  if (availableModels.value.length === 0) {
    await loadAvailableModels()
  }
  editingChatData.value = null
  showFunctionDialog.value = true
}

const openEditDialog = async (chat) => {
  editingChatId.value = chat.id
  if (availableModels.value.length === 0) {
    await loadAvailableModels()
  }
  editingChatData.value = {
    id: chat.id,
    title: chat.title,
    mode: chat.mode || 'general',
    settings: {
      modelName: chat.settings?.modelName || chat.modelName || '',
      role: chat.settings?.role || chat.role || '',
      temperature: chat.settings?.temperature ?? chat.temperature ?? 0.7,
      topP: chat.settings?.topP ?? chat.topP ?? 0.9,
      maxHistoryRounds: chat.settings?.maxHistoryRounds ?? chat.maxHistoryRounds ?? 10
    }
  }
  showEditDialog.value = true
}

const handleDialogConfirm = async ({ type, data }) => {
  if (type === 'create') {
    try {
      const response = await createConversation({
        title: data.title,
        mode: data.mode,
        modelName: data.modelName,
        role: data.role,
        temperature: data.temperature,
        topP: data.topP,
        maxHistoryRounds: data.maxHistoryRounds
      })
      if (response && response.data) {
        const newChat = {
          id: response.data.conversationId,
          title: response.data.title,
          messages: [],
          mode: response.data.mode,
          settings: {
            modelName: response.data.modelName || data.modelName,
            role: response.data.role || data.role,
            temperature: response.data.temperature ?? data.temperature,
            topP: response.data.topP ?? data.topP,
            maxHistoryRounds: response.data.maxHistoryRounds ?? data.maxHistoryRounds
          }
        }
        chats.value.unshift(newChat)
        currentChatId.value = newChat.id
        if (data.mode === 'navigation') {
          ElMessage.success('AI导航模式已开启，将根据场景推荐系统功能')
        }
      }
    } catch (error) {
      console.error('创建会话失败:', error)
      ElMessage.error('创建会话失败')
    }
  } else if (type === 'edit') {
    try {
      await updateConversation({
        conversationId: editingChatId.value,
        title: data.title,
        modelName: data.modelName,
        role: data.role,
        temperature: data.temperature,
        topP: data.topP,
        maxHistoryRounds: data.maxHistoryRounds
      })
      const chat = chats.value.find(c => c.id === editingChatId.value)
      if (chat) {
        chat.title = data.title
        chat.settings = {
          modelName: data.modelName,
          role: data.role,
          temperature: data.temperature,
          topP: data.topP,
          maxHistoryRounds: data.maxHistoryRounds || 10
        }
        chats.value = [...chats.value]
        ElMessage.success('会话已更新')
      }
    } catch (error) {
      console.error('更新会话名称失败:', error)
      ElMessage.error('更新会话名称失败')
    }
  }
}

const switchChat = async (chatId) => {
  currentChatId.value = chatId
  
  const chat = chats.value.find(c => c.id === chatId)
  if (chat && (!chat.messages || chat.messages.length === 0)) {
    isLoading.value = true
    const startTime = Date.now()
    await loadChatDetail(chatId)
    const elapsed = Date.now() - startTime
    const minLoadingTime = 800
    if (elapsed < minLoadingTime) {
      await new Promise(resolve => setTimeout(resolve, minLoadingTime - elapsed))
    }
    isLoading.value = false
  }
  
  nextTick(() => scrollToBottom())
}

const deleteChat = async (chatId) => {
  try {
    await deleteConversationApi(chatId)
    const index = chats.value.findIndex(chat => chat.id === chatId)
    if (index >= 0) {
      chats.value.splice(index, 1)
      if (chatId === currentChatId.value) {
        if (chats.value.length > 0) {
          currentChatId.value = chats.value[0].id
          const chat = chats.value[0]
          if (!chat.messages || chat.messages.length === 0) {
            await loadChatDetail(chat.id)
          }
        } else {
          currentChatId.value = null
        }
      }
    }
    ElMessage.success('会话已删除')
  } catch (error) {
    console.error('删除会话失败:', error)
    ElMessage.error('删除会话失败')
  }
}

const getLastMessage = (chat) => {
  if (chat.lastMessage) {
    let content = chat.lastMessage || ''
    if (content.length > 30) content = content.substring(0, 30) + '...'
    return content
  }
  if (!chat.messages || chat.messages.length === 0) return '无消息'
  const lastMessage = chat.messages[chat.messages.length - 1]
  let content = lastMessage.content || ''
  if (content.length > 30) content = content.substring(0, 30) + '...'
  return lastMessage.type === 'user' ? `我: ${content}` : `AI: ${content}`
}

const getLastMessageTime = (chat) => {
  if (chat.lastMessageTime) {
    return formatTime(new Date(chat.lastMessageTime))
  }
  if (!chat.messages || chat.messages.length === 0) return ''
  return chat.messages[chat.messages.length - 1].timestamp
}

const formatTime = (date) => {
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const copyMessage = async (content) => {
  try {
    // 清理掉所有的data:前缀（SSE格式残留）
    const cleanContent = content.replace(/data:/g, '')
    await navigator.clipboard.writeText(cleanContent)
    ElMessage.success('已复制到剪贴板')
  } catch (err) {
    console.error('复制失败:', err)
    ElMessage.error('复制失败')
  }
}

const editMessage = (content) => {
  // 清理掉所有的data:前缀（SSE格式残留）
  const cleanContent = content.replace(/data:/g, '')
  inputMessage.value = cleanContent
  ElMessage.success('已编辑消息')
}

const renderMessage = (content) => {
  if (!content) return ''
  
  let html = content
    .replace(/data:/g, '')
  
  html = html
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
  
  const codeBlocks = []
  html = html.replace(/```(\w*)\n?([\s\S]*?)```/g, (match, lang, code) => {
    const index = codeBlocks.length
    const trimmedCode = code.trim()
    
    if (lang === 'json' && trimmedCode.includes('"recommendations"')) {
      try {
        const jsonData = JSON.parse(trimmedCode)
        if (jsonData.recommendations && Array.isArray(jsonData.recommendations)) {
          let buttonsHtml = '<div class="route-recommendations">'
          buttonsHtml += '<div class="route-recommendations-title">推荐功能：</div>'
          buttonsHtml += '<div class="route-recommendations-list">'
          
          jsonData.recommendations.forEach((item, idx) => {
            buttonsHtml += `<div class="route-item" onclick="window.handleRouteClick && window.handleRouteClick('${item.path}')">`
            buttonsHtml += `<div class="route-item-header">`
            buttonsHtml += `<span class="route-item-index">${idx + 1}</span>`
            buttonsHtml += `<span class="route-item-title">${item.title}</span>`
            buttonsHtml += `</div>`
            buttonsHtml += `<div class="route-item-reason">${item.reason}</div>`
            buttonsHtml += `<div class="route-item-path">点击跳转 → ${item.path}</div>`
            buttonsHtml += `</div>`
          })
          
          buttonsHtml += '</div></div>'
          codeBlocks.push(buttonsHtml)
          return `__CODE_BLOCK_${index}__`
        }
      } catch (e) {
        console.error('解析推荐JSON失败:', e)
      }
    }
    
    codeBlocks.push(`<pre class="code-block"><code class="language-${lang || 'text'}">${trimmedCode}</code></pre>`)
    return `__CODE_BLOCK_${index}__`
  })
  
  codeBlocks.forEach((block, index) => {
    html = html.replace(`__CODE_BLOCK_${index}__`, block)
  })
  
  html = html.replace(/`([^`]+)`/g, '<code class="inline-code">$1</code>')
  
  html = html.replace(/^### (.*$)/gim, '<h3 class="md-h3">$1</h3>')
  html = html.replace(/^## (.*$)/gim, '<h2 class="md-h2">$1</h2>')
  html = html.replace(/^# (.*$)/gim, '<h1 class="md-h1">$1</h1>')
  
  html = html.replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>')
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong class="md-bold">$1</strong>')
  html = html.replace(/\*(.+?)\*/g, '<em class="md-italic">$1</em>')
  
  const routeLinkPattern = /(\d+)\.\s*\[([^\]]+)\]\(([^)]+)\)\s*[-－—]\s*([^]*?)(?=\d+\.\s*\[|根据您的|$)/g
  const routeMatches = []
  let routeMatch
  while ((routeMatch = routeLinkPattern.exec(html)) !== null) {
    let reason = routeMatch[4].trim()
    reason = reason.replace(/这些功能可以帮助您.*$/, '').trim()
    reason = reason.replace(/您可以根据.*$/, '').trim()
    
    routeMatches.push({
      fullMatch: routeMatch[0],
      index: routeMatch[1],
      title: routeMatch[2],
      path: routeMatch[3],
      reason: reason
    })
  }
  
  if (routeMatches.length > 0) {
    let buttonsHtml = '<div class="route-recommendations">'
    buttonsHtml += '<div class="route-recommendations-title">推荐功能：</div>'
    buttonsHtml += '<div class="route-recommendations-list">'
    
    routeMatches.forEach((item, idx) => {
      buttonsHtml += `<div class="route-item" onclick="window.handleRouteClick && window.handleRouteClick('${item.path}')">`
      buttonsHtml += `<div class="route-item-header">`
      buttonsHtml += `<span class="route-item-index">${idx + 1}</span>`
      buttonsHtml += `<span class="route-item-title">${item.title}</span>`
      buttonsHtml += `</div>`
      if (item.reason) {
        buttonsHtml += `<div class="route-item-reason">${item.reason}</div>`
      }
      buttonsHtml += `<div class="route-item-path">点击跳转</div>`
      buttonsHtml += `</div>`
    })
    
    buttonsHtml += '</div></div>'
    
    routeMatches.forEach((item) => {
      html = html.replace(item.fullMatch, '')
    })
    
    html = html.replace(/推荐以下功能[：:]/, '')
    html = html.replace(/根据您的需求[，,]?/g, '')
    html = html.replace(/这些功能可以帮助您[^。]*。/g, '')
    html = html.replace(/您可以根据[^。]*。/g, '')
    
    html = buttonsHtml + html
  }
  
  html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, (match, text, url) => {
    if (url.startsWith('/') || url.startsWith('#/')) {
      return `<a href="javascript:void(0)" class="md-route-link" data-path="${url}" onclick="window.handleRouteClick && window.handleRouteClick('${url}')">${text}</a>`
    }
    return `<a href="${url}" target="_blank" class="md-link">${text}</a>`
  })
  
  html = html.replace(/^>\s(.+)$/gim, '<blockquote class="md-quote">$1</blockquote>')
  html = html.replace(/(<blockquote class="md-quote">.*?<\/blockquote>)/gs, '<div class="md-blockquote">$1</div>')
  
  html = html.replace(/^---$/gim, '<hr class="md-hr">')
  
  html = html.replace(/^\s*[-*+]\s+(.+)$/gim, '<li class="md-list-item">$1</li>')
  html = html.replace(/(<li class="md-list-item">\s*<\/li>)/g, '')
  html = html.replace(/(<li class="md-list-item">.*?)(?=<li class="md-list-item">|$)/gs, (match) => {
    if (!match.includes('<ul class="md-list">')) {
      return '<ul class="md-list">' + match + '</ul>'
    }
    return match
  })
  html = html.replace(/<ul class="md-list"><li class="md-list-item"><ul class="md-list">/g, '<ul class="md-list"><li class="md-list-item">')
  html = html.replace(/<\/ul><\/li><\/ul>/g, '</ul></li></ul>')
  
  html = html.replace(/^\s*(\d+)\.\s+(.+)$/gim, '<li class="md-ordered-item">$2</li>')
  html = html.replace(/(<li class="md-ordered-item">.*?)(?=<li class="md-ordered-item">|$)/gs, (match) => {
    if (!match.includes('<ol class="md-ordered-list">')) {
      return '<ol class="md-ordered-list">' + match + '</ol>'
    }
    return match
  })
  
  html = html.replace(/\n\n+/g, '\n')
  html = html.split('\n').filter(line => line.trim()).map(line => {
    if (line.match(/^<(h[1-3]|ul|ol|blockquote|hr|div|pre)/)) {
      return line
    }
    return `<p class="md-paragraph">${line}</p>`
  }).join('\n')
  
  return html
}

const handleRouteClick = (path) => {
  console.log('[AI Chat] 导航到路径:', path)
  if (path) {
    router.push(path)
    ElMessage.success('跳转成功')
  }
}

const loadFunctionRoutes = async () => {
  try {
    const response = await getFunctionRoutes()
    if (response && response.data) {
      functionRoutes.value = response.data
    }
  } catch (error) {
    console.error('[AI Chat] 加载功能路由列表失败:', error)
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
    }
  })
}

const saveScrollPosition = (e) => {}

const loadConversationList = async () => {
  try {
    const response = await getConversationList()
    if (response && response.data) {
      const conversationList = response.data
      
      const loadedChats = conversationList.map((conv) => ({
        id: conv.conversationId,
        title: conv.title,
        mode: conv.mode || 'general',
        lastMessage: conv.lastMessage || '',
        lastMessageTime: conv.lastMessageTime || null,
        messages: [],
        settings: {
          modelName: conv.modelName || '',
          role: conv.role || '',
          temperature: conv.temperature ?? 0.7,
          topP: conv.topP ?? 0.9,
          maxHistoryRounds: conv.maxHistoryRounds ?? 10
        }
      }))
      
      chats.value = loadedChats
      
      if (loadedChats.length > 0) {
        currentChatId.value = loadedChats[0].id
        await loadChatDetail(loadedChats[0].id)
      }
    }
  } catch (error) {
    console.error('加载会话列表失败:', error)
    chats.value = []
  }
}

const loadChatDetail = async (chatId) => {
  try {
    const msgResponse = await getConversationMessages(chatId)
    const chatIndex = chats.value.findIndex(c => c.id === chatId)
    if (chatIndex !== -1) {
      chats.value[chatIndex].messages = msgResponse.data || []
    }
    return msgResponse.data || []
  } catch (e) {
    console.error('加载会话详情失败:', e)
    return []
  }
}

const loadChats = async () => {
  await loadConversationList()
}

const saveSettings = () => {
  showSettings.value = false
  ElMessage.success('设置已保存')
}

const resetSettings = () => {
  settings.modelName = ''
  settings.role = '你是一个SCRM营销助手，专注于客户关系管理和营销策略。请用专业、友好的语言回答用户的问题，提供实用的营销建议和客户管理技巧。'
  settings.temperature = 0.7
  settings.topP = 0.9
  settings.maxHistoryRounds = 10
  ElMessage.success('已恢复默认设置')
}

const loadSettings = () => {
}

const loadAvailableModels = async () => {
  try {
    const response = await getAvailableModels()
    if (response && Array.isArray(response)) {
    }
  } catch (error) {
    console.error('[AI Chat] 加载模型列表失败:', error)
  }
}

const loadInputHistory = () => {
}

const navigateHistory = (direction) => {
  if (direction === 'up') {
    // 向上箭头：浏览更早的历史记录
    if (historyIndex.value < inputHistory.value.length - 1) {
      if (historyIndex.value === -1) {
        // 保存当前输入
        tempInput.value = inputMessage.value
      }
      historyIndex.value++
      inputMessage.value = inputHistory.value[historyIndex.value]
    }
  } else if (direction === 'down') {
    // 向下箭头：浏览更近的历史记录
    if (historyIndex.value > 0) {
      historyIndex.value--
      inputMessage.value = inputHistory.value[historyIndex.value]
    } else if (historyIndex.value === 0) {
      // 回到当前输入
      historyIndex.value = -1
      inputMessage.value = tempInput.value
    }
  }
}

const clearInput = () => {
  inputMessage.value = ''
  historyIndex.value = -1
  tempInput.value = ''
}

const sendMessage = async () => {
    if (!inputMessage.value.trim()) return
    
    const userMessage = inputMessage.value.trim()
    
    if (userMessage && !inputHistory.value.includes(userMessage)) {
      inputHistory.value.unshift(userMessage)
      if (inputHistory.value.length > 50) {
        inputHistory.value.pop()
      }
    }
    historyIndex.value = -1
    tempInput.value = ''
    
    if (!currentChat.value) {
      const defaultChat = {
        id: Date.now().toString(),
        title: '新会话',
        messages: [],
        mode: currentChatMode.value || 'general',
        settings: { ...settings }
      }
      chats.value.unshift(defaultChat)
      currentChatId.value = defaultChat.id
    }
    
    const chatSettings = currentChat.value?.settings || settings
    if (!chatSettings.modelName) {
      ElMessage.warning('请先选择AI模型')
      return
    }
    
    const userMsgObj = {
      type: 'user',
      content: userMessage,
      timestamp: formatTime(new Date())
    }
    
    currentChat.value.messages.push(userMsgObj)
    
    inputMessage.value = ''
    isLoading.value = true
    scrollToBottom()
    
    const aiMessage = {
      type: 'ai',
      content: '',
      timestamp: formatTime(new Date())
    }
    
    currentChat.value.messages.push(aiMessage)
    
    chats.value = [...chats.value]
    
    console.log('[AI Chat] AI消息对象:', aiMessage)
    
    try {
      let history = ''
      const recentMessages = currentChat.value.messages.slice(-10, -1)
      for (const message of recentMessages) {
        if (message.type === 'user') {
          history += `用户: ${message.content}\n`
        } else if (message.type === 'ai') {
          history += `AI: ${message.content}\n`
        }
      }
      
    const requestParams = {
      question: userMessage,
      history: history,
      modelName: chatSettings.modelName,
      role: chatSettings.role,
      temperature: chatSettings.temperature,
      topP: chatSettings.topP,
      maxHistoryRounds: chatSettings.maxHistoryRounds
    }
      
      const streamApi = currentChatMode.value === 'navigation' ? navigationChatStream : chatWithMemoryStream
      console.log('[AI Chat] 开始流式请求, 模式:', currentChatMode.value)
      
      await streamApi(
        requestParams,
        (chunk) => {
          console.log('[AI Chat] 收到chunk:', chunk)
          if (chunk && chunk.trim()) {
            aiMessage.content += chunk
            chats.value = [...chats.value]
            scrollToBottom()
          }
        },
        (error) => {
          console.error('[AI Chat] 错误:', error)
          aiMessage.content = error.message || '抱歉，我暂时无法回答您的问题，请稍后再试。'
          chats.value = [...chats.value]
          isLoading.value = false
        },
        async (fullResponse) => {
          console.log('[AI Chat] 完成:', fullResponse)
          isLoading.value = false
          try {
            await saveConversationMessage({
              conversationId: currentChatId.value,
              type: 'user',
              content: userMessage,
              timestamp: userMsgObj.timestamp
            })
            await saveConversationMessage({
              conversationId: currentChatId.value,
              type: 'ai',
              content: aiMessage.content,
              timestamp: aiMessage.timestamp
            })
          } catch (e) {
            console.error('保存消息失败:', e)
          }
        }
      )
    } catch (error) {
      console.error('[AI Chat] 异常:', error)
      aiMessage.content = error.message || '抱歉，我暂时无法回答您的问题，请稍后再试。'
      chats.value = [...chats.value]
      isLoading.value = false
    }
  }

onMounted(async () => {
  // 先加载模型列表
  await loadAvailableModels()
  // 再加载设置，此时可以验证模型名称是否有效
  loadSettings()
  // 如果设置中的模型不在可用列表中，loadAvailableModels 已经处理了
  
  loadChats()
  loadInputHistory()
  loadFunctionRoutes()
  generateWatermarks()
  if (!currentChatId.value && chats.value.length === 0) {
    const defaultChat = {
      id: Date.now().toString(),
      title: '新会话',
      messages: [],
      mode: 'general'
    }
    chats.value.push(defaultChat)
    currentChatId.value = defaultChat.id
  }
  
  window.handleRouteClick = handleRouteClick
})

onUnmounted(() => {
  window.handleRouteClick = null
})

const startResize = (e) => {
  isResizing.value = true
  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResize)
  document.body.style.cursor = 'ew-resize'
  document.body.style.userSelect = 'none'
}

const handleResize = (e) => {
  if (!isResizing.value) return
  
  const newWidth = window.innerWidth - e.clientX
  
  if (newWidth >= 400 && newWidth <= 1200) {
    chatWidth.value = newWidth
  }
}

const stopResize = () => {
  isResizing.value = false
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
  document.body.style.cursor = ''
  document.body.style.userSelect = ''
}
</script>

<style scoped>
.ai-chat-wrapper {
  position: fixed;
  z-index: 9999;
  transition: all 0.3s ease;
  right: 20px;
  bottom: 20px;
}

.ai-chat-button {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: transparent;
  color: var(--FontWhite);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  position: relative;
}

.ai-chat-button::before {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: var(--Color);
  opacity: 0.4;
  animation: pulse 2s ease-in-out infinite;
}

.ai-chat-button:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.25);
}

.ai-chat-button:hover::before {
  animation: pulse-hover 1s ease-in-out infinite;
}

.ai-icon-svg {
  width: 56px;
  height: 56px;
  position: relative;
  z-index: 1;
  animation: float 3s ease-in-out infinite;
}

.ai-chat-button:hover .ai-icon-svg {
  animation: float-hover 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.4;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.2;
  }
}

@keyframes pulse-hover {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.3);
    opacity: 0.3;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}

@keyframes float-hover {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-2px) scale(1.05);
  }
}

.ai-chat-window {
  position: fixed;
  top: 0;
  right: 0;
  min-width: 400px;
  max-width: 1200px;
  height: 100vh;
  background: var(--BgWhite);
  box-shadow: -5px 0 15px rgba(0, 0, 0, 0.1);
  display: flex;
  overflow: hidden;
  transform: translateX(100%);
  animation: slideIn 0.3s ease forwards;
  border-left: 1px solid var(--BorderBlack10);
}

.resize-handle {
  position: absolute;
  left: 0;
  top: 0;
  width: 12px;
  height: 100%;
  cursor: ew-resize;
  background: transparent;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
}

.resize-icon {
  width: 4px;
  height: 40px;
  background: var(--BorderBlack10);
  border-radius: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease;
}

.resize-icon svg {
  width: 12px;
  height: 12px;
  color: var(--FontBlack5);
  opacity: 0.5;
}

.resize-handle:hover .resize-icon {
  background: var(--Color);
}

.resize-handle:hover .resize-icon svg {
  opacity: 1;
  color: var(--FontWhite);
}

@keyframes slideIn {
  to {
    transform: translateX(0);
  }
}

.ai-chat-sidebar {
  width: 0;
  background: var(--BgBlack11);
  border-right: 1px solid var(--BorderBlack10);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: width 0.3s ease;
}

.ai-chat-sidebar.sidebar-open {
  width: 250px;
}

.sidebar-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--BorderBlack10);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--BgWhite);
}

.sidebar-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--FontBlack);
}

.sidebar-new-chat {
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 500;
  background: linear-gradient(135deg, var(--Color) 0%, var(--ColorDark) 100%);
  color: var(--FontWhite);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.sidebar-new-chat:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.sidebar-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.chat-item {
  padding: 12px 16px;
  border-radius: var(--Radius);
  transition: all 0.2s ease;
  margin-bottom: 8px;
  background: var(--BgWhite);
  border: 1px solid var(--BorderBlack10);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.chat-item-content {
  flex: 1;
  cursor: pointer;
  padding-right: 8px;
  min-width: 0;
}

.chat-item:hover {
  background: var(--BgBlack11);
  border-color: var(--BorderBlack6);
}

.chat-item.active {
  background: var(--ColorLight12);
  border-color: var(--Color);
}

.chat-item-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--FontBlack);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  min-width: 0;
}

.chat-item-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.chat-item-mode {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 8px;
  flex-shrink: 0;
  background: var(--BgBlack11);
  color: var(--FontBlack5);
  border: 1px solid var(--BorderBlack10);
}

.chat-item-mode.general {
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
  color: #667eea;
  border-color: #667eea40;
}

.chat-item-mode.navigation {
  background: linear-gradient(135deg, #f093fb20 0%, #f5576c20 100%);
  color: #f5576c;
  border-color: #f5576c40;
}

.chat-item-last-message {
  font-size: 12px;
  color: var(--FontBlack6);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-item-time {
  font-size: 11px;
  color: var(--FontBlack7);
  text-align: right;
}

.chat-item-actions {
  display: flex;
  gap: 4px;
}

.chat-item-edit,
.chat-item-delete {
  background: none;
  border: none;
  color: var(--FontBlack7);
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.chat-item-edit:hover {
  background: var(--BgBlack10);
  color: var(--Color);
}

.chat-item-delete:hover {
  background: var(--BgBlack10);
  color: var(--ColorDanger);
}

.required {
  color: var(--ColorDanger);
  margin-left: 2px;
}

.ai-chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ai-chat-header {
  background: var(--BgWhite);
  color: var(--FontBlack);
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--BorderBlack10);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--FontBlack);
}

.header-mode-tag {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 12px;
  background: var(--BgBlack11);
  color: var(--FontBlack5);
  border: 1px solid var(--BorderBlack10);
  margin-left: 8px;
}

.header-mode-tag.general {
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
  color: #667eea;
  border-color: #667eea40;
}

.header-mode-tag.navigation {
  background: linear-gradient(135deg, #f093fb20 0%, #f5576c20 100%);
  color: #f5576c;
  border-color: #f5576c40;
}

.sidebar-toggle {
  background: none;
  border: none;
  color: var(--FontBlack5);
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.sidebar-toggle:hover {
  background: var(--BgBlack10);
  color: var(--FontBlack);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-action {
  font-size: 14px;
  color: var(--FontBlack5);
  cursor: pointer;
  transition: color 0.2s ease;
}

.header-action:hover {
  color: var(--Color);
}

.close-button {
  background: none;
  border: none;
  color: var(--FontBlack5);
  font-size: 20px;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-button:hover {
  background-color: var(--BgBlack10);
  color: var(--FontBlack);
}

.ai-chat-body {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background-color: var(--BgWhite);
  position: relative;
}

.watermark-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.watermark {
  position: absolute;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.2);
  white-space: nowrap;
  user-select: none;
  letter-spacing: 2px;
  pointer-events: none;
}

.message {
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
}

.message.user {
  align-items: flex-end;
}

.message.ai {
  align-items: flex-start;
}

.message-content {
  max-width: 85%;
  padding: 16px 20px;
  border-radius: var(--Radius);
  position: relative;
}

.message-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 8px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.message.user .message-footer {
  border-top-color: rgba(0, 0, 0, 0.1);
  flex-direction: row-reverse;
}

.message.user .message-time {
  color: rgba(0, 0, 0, 0.5);
}

.message-time {
  font-size: 12px;
  opacity: 0.6;
}

.ai-disclaimer {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed var(--BorderBlack10);
  font-size: 11px;
  color: var(--FontBlack5);
  opacity: 0.7;
}

.ai-disclaimer .el-icon {
  font-size: 12px;
}

.ai-disclaimer svg {
  width: 12px;
  height: 12px;
}

.message-actions {
  display: flex;
  gap: 4px;
  opacity: 1;
  transition: opacity 0.2s ease;
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: inherit;
  opacity: 0.7;
}

.action-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  opacity: 1;
}

.action-btn:disabled {
  cursor: not-allowed;
  opacity: 0.3;
}

.message.user .action-btn:hover {
  background: rgba(0, 0, 0, 0.05);
}

.action-btn .el-icon {
  font-size: 14px;
}

.input-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: flex-end;
}

.input-wrapper .el-textarea {
  flex: 1;
}

.input-actions {
  position: absolute;
  right: 8px;
  bottom: 8px;
  display: flex;
  gap: 4px;
}

.input-row {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  width: 100%;
}

.message.user .message-content {
  background: var(--BgBlack11);
  color: var(--FontBlack);
  border-bottom-right-radius: 4px;
  border: 1px solid var(--BorderBlack10);
}

.message.ai .message-content {
  background: var(--BgBlack11);
  color: var(--FontBlack);
  border-bottom-left-radius: 4px;
  border: 1px solid var(--BorderBlack10);
}

.message-text {
  line-height: 1.5;
  word-break: break-word;
  font-size: 15px;
}

.message-time {
  font-size: 12px;
  color: var(--FontBlack6);
  margin-top: 6px;
  text-align: right;
}

.message.ai .message-time {
  text-align: left;
}

.loading-message {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px 32px;
  color: var(--FontBlack5);
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  border-radius: var(--Radius);
  border: 1px solid var(--BorderBlack10);
  max-width: 60%;
  margin: 40px auto;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.01);
  }
}

.loading-message .el-icon {
  animation: spin 1.5s linear infinite;
  font-size: 20px;
  color: var(--Color);
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-message span {
  margin-left: 12px;
  font-size: 15px;
  background: linear-gradient(90deg, var(--Color) 0%, #a78bfa 50%, var(--Color) 100%);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradient 2s ease infinite;
}

@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.ai-chat-footer {
  padding: 24px;
  border-top: 1px solid var(--BorderBlack10);
  background: var(--BgWhite);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.model-selector {
  display: flex;
  align-items: center;
  gap: 8px;
}

.footer-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.function-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: var(--BgBlack11);
  border: 1px solid var(--BorderBlack10);
  color: var(--FontBlack);
  transition: all 0.2s ease;
}

.function-btn:hover {
  background: var(--ColorLight12);
  border-color: var(--Color);
  color: var(--Color);
}

.function-btn .el-icon {
  font-size: 14px;
}

.function-panel {
  padding: 0;
  margin: -12px;
}

.function-panel-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--FontBlack);
  padding: 8px 16px;
  margin-bottom: 8px;
  border-bottom: 1px solid var(--BorderBlack10);
}

.function-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  gap: 12px;
}

.function-item:hover {
  background: var(--BgBlack11);
}

.function-item.active {
  background: var(--ColorLight12);
}

.function-item.active .function-icon {
  color: var(--Color);
}

.function-item.active .function-name {
  color: var(--Color);
}

.function-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--BgBlack11);
  border-radius: 8px;
  color: var(--FontBlack5);
  flex-shrink: 0;
}

.function-icon svg {
  width: 20px;
  height: 20px;
}

.function-info {
  flex: 1;
  min-width: 0;
}

.function-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--FontBlack);
  margin-bottom: 2px;
}

.function-desc {
  font-size: 12px;
  color: var(--FontBlack5);
}

.function-check {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--Color);
  flex-shrink: 0;
}

.function-check svg {
  width: 20px;
  height: 20px;
}

.function-btn.has-selection {
  background: var(--ColorLight12);
  border-color: var(--Color);
  color: var(--Color);
}

.settings-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: var(--BgBlack11);
  border: 1px solid var(--BorderBlack10);
  color: var(--FontBlack);
  transition: all 0.2s ease;
}

.settings-btn:hover {
  background: var(--ColorLight12);
  border-color: var(--Color);
  color: var(--Color);
}

.settings-btn .el-icon {
  font-size: 14px;
}

.settings-action {
  font-size: 14px;
  color: var(--FontBlack5);
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.settings-action:hover {
  color: var(--Color);
  background: var(--ColorLight12);
}

.new-chat-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.new-chat-form .form-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  background: var(--BgBlack10);
  border-radius: 8px;
}

.new-chat-form .form-section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--FontBlack);
  padding-bottom: 8px;
  border-bottom: 1px solid var(--BorderBlack9);
}

.new-chat-form .form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.new-chat-form .mode-list {
  display: flex;
  gap: 12px;
}

.new-chat-form .mode-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 2px solid var(--BorderBlack9);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.new-chat-form .mode-card:hover {
  border-color: var(--Color);
  background: var(--ColorLight12);
}

.new-chat-form .mode-card.selected {
  border-color: var(--Color);
  background: var(--ColorLight12);
}

.new-chat-form .mode-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  flex-shrink: 0;
}

.new-chat-form .mode-icon svg {
  width: 24px;
  height: 24px;
}

.new-chat-form .mode-icon.general-icon {
  background: linear-gradient(135deg, #667eea20, #764ba220);
  color: #667eea;
}

.new-chat-form .mode-icon.navigation-icon {
  background: linear-gradient(135deg, #f093fb20, #f5576c20);
  color: #f5576c;
}

.new-chat-form .mode-info {
  flex: 1;
  min-width: 0;
}

.new-chat-form .mode-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--FontBlack);
}

.new-chat-form .mode-desc {
  font-size: 12px;
  color: var(--FontBlack7);
  margin-top: 2px;
}

.new-chat-form .mode-check {
  position: absolute;
  top: 8px;
  right: 8px;
}

.new-chat-form .mode-display {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  background: var(--BgBlack10);
  border-radius: 6px;
}

.new-chat-form .mode-tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.new-chat-form .mode-tag.general {
  background: linear-gradient(135deg, #667eea20, #764ba220);
  color: #667eea;
}

.new-chat-form .mode-tag.navigation {
  background: linear-gradient(135deg, #f093fb20, #f5576c20);
  color: #f5576c;
}

.new-chat-form .mode-hint {
  font-size: 12px;
  color: var(--FontBlack7);
}

.new-chat-form .mode-hint-text {
  font-size: 12px;
  color: var(--FontBlack7);
  margin-top: 8px;
}

.new-chat-form .mode-card.disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.slider-hint {
  font-size: 12px;
  color: var(--FontBlack7);
  margin-top: 4px;
}

.new-chat-form .form-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--FontBlack);
}

.function-dialog .function-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.function-card {
  border: 2px solid var(--BorderBlack10);
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: var(--BgWhite);
}

.function-card:hover {
  border-color: var(--Color);
  background: var(--ColorLight12);
}

.function-card.selected {
  border-color: var(--Color);
  background: var(--ColorLight12);
}

.function-card .card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.function-card .card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--BgBlack11);
  color: var(--FontBlack5);
}

.function-card .card-icon svg {
  width: 24px;
  height: 24px;
}

.function-card .card-icon.general-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.function-card .card-icon.navigation-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.function-card .card-radio {
  flex-shrink: 0;
}

.function-card .card-body {
  text-align: left;
}

.function-card .card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--FontBlack);
  margin-bottom: 6px;
}

.function-card .card-desc {
  font-size: 13px;
  color: var(--FontBlack5);
  line-height: 1.5;
}

.function-card.selected .card-title {
  color: var(--Color);
}

.model-label {
  font-size: 14px;
  color: var(--FontBlack5);
  font-weight: 500;
}

.ai-chat-footer .el-input {
  flex: 1;
}

.send-button {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--Color) 0%, var(--ColorDark) 100%);
  color: var(--FontWhite);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  flex-shrink: 0;
}

.send-button:hover:not(:disabled) {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, var(--ColorDark) 0%, var(--Color) 100%);
}

.send-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.send-button .el-icon {
  font-size: 24px;
}

.ai-chat-body::-webkit-scrollbar {
  width: 6px;
}

.ai-chat-body::-webkit-scrollbar-track {
  background: var(--BgBlack10);
  border-radius: 3px;
}

.ai-chat-body::-webkit-scrollbar-thumb {
  background: var(--BorderBlack6);
  border-radius: 3px;
}

.ai-chat-body::-webkit-scrollbar-thumb:hover {
  background: var(--BorderBlack5);
}

.param-description {
  font-size: 12px;
  color: var(--FontBlack5);
  margin-top: 8px;
  line-height: 1.5;
}

.slider-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
}

.slider-wrapper .el-slider {
  flex: 1;
}

.slider-value {
  min-width: 40px;
  padding: 4px 12px;
  background: var(--ColorLight12);
  color: var(--Color);
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  text-align: center;
}

/* Markdown渲染样式 */
.message-text :deep(.md-paragraph) {
  margin: 0 0 12px 0;
  line-height: 1.6;
}

.message-text :deep(.md-paragraph:last-child) {
  margin-bottom: 0;
}

.message-text :deep(.md-h1) {
  font-size: 24px;
  font-weight: 700;
  margin: 16px 0 12px 0;
  color: var(--FontBlack);
  border-bottom: 2px solid var(--BorderBlack10);
  padding-bottom: 8px;
}

.message-text :deep(.md-h2) {
  font-size: 20px;
  font-weight: 600;
  margin: 14px 0 10px 0;
  color: var(--FontBlack);
}

.message-text :deep(.md-h3) {
  font-size: 18px;
  font-weight: 600;
  margin: 12px 0 8px 0;
  color: var(--FontBlack);
}

.message-text :deep(.md-bold) {
  font-weight: 700;
  color: var(--FontBlack);
}

.message-text :deep(.md-italic) {
  font-style: italic;
  color: var(--FontBlack3);
}

.message-text :deep(.inline-code) {
  background: var(--BgBlack11);
  color: var(--Color);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  border: 1px solid var(--BorderBlack10);
}

.message-text :deep(.code-block) {
  background: var(--BgBlack11);
  border: 1px solid var(--BorderBlack10);
  border-radius: 8px;
  padding: 16px;
  margin: 12px 0;
  overflow-x: auto;
  position: relative;
}

.message-text :deep(.code-block code) {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: var(--FontBlack);
  display: block;
  white-space: pre;
}

.message-text :deep(.md-link) {
  color: var(--Color);
  text-decoration: none;
  border-bottom: 1px dashed var(--Color);
  transition: all 0.2s ease;
}

.message-text :deep(.md-link:hover) {
  color: var(--ColorDark);
  border-bottom-style: solid;
}

.message-text :deep(.md-list) {
  margin: 8px 0;
  padding-left: 24px;
}

.message-text :deep(.md-list-item) {
  margin: 6px 0;
  line-height: 1.6;
  position: relative;
}

.message-text :deep(.md-list-item::before) {
  content: '•';
  position: absolute;
  left: -16px;
  color: var(--Color);
  font-weight: bold;
}

.message-text :deep(.md-ordered-item) {
  margin: 6px 0;
  line-height: 1.6;
  padding-left: 8px;
}

.message-text :deep(.md-quote) {
  border-left: 4px solid var(--Color);
  padding: 8px 16px;
  margin: 12px 0;
  background: var(--BgBlack11);
  color: var(--FontBlack3);
  border-radius: 0 4px 4px 0;
  font-style: italic;
}

.message-text :deep(.md-hr) {
  border: none;
  border-top: 1px solid var(--BorderBlack10);
  margin: 16px 0;
}

/* 用户消息的Markdown样式调整 */
.message.user .message-text :deep(.inline-code) {
  background: rgba(0, 0, 0, 0.05);
  color: var(--FontBlack);
  border-color: rgba(0, 0, 0, 0.1);
}

.message.user .message-text :deep(.code-block) {
  background: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.1);
}

.message.user .message-text :deep(.code-block code) {
  color: var(--FontBlack);
}

.message.user .message-text :deep(.md-link) {
  color: #7c3aed;
  border-bottom-color: #7c3aed;
}

.message.user .message-text :deep(.md-quote) {
  background: rgba(0, 0, 0, 0.03);
  border-left-color: #7c3aed;
  color: rgba(0, 0, 0, 0.7);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.message-text :deep(.md-route-link) {
  color: var(--Color);
  text-decoration: none;
  border-bottom: 1px dashed var(--Color);
  transition: all 0.2s ease;
  cursor: pointer;
  background: var(--ColorLight12);
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

.message-text :deep(.md-route-link:hover) {
  color: var(--FontWhite);
  background: var(--Color);
  border-bottom-style: solid;
}

.message-text :deep(.route-recommendations) {
  margin: 12px 0;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  border-radius: 12px;
  border: 1px solid var(--BorderBlack10);
}

.message-text :deep(.route-recommendations-title) {
  font-size: 14px;
  font-weight: 600;
  color: var(--FontBlack);
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--BorderBlack10);
}

.message-text :deep(.route-recommendations-list) {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message-text :deep(.route-item) {
  padding: 12px 16px;
  background: var(--BgWhite);
  border: 2px solid var(--BorderBlack10);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.message-text :deep(.route-item:hover) {
  border-color: var(--Color);
  background: var(--ColorLight12);
  transform: translateX(4px);
}

.message-text :deep(.route-item-header) {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.message-text :deep(.route-item-index) {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--Color) 0%, var(--ColorDark) 100%);
  color: var(--FontWhite);
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
}

.message-text :deep(.route-item-title) {
  font-size: 15px;
  font-weight: 600;
  color: var(--FontBlack);
}

.message-text :deep(.route-item-reason) {
  font-size: 13px;
  color: var(--FontBlack5);
  line-height: 1.5;
  margin-bottom: 6px;
}

.message-text :deep(.route-item-path) {
  font-size: 12px;
  color: var(--Color);
  font-weight: 500;
}
</style>
