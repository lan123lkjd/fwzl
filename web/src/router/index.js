import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import component from 'element-plus/es/components/tree-select/src/tree-select-option.mjs'

const routes = [
    {
        path: '/',
        component: () => import('@/layouts/MainLayout.vue'),
        children: [
            { path: '', name: 'Home', component: () => import('@/views/Home.vue') },
            { path: 'house', name: 'HouseList', component: () => import('@/views/house/HouseList.vue') },
            { path: 'house/:id', name: 'HouseDetail', component: () => import('@/views/house/HouseDetail.vue') },
            { path: 'news', name: 'NewsList', component: () => import('@/views/news/NewsList.vue') },
            { path: 'news/:id', name: 'NewsDetail', component: () => import('@/views/news/NewsDetail.vue') },
            { path: 'notice', name: 'NoticeList', component: () => import('@/views/notice/NoticeList.vue') },
        ]
    },
    {
        path: '/user',
        component: () => import('@/layouts/UserLayout.vue'),
        meta: { requiresAuth: true },
        children: [
            { path: '', name: 'UserCenter', component: () => import('@/views/user/UserCenter.vue') },
            { path: 'orders', name: 'UserOrders', component: () => import('@/views/user/UserOrders.vue') },
            { path: 'rentals', name: 'UserRentals', component: () => import('@/views/user/UserRentals.vue') },
            { path: 'collect', name: 'UserCollect', component: () => import('@/views/user/UserCollect.vue') },
            { path: 'history', name: 'UserHistory', component: () => import('@/views/user/UserHistory.vue') },
        ]
    },
    {
        path: '/landlord',
        component: () => import('@/layouts/LandlordLayout.vue'),
        meta: { requiresAuth: true, role: 2 },
        children: [
            { path: '', name: 'LandlordDashboard', component: () => import('@/views/landlord/Dashboard.vue') },
            { path: 'house', name: 'LandlordHouse', component: () => import('@/views/landlord/HouseManage.vue') },
            { path: 'house/add', name: 'AddHouse', component: () => import('@/views/landlord/HouseForm.vue') },
            { path: 'house/edit/:id', name: 'EditHouse', component: () => import('@/views/landlord/HouseForm.vue') },
            { path: 'orders', name: 'LandlordOrders', component: () => import('@/views/landlord/OrderManage.vue') },
            { path: 'rentals', name: 'LandlordRentals', component: () => import('@/views/landlord/RentalManage.vue') },
        ]
    },
    {
        path: '/admin',
        component: () => import('@/layouts/AdminLayout.vue'),
        meta: { requiresAuth: true, role: 0 },
        children: [
            { path: '', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue') },
            { path: 'user', name: 'AdminUser', component: () => import('@/views/admin/UserManage.vue') },
            { path: 'house', name: 'AdminHouse', component: () => import('@/views/admin/HouseManage.vue') },
            { path: 'landlord', name: 'AdminLandlord', component: () => import('@/views/admin/LandlordManage.vue') },
            { path: 'order', name: 'AdminOrder', component: () => import('@/views/admin/OrderManage.vue') },
            { path: 'news', name: 'AdminNews', component: () => import('@/views/admin/NewsManage.vue') },
            { path: 'notice', name: 'AdminNotice', component: () => import('@/views/admin/NoticeManage.vue') },
            { path: 'comment', name: 'AdminComment', component: () => import("@/views/admin/CommentManage.vue") },
        ]
    },
    { path: '/login', name: 'Login', component: () => import('@/views/auth/Login.vue') },
    { path: '/register', name: 'Register', component: () => import('@/views/auth/Register.vue') },
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/views/NotFound.vue') }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    if (to.meta.requiresAuth && !userStore.isLoggedIn) {
        next({ name: 'Login', query: { redirect: to.fullPath } })
    } else if (to.meta.role !== undefined && userStore.user?.role !== to.meta.role) {
        next({ name: 'Home' })
    } else {
        next()
    }
})

export default router
