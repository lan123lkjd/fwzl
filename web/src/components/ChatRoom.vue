<template>
  <div class="chat-room">
    <!-- 聊天记录区域 -->
    <div ref="messagesContainer" class="messages-container">
      <div v-if="messages.length === 0" class="empty-state">
        <div class="empty-icon">🏠</div>
        <h3>开始与 AI 对话</h3>
        <p>我是你的 AI 房屋租赁助手，可以帮你搜索房源、推荐合适的房子</p>
        <div class="quick-questions">
          <button 
            v-for="question in quickQuestions" 
            :key="question"
            @click="sendQuickQuestion(question)"
            class="quick-btn"
          >
            {{ question }}
          </button>
        </div>
      </div>
      
      <div 
        v-for="(message, index) in messages" 
        :key="index" 
        class="message"
      >
        <!-- AI 消息 -->
        <div v-if="message.type === 'ai'" class="message-content ai">
          <div class="avatar">🤖</div>
          <div class="bubble">
            <div class="message-text" v-html="formatMessage(message.text)"></div>
            <div class="message-meta">
              <span class="time">{{ formatTime(message.timestamp) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 用户消息 (已修复结构) -->
        <div v-else class="message-content user">
          <div class="bubble">
            <div class="message-text">{{ message.text }}</div>
            <div class="message-meta">
              <span class="time">{{ formatTime(message.timestamp) }}</span>
            </div>
          </div>
          <div class="avatar">👤</div>
        </div>
      </div>
      
      <!-- AI 思考中 -->
      <div v-if="isLoading" class="message">
        <div class="message-content ai">
          <div class="avatar">🤖</div>
          <div class="bubble">
            <div class="thinking">
              <div class="dot-flashing"></div>
              <span>AI 正在思考中...</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-container">
        <form @submit.prevent="sendMessage" class="input-form">
          <div class="input-wrapper">
            <textarea
              v-model="userInput"
              @keydown.enter.exact.prevent="sendMessage"
              @keydown.enter.shift.exact.prevent="userInput += '\n'"
              placeholder="输入你的租房需求，如：青秀区两居室，预算2500以内..."
              :disabled="isLoading"
              rows="1"
              ref="textareaRef"
              class="message-input"
              :style="{ height: textareaHeight }"
            ></textarea>
            
            <div class="input-actions">
              <button 
                type="button"
                class="action-icon-btn"
                title="清空对话"
                @click="clearMessages"
                :disabled="messages.length === 0"
              >
                <svg class="icon" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                </svg>
              </button>
              
              <button 
                type="submit" 
                :disabled="!userInput.trim() || isLoading" 
                class="send-btn"
              >
                <svg class="send-icon" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                </svg>
              </button>
            </div>
          </div>
          
          <div class="input-footer">
            <span class="hint">
              <span class="hint-icon">⌨️</span>
              Enter 发送，Shift + Enter 换行
            </span>
            <span class="char-count" v-if="userInput.length > 0">
              {{ userInput.length }} / 2000
            </span>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

dayjs.locale('zh-cn')

export default {
  name: 'ChatRoom',
  props: {
    sessionId: {
      type: Number,
      required: true
    }
  },
  emits: ['new-session'],
  setup(props, { emit }) {
    // 响应式数据
    const messages = ref([])
    const userInput = ref('')
    const isLoading = ref(false)
    const textareaRef = ref(null)
    const messagesContainer = ref(null)
    const textareaHeight = ref('auto')
    let eventSource = null

    // 快捷问题
    const quickQuestions = [
      '青秀区有什么房源？',
      '2000元以下的两居室',
      '西乡塘区精装修房源',
      '江南区整租房源',
      '有哪些区域可以选择？'
    ]

    // 自动调整文本域高度
    const adjustTextareaHeight = () => {
      if (textareaRef.value) {
        textareaRef.value.style.height = 'auto'
        const scrollHeight = textareaRef.value.scrollHeight
        const newHeight = Math.min(scrollHeight, 120)
        textareaHeight.value = `${newHeight}px`
      }
    }

    // 滚动到底部
    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }

    // 添加消息
    const addMessage = (text, type) => {
      messages.value.push({
        text,
        type,
        timestamp: new Date()
      })
      scrollToBottom()
    }

    // 发送消息
    const sendMessage = async () => {
      const message = userInput.value.trim()
      if (!message || isLoading.value) return
      
      // 添加用户消息
      addMessage(message, 'user')
      userInput.value = ''
      adjustTextareaHeight()
      
      // 开始加载
      isLoading.value = true
      
      try {
        // 关闭之前的 SSE 连接
        if (eventSource) {
          eventSource.close()
        }
        
        // 初始化 AI 消息
        const aiMessageIndex = messages.value.length
        addMessage('', 'ai')
        
        // 创建 SSE 连接
        const url = `http://localhost:8080/api/ai/chat?memoryId=${props.sessionId}&message=${encodeURIComponent(message)}`
        eventSource = new EventSource(url)
        
        let isEnded = false
        
        eventSource.onmessage = (event) => {
          const chunk = event.data
          messages.value[aiMessageIndex].text += chunk
          scrollToBottom()
        }
        
        eventSource.onerror = () => {
          if (isEnded) return
          if (eventSource) {
            eventSource.close()
          }
          isLoading.value = false
          
          if (!messages.value[aiMessageIndex].text) {
            messages.value[aiMessageIndex].text = '抱歉，连接出现错误，请重试。'
          }
        }
        
        eventSource.addEventListener('end', () => {
          isEnded = true
          if (eventSource) {
            eventSource.close()
          }
          isLoading.value = false
        })
        
      } catch (error) {
        console.error('发送消息失败:', error)
        isLoading.value = false
        addMessage('抱歉，发送消息时出现错误，请重试。', 'ai')
      }
    }

    // 发送快捷问题
    const sendQuickQuestion = (question) => {
      userInput.value = question
      sendMessage()
    }

    // 清空消息
    const clearMessages = () => {
      if (confirm('确定要清空所有对话记录吗？')) {
        messages.value = []
        if (eventSource) {
          eventSource.close()
          eventSource = null
        }
        isLoading.value = false
        scrollToBottom()
      }
    }

    // 开始新会话
    const startNewSession = () => {
      clearMessages()
      emit('new-session')
    }

    // 格式化消息（支持简单 Markdown）
    const formatMessage = (text) => {
      if (!text) return ''
      return text
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*(.*?)\*/g, '<em>$1</em>')
        .replace(/`(.*?)`/g, '<code>$1</code>')
        .replace(/\n/g, '<br>')
    }

    // 格式化时间
    const formatTime = (timestamp) => {
      return dayjs(timestamp).format('HH:mm')
    }

    // 监听文本变化调整高度
    watch(userInput, adjustTextareaHeight)
    
    // 监听消息变化，自动滚动到底部
    watch(messages, () => {
      scrollToBottom()
    }, { deep: true })

    // 监听会话 ID 变化，清空消息
    watch(() => props.sessionId, () => {
      messages.value = []
      if (eventSource) {
        eventSource.close()
        eventSource = null
      }
      scrollToBottom()
    })

    onMounted(() => {
      adjustTextareaHeight()
      scrollToBottom()
    })

    onUnmounted(() => {
      if (eventSource) {
        eventSource.close()
      }
    })

    return {
      messages,
      userInput,
      isLoading,
      textareaRef,
      messagesContainer,
      textareaHeight,
      quickQuestions,
      sendMessage,
      sendQuickQuestion,
      clearMessages,
      startNewSession,
      formatMessage,
      formatTime
    }
  }
}
</script>

