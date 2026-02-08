import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('token') || '')
    const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

    const isLoggedIn = computed(() => !!token.value)
    const isAdmin = computed(() => user.value?.role === 0)
    const isLandlord = computed(() => user.value?.role === 2)
    const isUser = computed(() => user.value?.role === 1)

    async function login(username, password) {
        const res = await authApi.login({ username, password })
        if (res.code === 200) {
            token.value = res.data.token
            user.value = res.data.user
            localStorage.setItem('token', res.data.token)
            localStorage.setItem('user', JSON.stringify(res.data.user))
        }
        return res
    }

    async function register(userData) {
        return await authApi.register(userData)
    }

    function logout() {
        token.value = ''
        user.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('user')
    }

    async function fetchUserInfo() {
        if (!token.value) return
        const res = await authApi.getUserInfo()
        if (res.code === 200) {
            user.value = res.data
            localStorage.setItem('user', JSON.stringify(res.data))
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
