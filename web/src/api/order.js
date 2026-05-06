import request from '@/utils/request'

export const orderApi = {
    create: (data) => request.post('/order', data),
    confirm: (id) => request.put(`/order/confirm/${id}`),
    reject: (id, remark) => request.put(`/order/reject/${id}`, null, { params: { remark } }),
    complete: (id) => request.put(`/order/complete/${id}`),
    cancel: (id) => request.put(`/order/cancel/${id}`),
    userList: (params) => request.get('/order/user', { params }),
    landlordList: (params) => request.get('/order/landlord', { params }),
    detail: (id) => request.get(`/order/detail/${id}`),
    evaluate: (id, rating, content) => request.post(`/order/evaluate/${id}`, null, { params: { rating, content } })
}
