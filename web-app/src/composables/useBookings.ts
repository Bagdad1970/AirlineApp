import { ref } from 'vue';
import type { Booking, BookingCreate, BookingUpdate, BookingQuery, BookingStatistics } from '@/types/booking';

const API_BASE = 'http://localhost:8082/api/booking';

export const useBookings = () => {
    const bookings = ref<Booking[]>([]);
    const statistics = ref<BookingStatistics | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);

    const fetchBookings = async (query?: BookingQuery) => {
        loading.value = true;
        error.value = null;
        try {
            const formData = new FormData();
            if (query) {
                Object.entries(query).forEach(([key, value]) => {
                    if (value) formData.append(key, value.toString());
                });
            }

            const response = await fetch(`${API_BASE}/query`, {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) throw new Error('Ошибка загрузки броней');
            bookings.value = await response.json();
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Неизвестная ошибка';
            console.error('Ошибка загрузки бронирований:', err);
        } finally {
            loading.value = false;
        }
    };

    const createBooking = async (booking: BookingCreate): Promise<Booking | null> => {
        try {
            const formData = new FormData();
            formData.append('flightId', booking.flightId.toString());
            formData.append('passengerCount', booking.passengerCount.toString());

            const response = await fetch(`${API_BASE}/create`, {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) throw new Error('Ошибка создания бронирования');
            const created = await response.json();
            bookings.value.push(created);
            await fetchStatistics();
            return created;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка создания бронирования';
            return null;
        }
    };

    const updateBooking = async (booking: BookingUpdate): Promise<Booking | null> => {
        try {
            const formData = new FormData();
            formData.append('id', booking.id.toString());
            formData.append('passengerCount', booking.passengerCount.toString());

            const response = await fetch(`${API_BASE}/update`, {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) throw new Error('Ошибка обновления бронирования');
            const updated = await response.json();
            const index = bookings.value.findIndex(b => b.id === booking.id);
            if (index !== -1) bookings.value[index] = updated;
            await fetchStatistics();
            return updated;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка обновления бронирования';
            return null;
        }
    };

    const deleteBooking = async (id: number) => {
        try {
            const formData = new FormData();
            formData.append('id', id.toString());

            const response = await fetch(`${API_BASE}/delete`, {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) throw new Error('Ошибка отмены бронирования');
            bookings.value = bookings.value.filter(booking => booking.id !== id);
            await fetchStatistics();
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка отмены бронирования';
        }
    };

    const exportCSV = async () => {
        try {
            const response = await fetch(`${API_BASE}/export-csv`);
            if (!response.ok) throw new Error('Ошибка экспорта');

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'bookings.csv';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка экспорта';
        }
    };

    const importCSV = async (file: File): Promise<boolean> => {
        try {
            const formData = new FormData();
            formData.append('file', file);

            const response = await fetch(`${API_BASE}/import-csv`, {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) throw new Error('Ошибка импорта');
            await fetchBookings();
            await fetchStatistics();
            return true;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка импорта';
            return false;
        }
    };

    const fetchAll = async () => {
        await fetchBookings();
        await fetchStatistics();
    };

    const fetchStatistics = async () => {
        try {
            const response = await fetch(`${API_BASE}/statistics`, {
                method: 'POST',
            });

            if (!response.ok) throw new Error('Ошибка загрузки статистики');
            statistics.value = await response.json();
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка загрузки статистики';
            console.error('Ошибка загрузки статистики:', err);
        }
    };

    return {
        bookings,
        statistics,
        loading,
        error,
        fetchBookings,
        createBooking,
        updateBooking,
        deleteBooking,
        exportCSV,
        importCSV,
        fetchAll,
        fetchStatistics,
    };
};