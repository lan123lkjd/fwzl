import request from '@/utils/request'

export const commonApi = {
    areaList: (parentId = 0) => request.get('/area/list', { params: { parentId } }),
    areaTree: () => request.get('/area/tree'),
    communityList: (areaId) => request.get('/community/list', { params: { areaId } }),
    communityDetail: (id) => request.get(`/community/${id}`),
    noticeList: (params) => request.get('/notice/list', { params }),
    topNotice: () => request.get('/notice/top'),
    noticeDetail: (id) => request.get(`/notice/${id}`),
    upload: (file, type = 'common') => {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('type', type)
        return request.post('/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    },
    uploadImages: (files, type = 'house') => {
        const formData = new FormData()
        files.forEach(file => formData.append('files', file))
        formData.append('type', type)
        return request.post('/upload/images', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    },
    uploadCover: (file) => {
        const formData = new FormData()
        formData.append('file', file)
        return request.post('/upload/cover', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    },
    uploadHouseImages: (files) => {
        const formData = new FormData()
        files.forEach(file => formData.append('files', file))
        return request.post('/upload/house-images', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    },
    uploadIdCard: (file) => {
        const formData = new FormData()
        formData.append('file', file)
        return request.post('/upload/idcard', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    },
    uploadAvatar: (file) => {
        const formData = new FormData()
        formData.append('file', file)
        return request.post('/upload/avatar', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    },
    uploadNews: (file) => {
        const formData = new FormData()
        formData.append('file', file)
        return request.post('/upload/news', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    }
}

export const newsApi = {
    list: (params) => request.get('/news/list', { params }),
    detail: (id) => request.get(`/news/detail/${id}`),
    hot: (limit = 5) => request.get('/news/hot', { params: { limit } }),
    recommend: (limit = 5) => request.get('/news/recommend', { params: { limit } })
}
