import request from '@/utils/request'

export const houseApi = {
    list: (params) => request.get('/house/list', { params }),
    detail: (id) => request.get(`/house/detail/${id}`),
    recommend: (limit = 6) => request.get('/house/recommend', { params: { limit } }),
    hot: (limit = 6) => request.get('/house/hot', { params: { limit } }),
    publish: (data) => request.post('/house/publish', data),
    update: (id, data) => request.put(`/house/${id}`, data),
    delete: (id) => request.delete(`/house/${id}`),
    updateStatus: (id, status) => request.put(`/house/status/${id}`, null, { params: { status } }),
    landlordList: (params) => request.get('/house/landlord', { params }),
    collect: (id) => request.post(`/house/collect/${id}`),
    cancelCollect: (id) => request.delete(`/house/collect/${id}`)
}
