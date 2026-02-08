<template>
  <div class="chat-room">
    <!-- 聊天记录区域 -->
    <div ref="messagesContainer" class="messages-container">
      <div v-if="messages.length === 0" class="empty-state">
        <div class="empty-icon">🏠</div>
        <h3>智能租房助手</h3>
        <p>描述您的租房需求，我来帮您找到合适的房源</p>
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
        :class="['message', message.type]"
      >
        <!-- 助手消息 -->
        <div v-if="message.type === 'ai'" class="message-content assistant">
          <div class="avatar assistant-avatar">🏠</div>
          <div class="bubble">
            <div class="message-text" v-html="formatMessage(message.text)"></div>
            <div class="message-meta">
              <span class="time">{{ formatTime(message.timestamp) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 用户消息 - 头像在右边 -->
        <div v-else class="message-content user">
          <div class="avatar user-avatar">我</div>
          <div class="bubble">
            <div class="message-text">{{ message.text }}</div>
            <div class="message-meta">
              <span class="time">{{ formatTime(message.timestamp) }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 助手思考中 -->
      <div v-if="isLoading" class="message assistant">
        <div class="message-content assistant">
          <div class="avatar assistant-avatar">🏠</div>
          <div class="bubble">
            <div class="thinking">
              <div class="dot-flashing"></div>
              <span>正在查询中...</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 输入区域 -->
    <div class="input-area">
      <form @submit.prevent="sendMessage" class="input-form">
        <div class="input-wrapper">
          <textarea
            v-model="userInput"
            @keydown.enter.exact.prevent="sendMessage"
            @keydown.enter.shift.exact.prevent="userInput += '\n'"
            placeholder="描述你的租房需求，如：朝阳区两居室，预算8000以内..."
            :disabled="isLoading"
            rows="1"
            ref="textareaRef"
            class="message-input"
          ></textarea>
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
        
        <div class="input-footer">
          <span class="hint">
            按 Enter 发送，Shift + Enter 换行
          </span>
          <div class="actions">
            <button 
              type="button" 
              @click="clearMessages" 
              class="action-btn"
              :disabled="messages.length === 0"
            >
              清空对话
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, nextTick, onMounted } from 'vue'
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
    let eventSource = null

    // 快捷问题
    const quickQuestions = [
      '朝阳区有什么房源？',
      '5000元以下的两居室',
      '海淀区精装修房源',
      '浦东新区整租房源',
      '有哪些区域可以选择？'
    ]

    // 自动调整文本域高度
    const adjustTextareaHeight = () => {
      if (textareaRef.value) {
        textareaRef.value.style.height = 'auto'
        textareaRef.value.style.height = `${textareaRef.value.scrollHeight}px`
      }
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
        
        eventSource.onmessage = (event) => {
          const chunk = event.data
          // 更新最后一条 AI 消息
          messages.value[aiMessageIndex].text += chunk
          scrollToBottom()
        }
        
        eventSource.onerror = (error) => {
          console.error('SSE 错误:', error)
          if (eventSource) {
            eventSource.close()
          }
          isLoading.value = false
          
          // 如果 AI 消息为空，设置为错误信息
          if (!messages.value[aiMessageIndex].text) {
            messages.value[aiMessageIndex].text = '抱歉，连接出现错误，请重试。'
          }
        }
        
        eventSource.addEventListener('end', () => {
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

    // 添加消息
    const addMessage = (text, type) => {
      messages.value.push({
        text,
        type,
        timestamp: new Date()
      })
      scrollToBottom()
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
      }
    }

    // 开始新会话
    const startNewSession = () => {
      clearMessages()
      emit('new-session')
    }

    // 滚动到底部
    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
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

    // 监听会话 ID 变化，清空消息
    watch(() => props.sessionId, () => {
      messages.value = []
      if (eventSource) {
        eventSource.close()
        eventSource = null
      }
    })

    onMounted(() => {
      adjustTextareaHeight()
    })

    return {
      messages,
      userInput,
      isLoading,
      textareaRef,
      messagesContainer,
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
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
  background: #f8fafc;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #64748b;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  margin: 1rem 0;
  color: #334155;
  font-size: 1.5rem;
}

.empty-state p {
  margin-bottom: 2rem;
  line-height: 1.6;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.5rem;
  max-width: 600px;
  margin: 0 auto;
}

.quick-btn {
  background: #f0f5ff;
  color: #1890ff;
  border: 1px solid #d6e4ff;
  padding: 0.5rem 1rem;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  white-space: nowrap;
}

.quick-btn:hover {
  background: #e6f7ff;
  border-color: #91d5ff;
  transform: translateY(-2px);
}

.message {
  margin-bottom: 1.5rem;
}

.message-content {
  display: flex;
  align-items: flex-end;
  gap: 0.75rem;
  max-width: 80%;
}

.message-content.assistant {
  margin-right: auto;
}

.message-content.user {
  margin-left: auto;
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  flex-shrink: 0;
}

.assistant-avatar {
  background: #f0f5ff;
  color: #1890ff;
  font-size: 1.2rem;
}

.user-avatar {
  background: #1890ff;
  color: white;
  font-size: 0.85rem;
  font-weight: 500;
}

.bubble {
  background: white;
  padding: 1rem 1.25rem;
  border-radius: 18px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  position: relative;
  max-width: 100%;
  word-wrap: break-word;
}

.message-content.assistant .bubble {
  border-bottom-left-radius: 4px;
  background: #f5f7fa;
}

.message-content.user .bubble {
  border-bottom-right-radius: 4px;
  background: #1890ff;
  color: white;
}

.message-text {
  line-height: 1.6;
  font-size: 1rem;
}

.message-text :deep(code) {
  background: rgba(0, 0, 0, 0.1);
  padding: 0.2rem 0.4rem;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 0.9em;
}

.message-content.user .message-text :deep(code) {
  background: rgba(255, 255, 255, 0.2);
}

.message-meta {
  margin-top: 0.5rem;
  font-size: 0.75rem;
  opacity: 0.7;
  display: flex;
  justify-content: flex-end;
}

.thinking {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #64748b;
}

.dot-flashing {
  position: relative;
  width: 8px;
  height: 8px;
  border-radius: 4px;
  background-color: #1890ff;
  color: #1890ff;
  animation: dotFlashing 1s infinite linear alternate;
  animation-delay: 0.5s;
}

.dot-flashing::before,
.dot-flashing::after {
  content: '';
  display: inline-block;
  position: absolute;
  top: 0;
}

.dot-flashing::before {
  left: -12px;
  width: 8px;
  height: 8px;
  border-radius: 4px;
  background-color: #1890ff;
  color: #1890ff;
  animation: dotFlashing 1s infinite alternate;
  animation-delay: 0s;
}

.dot-flashing::after {
  left: 12px;
  width: 8px;
  height: 8px;
  border-radius: 4px;
  background-color: #1890ff;
  color: #1890ff;
  animation: dotFlashing 1s infinite alternate;
  animation-delay: 1s;
}

@keyframes dotFlashing {
  0% {
    background-color: #1890ff;
  }
  50%,
  100% {
    background-color: rgba(24, 144, 255, 0.2);
  }
}

.input-area {
  background: white;
  border-top: 1px solid #e2e8f0;
  padding: 1rem;
}

.input-form {
  max-width: 800px;
  margin: 0 auto;
}

.input-wrapper {
  display: flex;
  gap: 0.75rem;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  resize: none;
  max-height: 120px;
  transition: all 0.3s ease;
  font-family: inherit;
}

.message-input:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.message-input:disabled {
  background-color: #f8fafc;
  cursor: not-allowed;
}

.send-btn {
  width: 44px;
  height: 44px;
  background: #1890ff;
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  background: #40a9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.send-icon {
  width: 24px;
  height: 24px;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.75rem;
}

.hint {
  font-size: 0.8rem;
  color: #64748b;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  background: none;
  border: 1px solid #e2e8f0;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.85rem;
}

.action-btn:hover:not(:disabled) {
  background-color: #f1f5f9;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .chat-room {
    height: calc(100vh - 200px);
  }
  
  .messages-container {
    padding: 1rem;
  }
  
  .message-content {
    max-width: 90%;
  }
  
  .avatar {
    width: 32px;
    height: 32px;
    font-size: 1.2rem;
  }
}
</style>