import request from '@/utils/request'

export const evaluationsApi = {
    list: (houseId, params) => request.get(`/evaluations/list/${houseId}`, { params }),
    getAll: (houseId) => request.get(`/evaluations/all/${houseId}`),
    publish: (data) => request.post('/evaluations', data),
    upvote: (id) => request.post(`/evaluations/upvote/${id}`),
    cancelUpvote: (id) => request.delete(`/evaluations/upvote/${id}`),
    checkUpvote: (id) => request.get(`/evaluations/upvote/check/${id}`),
    delete: (id) => request.delete(`/evaluations/${id}`)
}
