import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
    const token = ref(sessionStorage.getItem('token') || '')
    const user = ref(JSON.parse(sessionStorage.getItem('user') || 'null'))

    const isLoggedIn = computed(() => !!token.value)
    const isAdmin = computed(() => user.value?.role === 0)
    const isLandlord = computed(() => user.value?.role === 2)
    const isUser = computed(() => user.value?.role === 1)

    async function login(username, password) {
        const res = await authApi.login({ username, password })
        if (res.code === 200) {
            token.value = res.data.token
            user.value = res.data.user
            sessionStorage.setItem('token', res.data.token)
            sessionStorage.setItem('user', JSON.stringify(res.data.user))
        }
        return res
    }

    async function register(userData) {
        return await authApi.register(userData)
    }

    function logout() {
        token.value = ''
        user.value = null
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('user')
    }

    async function fetchUserInfo() {
        if (!token.value) return
        const res = await authApi.getUserInfo()
        if (res.code === 200) {
            user.value = res.data
            sessionStorage.setItem('user', JSON.stringify(res.data))
        }
    }

    async function updateProfile(data) {
        return await authApi.updateProfile(data)
    }

    async function updatePassword(oldPassword, newPassword) {
        return await authApi.updatePassword({ oldPassword, newPassword })
    }

    return {
        token,
        user,
        isLoggedIn,
        isAdmin,
        isLandlord,
        isUser,
        login,
        register,
        logout,
        fetchUserInfo,
        updateProfile,
        updatePassword
    }
})