<style scoped>
.chat-room {
  height: 70vh;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
  background: #f8fafc;
  scroll-behavior: smooth;
  
  /* 隐藏滚动条但保持滚动功能 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE 和 Edge */
}

.messages-container::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera */
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #64748b;
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.empty-state h3 {
  margin: 1rem 0;
  color: #334155;
  font-size: 1.5rem;
  font-weight: 600;
}

.empty-state p {
  margin-bottom: 2rem;
  line-height: 1.6;
  color: #475569;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.75rem;
  max-width: 600px;
  margin: 0 auto;
}

.quick-btn {
  background: white;
  color: #4f46e5;
  border: 1px solid #e0e7ff;
  padding: 0.6rem 1.2rem;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(79, 70, 229, 0.05);
}

.quick-btn:hover {
  background: #4f46e5;
  color: white;
  border-color: #4f46e5;
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(79, 70, 229, 0.2);
}

.message {
  margin-bottom: 1.5rem;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-content {
  display: flex;
  align-items: flex-end;
  gap: 0.75rem;
  max-width: 80%;
}

.message-content.ai {
  margin-right: auto;
}

.message-content.user {
  margin-left: auto;
  justify-content: flex-end;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  flex-shrink: 0;
}

/* AI 头像样式 - 紫色 */
.message-content.ai .avatar {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

/* 用户头像样式 - 蓝色 */
.message-content.user .avatar {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.bubble {
  padding: 1rem 1.25rem;
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  position: relative;
  max-width: 100%;
  word-wrap: break-word;
}

/* AI 气泡样式 - 白色 */
.message-content.ai .bubble {
  background: white;
  color: #1e293b;
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 20px;
}

/* 用户气泡样式 - 蓝色 */
.message-content.user .bubble {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border-bottom-left-radius: 20px;
  border-bottom-right-radius: 4px;
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
}

.message-text {
  line-height: 1.6;
  font-size: 1rem;
}

.message-text :deep(code) {
  background: rgba(0, 0, 0, 0.08);
  padding: 0.2rem 0.4rem;
  border-radius: 6px;
  font-family: 'Courier New', monospace;
  font-size: 0.9em;
}

.message-content.user .message-text :deep(code) {
  background: rgba(255, 255, 255, 0.2);
}

.message-meta {
  margin-top: 0.5rem;
  font-size: 0.7rem;
  opacity: 0.6;
  display: flex;
  justify-content: flex-end;
}

.thinking {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #475569;
}

.dot-flashing {
  position: relative;
  width: 8px;
  height: 8px;
  border-radius: 4px;
  background-color: #6366f1;
  animation: dotFlashing 1s infinite linear alternate;
  animation-delay: 0.5s;
}

.dot-flashing::before,
.dot-flashing::after {
  content: '';
  display: inline-block;
  position: absolute;
  top: 0;
  width: 8px;
  height: 8px;
  border-radius: 4px;
  background-color: #6366f1;
}

.dot-flashing::before {
  left: -12px;
  animation: dotFlashing 1s infinite alternate;
  animation-delay: 0s;
}

.dot-flashing::after {
  left: 12px;
  animation: dotFlashing 1s infinite alternate;
  animation-delay: 1s;
}

@keyframes dotFlashing {
  0% {
    background-color: #6366f1;
    opacity: 1;
  }
  50%, 100% {
    background-color: #6366f1;
    opacity: 0.3;
  }
}

/* 输入区域样式 */
.input-area {
  background: white;
  padding: 1.5rem 2rem 2rem;
  border-top: 1px solid rgba(226, 232, 240, 0.6);
}

.input-container {
  max-width: 800px;
  margin: 0 auto;
}

.input-form {
  width: 100%;
}

.input-wrapper {
  display: flex;
  gap: 0.75rem;
  align-items: flex-end;
  background: #f8fafc;
  border-radius: 24px;
  padding: 0.5rem 0.5rem 0.5rem 1.25rem;
  border: 2px solid transparent;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.input-wrapper:focus-within {
  border-color: #6366f1;
  background: white;
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.12);
}

.message-input {
  flex: 1;
  background: transparent;
  border: none;
  padding: 0.75rem 0;
  font-size: 1rem;
  resize: none;
  max-height: 120px;
  min-height: 48px;
  font-family: inherit;
  color: #1e293b;
  line-height: 1.5;
  overflow-y: hidden;
  width: 100%;
  box-sizing: border-box;
}

.message-input::placeholder {
  color: #94a3b8;
  font-size: 0.95rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
  display: block;
}

.message-input:focus {
  outline: none;
}

.message-input:focus::placeholder {
  opacity: 0.7;
}

.message-input:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.input-actions {
  display: flex;
  gap: 0.25rem;
  align-items: center;
  flex-shrink: 0;
}

.action-icon-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 40px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-icon-btn:hover:not(:disabled) {
  background: rgba(100, 116, 139, 0.1);
  color: #475569;
}

.action-icon-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.action-icon-btn .icon {
  width: 20px;
  height: 20px;
}

.send-btn {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  border-radius: 44px;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
}

.send-btn:active:not(:disabled) {
  transform: scale(0.95);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

.send-icon {
  width: 20px;
  height: 20px;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.75rem;
  padding: 0 0.5rem;
}

.hint {
  font-size: 0.8rem;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.hint-icon {
  font-size: 1rem;
}

.char-count {
  font-size: 0.8rem;
  color: #94a3b8;
  background: #f1f5f9;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
}

@media (max-width: 768px) {
  .chat-room {
    height: calc(100vh - 180px);
    border-radius: 20px;
  }
  
  .messages-container {
    padding: 1rem;
  }
  
  .message-content {
    max-width: 90%;
  }
  
  .avatar {
    width: 36px;
    height: 36px;
    font-size: 1.2rem;
  }
  
  .input-area {
    padding: 1rem;
  }
  
  .input-wrapper {
    padding: 0.25rem 0.25rem 0.25rem 1rem;
  }
  
  .message-input {
    min-height: 44px;
    font-size: 0.95rem;
  }
  
  .send-btn {
    width: 40px;
    height: 40px;
  }
  
  .action-icon-btn {
    width: 36px;
    height: 36px;
  }
}
</style>