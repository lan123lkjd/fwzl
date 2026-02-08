import request from '@/utils/request'

export const landlordApi = {
    apply: (data) => request.post('/landlord/apply', data),
    getInfo: () => request.get('/landlord/info'),
    update: (data) => request.put('/landlord', data),
    detail: (id) => request.get(`/landlord/${id}`)
}
