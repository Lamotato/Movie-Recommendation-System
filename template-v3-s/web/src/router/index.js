import {createRouter, createWebHistory} from 'vue-router'
import AdminLayout from "@/views/layout/AdminLayout.vue";
import FrontLayout from "@/views/layout/FrontLayout.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: getRoutes()
})

function getRoutes() {
    let defaultRoutes = [
        {
            path: '/',
            name: 'front',
            component: FrontLayout,
            redirect: '/home',
            children: [
                {
                    path: 'home',
                    name: 'front-home',
                    component: () => import('../views/front/Home.vue')
                },
                {
                    path: 'movies',
                    name: 'front-movie-list',
                    component: () => import('../views/front/MovieList.vue')
                },
                {
                    path: 'movies/:id',
                    name: 'front-movie-detail',
                    component: () => import('../views/front/MovieDetail.vue')
                },
                {
                    path: 'booking/:id',
                    name: 'front-seat-selection',
                    component: () => import('../views/front/SeatSelection.vue')
                },
                {
                    path: 'orders',
                    name: 'front-order-list',
                    component: () => import('../views/front/OrderList.vue')
                },
                {
                    path: 'orders/:orderNo',
                    name: 'front-order-detail',
                    component: () => import('../views/front/OrderDetail.vue')
                },
                {
                    path: 'editCurrentUser',
                    name: 'front-editCurrentUser',
                    component: () => import('../views/EditCurrentUser.vue')
                },
                {
                    path: 'editPassword',
                    name: 'front-editPassword',
                    component: () => import('../views/EditPassword.vue')
                }
            ]
        },
        {
            path: '/admin',
            name: 'admin',
            component: AdminLayout,
            redirect: "/admin/home",
            children: [{
                path: "home",
                name: "admin-home",
                component: () =>
                    import ('../views/admin/Home.vue')
            },
                {
                    path: 'editCurrentUser',
                    name: 'admin-editCurrentUser',
                    component: () =>
                        import ('../views/EditCurrentUser.vue')
                },
                {
                    path: 'editPassword',
                    name: 'admin-editPassword',
                    component: () =>
                        import ('../views/EditPassword.vue')
                },
                {
                    path: 'admin',
                    name: 'Admin',
                    component: () =>
                        import ('../views/admin/AdminManage.vue')
                },
                {
                    path: 'movie',
                    name: 'admin-movie',
                    component: () =>
                        import ('../views/admin/MovieManage.vue')
                },
                {
                    path: 'order',
                    name: 'admin-order',
                    component: () =>
                        import ('../views/admin/OrderManage.vue')
                },
                {
                    path: 'user',
                    name: 'admin-user',
                    component: () =>
                        import ('../views/admin/UserManage.vue')
                },
                {
                    path: 'cinema',
                    name: 'admin-cinema',
                    component: () =>
                        import ('../views/admin/CinemaManage.vue')
                },
                {
                    path: 'room',
                    name: 'admin-room',
                    component: () =>
                        import ('../views/admin/RoomManage.vue')
                },
                {
                    path: 'seat/manage',
                    name: 'admin-seat-manage',
                    component: () =>
                        import ('../views/admin/SeatManage.vue')
                },
                {
                    path: 'screening',
                    name: 'admin-screening',
                    component: () =>
                        import ('../views/admin/ScreeningManage.vue')
                }
            ]
        },
        {
            path: "/login",
            name: "login",
            component: () =>
                import ('../views/Login.vue')
        }, {
            path: "/register",
            name: "register",
            component: () =>
                import ('../views/Register.vue')
        }, {
            path: "/retrievePassword",
            name: "front-retrievePassword",
            component: () =>
                import ('../views/RetrievePassword.vue')
        }];
    defaultRoutes.push({
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        meta: {
            name: ''
        },
        component: () => import ('../views/404.vue')
    })
    console.log('getDynamicRoutes', defaultRoutes)
    return defaultRoutes;
}

router.beforeEach((to, from, next) => {
    next();
});
export default router
