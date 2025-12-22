import { createRouter, createWebHistory } from 'vue-router';
import FlightsPage from '@/views/FlightsPage.vue';
import BookingsPage from "@/views/BookingsPage.vue";
import StatisticsPage from "@/views/StatisticsPage.vue";

const routes = [
    {
        path: '/flights',
        name: 'Flights',
        component: FlightsPage,
    },
    {
        path: '/bookings',
        name: "Bookings",
        component: BookingsPage
    },
    {
        path: '/statistics',
        name: "Statistics",
        component: StatisticsPage
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;