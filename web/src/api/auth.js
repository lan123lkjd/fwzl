import request from '@/utils/request'

export const authApi = {
    login: (data) => request.post('/auth/login', data),
    register: (data) => request.post('/auth/register', data),
    getUserInfo: () => request.get('/auth/info'),
    updateProfile: (data) => request.put('/auth/profile', data),
    updatePassword: (data) => request.put('/auth/password', data)
}
