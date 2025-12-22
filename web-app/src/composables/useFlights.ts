import { ref, reactive } from 'vue';
import type { Flight, FlightCreate, FlightUpdate, FlightQuery, FlightDelete } from '@/types/flight';

const API_BASE = 'http://localhost:8081/api/flight';

export const useFlights = () => {
    const flights = ref<Flight[]>([]);
    const loading = ref(false);
    const error = ref<string | null>(null);

    const fetchFlights = async (query?: FlightQuery) => {
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

            if (!response.ok) throw new Error('Ошибка загрузки рейсов');
            flights.value = await response.json();
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Неизвестная ошибка';
            console.error('Ошибка загрузки рейсов:', err);
        } finally {
            loading.value = false;
        }
    };

    const createFlight = async (flight: FlightCreate): Promise<Flight | null> => {
        try {
            const formData = new FormData();
            Object.entries(flight).forEach(([key, value]) => {
                formData.append(key, value.toString());
            });

            const response = await fetch(`${API_BASE}/create`, {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) throw new Error('Ошибка создания рейса');
            const created = await response.json();
            flights.value.push(created);
            return created;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка создания рейса';
            return null;
        }
    };

    const updateFlight = async (flight: FlightUpdate): Promise<Flight | null> => {
        try {
            const formData = new FormData();
            Object.entries(flight).forEach(([key, value]) => {
                formData.append(key, value.toString());
            });

            const response = await fetch(`${API_BASE}/update`, {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) throw new Error('Ошибка обновления рейса');
            const updated = await response.json();
            const index = flights.value.findIndex(f => f.id === flight.id);
            if (index !== -1) flights.value[index] = updated;
            return updated;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка обновления рейса';
            return null;
        }
    };

    const deleteFlight = async (id: number) => {
        try {
            const formData = new FormData();
            formData.append('id', id.toString());

            const response = await fetch(`${API_BASE}/delete`, {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) throw new Error('Ошибка удаления рейса');
            flights.value = flights.value.filter(flight => flight.id !== id);
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка удаления рейса';
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
            a.download = 'flights.csv';
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
            await fetchFlights();
            return true;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка импорта';
            return false;
        }
    };

    const fetchAll = async () => {
        try {
            const response = await fetch(`${API_BASE}/all`);
            if (!response.ok) throw new Error('Ошибка загрузки всех рейсов');
            flights.value = await response.json();
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'Ошибка загрузки';
        }
    };

    return {
        flights,
        loading,
        error,
        fetchFlights,
        createFlight,
        updateFlight,
        deleteFlight,
        exportCSV,
        importCSV,
        fetchAll,
    };
};