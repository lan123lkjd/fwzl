<template>
  <div class="container">
    <div class="page-header"><h2>系统公告</h2></div>
    <el-card v-for="notice in list" :key="notice.id" style="margin-bottom: 16px">
      <div style="display: flex; align-items: center; gap: 12px">
        <el-tag v-if="notice.top" type="danger" size="small">置顶</el-tag>
        <el-tag :type="{ 1: 'primary', 2: 'success', 3: 'warning' }[notice.type]" size="small">
          {{ { 1: '系统公告', 2: '活动公告', 3: '政策公告' }[notice.type] }}
        </el-tag>
        <h3 style="flex: 1; font-size: 16px">{{ notice.title }}</h3>
      </div>
      <div style="margin-top: 12px; line-height: 1.6; color: var(--text-secondary)">{{ notice.content }}</div>
    </el-card>
    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="page" :total="total" layout="prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { commonApi } from '@/api/common'

const list = ref([])
const page = ref(1)
const total = ref(0)

const loadData = async () => {
  const res = await commonApi.noticeList({ page: page.value, size: 10 })
  if (res.code === 200) { list.value = res.data.records || []; total.value = res.data.total || 0 }
}

onMounted(loadData)
</script>
