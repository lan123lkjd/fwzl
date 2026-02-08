import request from '@/utils/request'

export const rentalApi = {
    // 创建租赁申请
    create: (data) => request.post('/rental', data),

    // 用户租赁列表
    userList: (params) => request.get('/rental/user', { params }),

    // 房东租赁列表
    landlordList: (params) => request.get('/rental/landlord', { params }),

    // 租赁详情
    detail: (id) => request.get(`/rental/detail/${id}`),

    // 确认租赁
    confirm: (id) => request.put(`/rental/confirm/${id}`),

    // 拒绝租赁
    reject: (id, remark) => request.put(`/rental/reject/${id}`, null, { params: { remark } }),

    // 完成租赁
    complete: (id) => request.put(`/rental/complete/${id}`),

    // 取消租赁
    cancel: (id) => request.put(`/rental/cancel/${id}`)
}
