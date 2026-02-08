<template>
  <div class="container house-detail" v-if="house">
    <el-row :gutter="24">
      <el-col :span="16">
        <div class="gallery card">
          <el-carousel height="400px" v-if="images.length">
            <el-carousel-item v-for="(img, idx) in images" :key="idx">
              <img :src="img" style="width: 100%; height: 100%; object-fit: cover;" />
            </el-carousel-item>
          </el-carousel>
          <el-empty v-else description="暂无图片" />
        </div>
        
        <div class="main-info">
          <h1 class="title">{{ house.title }}</h1>
          <div class="price">¥{{ house.price }} <span>/月</span></div>
          
          <div class="info-grid">
            <div class="info-item"><div class="label">户型</div><div class="value">{{ house.rooms }}室{{ house.halls }}厅{{ house.bathrooms }}卫</div></div>
            <div class="info-item"><div class="label">面积</div><div class="value">{{ house.area }}㎡</div></div>
            <div class="info-item"><div class="label">楼层</div><div class="value">{{ house.floor }}/{{ house.totalFloor }}层</div></div>
            <div class="info-item"><div class="label">朝向</div><div class="value">{{ house.orientation || '-' }}</div></div>
            <div class="info-item"><div class="label">装修</div><div class="value">{{ house.decoration || '-' }}</div></div>
            <div class="info-item"><div class="label">类型</div><div class="value">{{ house.houseType === 1 ? '整租' : '合租' }}</div></div>
          </div>
        </div>
        
        <div class="card" style="margin-top: 24px">
          <h3 style="margin-bottom: 16px">房源描述</h3>
          <p style="line-height: 1.8; color: var(--text-secondary)">{{ house.description || '暂无描述' }}</p>
        </div>
        
        <div class="card" style="margin-top: 24px">
          <h3 style="margin-bottom: 16px">房源评论</h3>
          <div v-if="userStore.isLoggedIn" style="margin-bottom: 20px">
            <el-input v-model="commentContent" type="textarea" :rows="3" placeholder="写下您的评论..." />
            <el-button type="primary" style="margin-top: 12px" @click="submitComment" :disabled="!commentContent.trim()">发表评论</el-button>
          </div>
          <div v-else style="margin-bottom: 20px; color: var(--text-secondary)">
            <router-link to="/login">登录</router-link> 后发表评论
          </div>
          
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <span class="user">用户{{ comment.userId }}</span>
              <span class="time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button text size="small" @click="handleUpvote(comment)">
                <el-icon><Star /></el-icon> {{ comment.upvoteCount }}
              </el-button>
            </div>
          </div>
          <el-empty v-if="!comments.length" description="暂无评论" />
        </div>
      </el-col>
      
      <el-col :span="8">
        <div class="card">
          <h3 style="margin-bottom: 16px">预约看房</h3>
          
          <!-- 已预约状态 -->
          <div v-if="hasBooked" style="text-align: center; padding: 20px 0">
            <el-result icon="success" title="您已预约此房源" sub-title="请等待房东确认">
              <template #extra>
                <el-button type="primary" @click="$router.push('/user/orders')">查看我的预约</el-button>
              </template>
            </el-result>
          </div>
          
          <!-- 预约表单 -->
          <el-form v-else-if="userStore.isLoggedIn" :model="orderForm" label-width="80px">
            <el-form-item label="预约时间">
              <el-date-picker v-model="orderForm.orderTime" type="datetime" placeholder="选择时间" style="width: 100%" />
            </el-form-item>
            <el-form-item label="联系姓名">
              <el-input v-model="orderForm.contactName" placeholder="您的姓名" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="orderForm.contactPhone" placeholder="您的电话" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="orderForm.remark" type="textarea" :rows="2" placeholder="备注信息" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width: 100%" @click="submitOrder">提交预约</el-button>
            </el-form-item>
          </el-form>
          <div v-else style="text-align: center; color: var(--text-secondary)">
            <p>请先 <router-link to="/login">登录</router-link> 后预约看房</p>
          </div>
        </div>
        
        <div class="card" style="margin-top: 24px">
          <div style="display: flex; justify-content: space-between; align-items: center">
            <div>
              <el-icon><View /></el-icon> {{ house.viewCount }} 浏览
            </div>
            <div>
              <el-button :type="isCollected ? 'warning' : 'default'" @click="toggleCollect">
                <el-icon><Star /></el-icon> {{ isCollected ? '已收藏' : '收藏' }}
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 立即租赁 -->
        <div class="card" style="margin-top: 24px">
          <h3 style="margin-bottom: 16px">立即租赁</h3>
          <div v-if="hasRented" style="text-align: center; padding: 20px 0">
            <el-result icon="success" title="您已申请租赁此房源" sub-title="请等待房东确认">
              <template #extra>
                <el-button type="primary" @click="$router.push('/user/rentals')">查看我的租赁</el-button>
              </template>
            </el-result>
          </div>
          <div v-else-if="userStore.isLoggedIn" style="text-align: center; padding: 10px 0">
            <el-button type="success" size="large" style="width: 100%" @click="showRentalDialog = true">
              <el-icon><Tickets /></el-icon> 立即租赁
            </el-button>
          </div>
          <div v-else style="text-align: center; color: var(--text-secondary)">
            <p>请先 <router-link to="/login">登录</router-link> 后租赁</p>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 租赁表单弹窗 -->
    <el-dialog v-model="showRentalDialog" title="申请租赁" width="500px">
      <el-form :model="rentalForm" label-width="100px">
        <el-form-item label="月租金">
          <el-input-number v-model="rentalForm.monthlyRent" :min="0" :precision="2" style="width: 100%" disabled />
          <span style="margin-left: 8px; color: #999">元/月</span>
        </el-form-item>
        <el-form-item label="起租日期">
          <el-date-picker v-model="rentalForm.startDate" type="date" placeholder="选择起租日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="到期日期">
          <el-date-picker v-model="rentalForm.endDate" type="date" placeholder="选择到期日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="押金">
          <el-input-number v-model="rentalForm.deposit" :min="0" :precision="2" style="width: 100%" />
          <span style="margin-left: 8px; color: #999">元</span>
        </el-form-item>
        <el-form-item label="联系姓名">
          <el-input v-model="rentalForm.contactName" placeholder="您的姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="rentalForm.contactPhone" placeholder="您的电话" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="rentalForm.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRentalDialog = false">取消</el-button>
        <el-button type="primary" @click="submitRental">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { houseApi } from '@/api/house'
