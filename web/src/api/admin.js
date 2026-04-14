import request from '@/utils/request'

export const adminApi = {
    // 用户管理
    userList: (params) => request.get('/admin/user/list', { params }),
    updateUserStatus: (id, status) => request.put(`/admin/user/status/${id}`, null, { params: { status } }),
    deleteUser: (id) => request.delete(`/admin/user/${id}`),
    updateUser: (id, data) => request.put(`/admin/user/${id}`, data),

    // 房源管理
    houseList: (status) => request.get('/admin/house/list', { params: { status } }),
    auditHouse: (id, status, remark) => request.put(`/admin/house/audit/${id}`, null, { params: { status, remark } }),
    deleteHouse: (id) => request.delete(`/admin/house/${id}`),

    // 房东管理
    landlordList: (params) => request.get('/admin/landlord/list', { params }),
    auditLandlord: (id, status, remark) => request.put(`/admin/landlord/audit/${id}`, null, { params: { status, remark } }),

    // 预约管理
    orderList: (params) => request.get('/admin/order/list', { params }),

    // 资讯管理
    newsList: () => request.get('/admin/news/list'),
    publishNews: (data) => request.post('/admin/news', data),
    updateNews: (id, data) => request.put(`/admin/news/${id}`, data),
    deleteNews: (id) => request.delete(`/admin/news/${id}`),

    // 公告管理
    noticeList: () => request.get('/admin/notice/list'),
    publishNotice: (data) => request.post('/admin/notice', data),
    updateNotice: (id, data) => request.put(`/admin/notice/${id}`, data),
    deleteNotice: (id) => request.delete(`/admin/notice/${id}`),

    // 地区管理
    areaList: (parentId) => request.get('/admin/area/list', { params: { parentId } }),
    addArea: (data) => request.post('/admin/area', data),

    // 小区管理
    communityList: (areaId) => request.get('/admin/community/list', { params: { areaId } }),
    addCommunity: (data) => request.post('/admin/community', data),

    // 统计数据
    todayStats: () => request.get('/admin/stats/today'),
    totalStats: () => request.get('/admin/stats/total'),
    trendStats: (days = 7) => request.get('/admin/stats/trend', { params: { days } }),

    // 评论管理
    evaluationList: (params) => request.get('/admin/evaluation/list', { params }),
    deleteEvaluation: (id) => request.delete(`/admin/evaluation/${id}`)
}
