<template>
  <div>
    <div class="page-header"><h2>我的租赁</h2></div>
    
    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待确认" name="0" />
      <el-tab-pane label="待支付" name="1" />
      <el-tab-pane label="租赁中" name="2" />
      <el-tab-pane label="已完成" name="3" />
      <el-tab-pane label="已取消/拒绝" name="cancelled" />
    </el-tabs>

    <el-table :data="list" border stripe style="margin-top: 16px">
      <el-table-column prop="rentalNo" label="租赁单号" width="180" />
      <el-table-column prop="houseTitle" label="房源" min-width="150" show-overflow-tooltip />
      <el-table-column label="租赁期限" width="200">
        <template #default="{ row }">
          {{ row.startDate }} 至 {{ row.endDate }}
        </template>
      </el-table-column>
      <el-table-column prop="monthlyRent" label="月租金" width="100">
        <template #default="{ row }">
          ¥{{ row.monthlyRent }}
        </template>
      </el-table-column>
      <el-table-column prop="deposit" label="押金" width="100">
        <template #default="{ row }">
          ¥{{ row.deposit || 0 }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="160" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <!-- 待确认/待支付状态：显示取消按钮 -->
          <el-button 
            v-if="row.status === 0 || row.status === 1" 
            size="small" 
            type="danger" 
            @click="handleCancel(row)"
          >
            取消申请
          </el-button>
          
          <!-- 租赁中状态：显示禁用按钮+提示 -->
          <el-tooltip 
            v-if="row.status === 2" 
            content="租赁中的订单需联系房东办理退租" 
            placement="top"
          >
            <el-button size="small" type="danger" disabled>取消</el-button>
          </el-tooltip>
          
          <!-- 待支付状态：显示支付按钮 -->
          <el-button 
            v-if="row.status === 1" 
            size="small" 
            type="warning" 
            @click="openPayDialog(row)"
          >
            去支付
          </el-button>
          
          <!-- 租赁中状态：显示完成按钮 -->
          <el-button 
            v-if="row.status === 2" 
            size="small" 
            type="success" 
            @click="handleComplete(row.id)"
          >
            完成租赁
          </el-button>
          
          <!-- 已完成状态 -->
          <el-tag v-if="row.status === 3" size="small" type="success">
            ✓ 已完成
          </el-tag>
          
          <!-- 已取消/已拒绝状态 -->
          <el-tag v-if="row.status === 4 || row.status === 5" size="small" type="info">
            已结束
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <el-dialog v-model="showPayDialog" title="订单支付" width="450px" :close-on-click-modal="false">
      <div v-if="payRental" class="pay-dialog-content">
        <div class="pay-info-item">
          <span class="pay-label">租赁单号</span>
          <span class="pay-value">{{ payRental.rentalNo }}</span>
        </div>
        <div class="pay-info-item">
          <span class="pay-label">房源</span>
          <span class="pay-value">{{ payRental.houseTitle }}</span>
        </div>
        <div class="pay-info-item">
          <span class="pay-label">租赁期限</span>
          <span class="pay-value">{{ payRental.startDate }} 至 {{ payRental.endDate }}</span>
        </div>
        <el-divider />
        <div class="pay-info-item">
          <span class="pay-label">月租金</span>
          <span class="pay-value">¥{{ payRental.monthlyRent }}/月</span>
        </div>
        <div class="pay-info-item">
          <span class="pay-label">押金</span>
          <span class="pay-value">¥{{ payRental.deposit || 0 }}</span>
        </div>
        <div class="pay-info-item total">
          <span class="pay-label">应付总额</span>
          <span class="pay-value pay-total">¥{{ payRental.totalAmount || 0 }}</span>
        </div>
        <el-divider />
        <div class="pay-method">
          <div class="pay-method-title">选择支付方式</div>
          <el-radio-group v-model="payMethod">
            <el-radio label="alipay" style="margin-bottom: 8px">
              <span style="color: #1677ff">支付宝</span>
            </el-radio>
            <el-radio label="wechat" style="margin-bottom: 8px">
              <span style="color: #07c160">微信支付</span>
            </el-radio>
            <el-radio label="bank">
              <span style="color: #333">银行卡</span>
            </el-radio>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button type="primary" :loading="payLoading" @click="handlePay">
          确认支付 ¥{{ payRental?.totalAmount || 0 }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { rentalApi } from '@/api/rental'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const activeTab = ref('all')

const showPayDialog = ref(false)
const payRental = ref(null)
const payMethod = ref('alipay')
const payLoading = ref(false)

const loadData = async () => {
  const params = { page: page.value, size: size.value }
  if (activeTab.value !== 'all') {
    if (activeTab.value === 'cancelled') {
      params.statusList = '4,5'
    } else {
      params.status = parseInt(activeTab.value)
    }
  }
  const res = await rentalApi.userList(params)
  if (res.code === 200) {
    list.value = res.data.records || []
    total.value = res.data.total || 0
  }
}

const getStatusText = (s) => ({ 0: '待确认', 1: '待支付', 2: '租赁中', 3: '已完成', 4: '已取消', 5: '已拒绝' }[s] || '-')
const getStatusType = (s) => ({ 0: 'warning', 1: 'danger', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }[s] || '')

const handleCancel = async (row) => {
  const statusText = getStatusText(row.status)
  let message = ''
  let title = '确认取消'
  
  // 根据不同状态给出不同的确认提示
  if (row.status === 0) {
    message = `当前状态：${statusText}\n\n取消后房东将收到通知，房源将重新开放预订。\n确定要取消吗？`
    title = '取消待确认申请'
  } else if (row.status === 1) {
    message = `当前状态：${statusText}\n\n您还未支付租金，取消后将释放房源。\n确定要取消吗？`
    title = '取消待支付订单'
  }
  
  try {
    await ElMessageBox.confirm(message, title, { 
      type: 'warning',
      confirmButtonText: '确定取消',
      cancelButtonText: '我再想想',
      distinguishCancelAndClose: true
    })
    
    const res = await rentalApi.cancel(row.id)
    if (res.code === 200) {
      ElMessage.success('已取消')
      loadData()
    } else {
      // 后端返回错误时，根据错误信息显示不同的提示
      handleCancelError(res.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      // 用户点击了取消按钮或关闭对话框，不做处理
      console.log('用户取消了操作')
    }
  }
}

// 处理取消操作的错误信息
const handleCancelError = (message) => {
  if (message && message.includes('租赁中')) {
    ElMessage.warning({
      message: '租赁中的订单需要联系房东办理退租手续',
      duration: 3000
    })
  } else if (message && message.includes('已完成')) {
    ElMessage.info('该租赁已完成，无法取消')
  } else if (message && message.includes('已取消')) {
    ElMessage.info('该订单已经取消过了')
  } else {
    ElMessage.error(message || '操作失败')
  }
}

const openPayDialog = (row) => {
  payRental.value = row
  payMethod.value = 'alipay'
  showPayDialog.value = true
}

const handlePay = async () => {
  payLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1500))
    const res = await rentalApi.pay(payRental.value.id)
    if (res.code === 200) {
      ElMessage.success('支付成功！')
      showPayDialog.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '支付失败')
    }
  } finally {
    payLoading.value = false
  }
}

const handleComplete = async (id) => {
  await ElMessageBox.confirm('确定要结束此租赁吗？', '提示', { type: 'warning' })
  const res = await rentalApi.complete(id)
  if (res.code === 200) {
    ElMessage.success('租赁已完成')
    loadData()
  } else {
    ElMessage.error(res.message || '操作失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-header {
  margin-bottom: 16px;
}
.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
.pay-dialog-content {
  padding: 0 10px;
}
.pay-info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}
.pay-label {
  color: #999;
  font-size: 14px;
}
.pay-value {
  font-size: 14px;
  color: #333;
}
.pay-info-item.total {
  padding: 12px 0;
}
.pay-total {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}
.pay-method {
  margin-top: 8px;
}
.pay-method-title {
  margin-bottom: 12px;
  font-size: 14px;
  color: #666;
}
</style>