import { orderApi } from '@/api/order'
import { rentalApi } from '@/api/rental'
import { evaluationsApi } from '@/api/evaluations'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()

const house = ref(null)
const comments = ref([])
const commentContent = ref('')
const isCollected = ref(false)
const hasBooked = ref(false)
const hasRented = ref(false)
const showRentalDialog = ref(false)
const orderForm = reactive({ orderTime: '', contactName: '', contactPhone: '', remark: '' })
const rentalForm = reactive({ monthlyRent: 0, startDate: '', endDate: '', deposit: 0, contactName: '', contactPhone: '', remark: '' })

const images = computed(() => {
  if (!house.value?.images) return house.value?.coverImage ? [house.value.coverImage] : []
  try { return JSON.parse(house.value.images) } catch { return [] }
})

const loadDetail = async () => {
  const res = await houseApi.detail(route.params.id)
  if (res.code === 200) house.value = res.data
}

const loadComments = async () => {
  const res = await evaluationsApi.list(route.params.id, { page: 1, size: 50 })
  if (res.code === 200) comments.value = res.data.records || []
}

// 检查是否已预约
const checkBookingStatus = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await orderApi.userList({ page: 1, size: 100 })
    if (res.code === 200) {
      const orders = res.data.records || []
      // 检查是否有针对该房源的有效预约（状态0待确认或1已确认）
      hasBooked.value = orders.some(o => o.houseId == route.params.id && (o.status === 0 || o.status === 1))
    }
  } catch (e) {
    console.log('检查预约状态失败', e)
  }
}

const submitComment = async () => {
  const res = await evaluationsApi.publish({ houseId: route.params.id, content: commentContent.value })
  if (res.code === 200) {
    ElMessage.success('评论成功')
    commentContent.value = ''
    loadComments()
  }
}

const handleUpvote = async (comment) => {
  await evaluationsApi.upvote(comment.id)
  comment.upvoteCount++
}

const submitOrder = async () => {
  if (!orderForm.orderTime || !orderForm.contactName || !orderForm.contactPhone) {
    ElMessage.warning('请填写完整信息')
    return
  }
  const res = await orderApi.create({ houseId: route.params.id, landlordId: house.value.landlordId, ...orderForm })
  if (res.code === 200) {
    ElMessage.success('预约成功！')
    hasBooked.value = true
    Object.assign(orderForm, { orderTime: '', contactName: '', contactPhone: '', remark: '' })
  }
}

const toggleCollect = async () => {
  if (!userStore.isLoggedIn) return ElMessage.warning('请先登录')
  if (isCollected.value) {
    await houseApi.cancelCollect(route.params.id)
    isCollected.value = false
    ElMessage.success('取消收藏')
  } else {
    await houseApi.collect(route.params.id)
    isCollected.value = true
    ElMessage.success('收藏成功')
  }
}

const formatTime = (time) => time ? new Date(time).toLocaleString() : ''

onMounted(() => { 
  loadDetail()
  loadComments()
  checkBookingStatus()
  checkRentalStatus()
})

// 监听房源数据加载，填充租赁表单的月租金
watch(() => house.value, (newHouse) => {
  if (newHouse) {
    rentalForm.monthlyRent = newHouse.price || 0
    rentalForm.deposit = newHouse.price || 0
  }
})

// 检查是否已租赁
const checkRentalStatus = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await rentalApi.userList({ page: 1, size: 100 })
    if (res.code === 200) {
      const rentals = res.data.records || []
      hasRented.value = rentals.some(r => r.houseId == route.params.id && (r.status === 0 || r.status === 1))
    }
  } catch (e) {
    console.log('检查租赁状态失败', e)
  }
}

// 提交租赁申请
const submitRental = async () => {
  if (!rentalForm.startDate || !rentalForm.endDate) {
    ElMessage.warning('请选择租赁日期')
    return
  }
  if (!rentalForm.contactName || !rentalForm.contactPhone) {
    ElMessage.warning('请填写联系信息')
    return
  }
  
  const data = {
    houseId: route.params.id,
    landlordId: house.value.landlordId,
    ...rentalForm
  }
  
  const res = await rentalApi.create(data)
  if (res.code === 200) {
    ElMessage.success('租赁申请已提交！')
    hasRented.value = true
    showRentalDialog.value = false
  } else {
    ElMessage.error(res.message || '提交失败')
  }
}
</script>

<style scoped lang="scss">
.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color);
  
  .comment-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
    
    .user { font-weight: 500; }
    .time { color: var(--text-secondary); font-size: 12px; }
  }
  
  .comment-content { line-height: 1.6; }
  .comment-actions { margin-top: 8px; }
}
</style>
