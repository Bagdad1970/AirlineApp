<template>
  <div class="statistics-page">
    <header class="page-header">
      <h1>Статистика</h1>
    </header>

    <div class="content-wrapper">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка статистики...</p>
      </div>

      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button @click="fetchAllStatistics" class="btn-retry">
          Повторить
        </button>
      </div>

      <!-- Основная статистика -->
      <div v-else class="statistics-content">
        <!-- Статистика рейсов -->
        <div class="statistics-section">
          <h2 class="section-title">
            Статистика рейсов
          </h2>

          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-content">
                <div class="stat-value">{{ flightStats.totalFlights || 0 }}</div>
                <div class="stat-label">Всего рейсов</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-content">
                <div class="stat-value">{{ flightStats.theMostDepartureCity || '—' }}</div>
                <div class="stat-label">Самый популярный город отправления</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-content">
                <div class="stat-value">{{ flightStats.theMostVisitingCity || '—' }}</div>
                <div class="stat-label">Самый популярный город назначения</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Статистика бронирований -->
        <div class="statistics-section">
          <h2 class="section-title">
            Статистика бронирований
          </h2>

          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-content">
                <div class="stat-value">{{ bookingStats.bookingCount || 0 }}</div>
                <div class="stat-label">Всего бронирований</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-content">
                <div class="stat-value">{{ bookingStats.totalPassengerCount || 0 }}</div>
                <div class="stat-label">Общее количество пассажиров</div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-content">
                <div class="stat-value">{{ formatAverage(bookingStats.averagePassengerCount) }}</div>
                <div class="stat-label">Среднее количество пассажиров на бронирование</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

// Типы статистики
interface FlightStats {
  theMostDepartureCity: string;
  theMostVisitingCity: string;
  totalFlights: number;
  totalRevenue: number;
}

interface BookingStats {
  averagePassengerCount: number;
  totalPassengerCount: number;
  bookingCount: number;
}

// Состояние
const flightStats = ref<FlightStats>({
  theMostDepartureCity: '',
  theMostVisitingCity: '',
  totalFlights: 0,
  totalRevenue: 0
});

const bookingStats = ref<BookingStats>({
  averagePassengerCount: 0,
  totalPassengerCount: 0,
  bookingCount: 0
});

const loading = ref(false);
const error = ref<string | null>(null);
const lastUpdated = ref<Date | null>(null);

// API эндпоинты
const FLIGHT_API = 'http://localhost:8081/api/flight/statistics';
const BOOKING_API = 'http://localhost:8082/api/booking/statistics';

// Форматирование валюты
const formatCurrency = (value: number) => {
  if (!value) return '0 ₽';
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(value);
};

// Форматирование среднего значения
const formatAverage = (value: number) => {
  if (!value) return '0';
  return value.toFixed(1);
};

// Форматирование времени
const formatTime = (date: Date) => {
  return date.toLocaleString('ru-RU', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

// Расчётные показатели
const calculateLoadPercentage = () => {
  if (!flightStats.value.totalFlights || !bookingStats.value.bookingCount) return 0;
  const percentage = (bookingStats.value.bookingCount / flightStats.value.totalFlights) * 100;
  return Math.min(Math.round(percentage), 100);
};

const calculatePassengersPerFlight = () => {
  if (!flightStats.value.totalFlights || !bookingStats.value.totalPassengerCount) return '0';
  const average = bookingStats.value.totalPassengerCount / flightStats.value.totalFlights;
  return average.toFixed(1);
};

const calculateRevenuePerPassenger = () => {
  if (!bookingStats.value.totalPassengerCount || !flightStats.value.totalRevenue) return '0 ₽';
  const perPassenger = flightStats.value.totalRevenue / bookingStats.value.totalPassengerCount;
  return formatCurrency(perPassenger);
};

// Загрузка статистики рейсов
const fetchFlightStatistics = async () => {
  try {
    const response = await fetch(FLIGHT_API, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });

    if (!response.ok) throw new Error('Ошибка загрузки статистики рейсов');
    return await response.json();
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Ошибка загрузки статистики рейсов';
    return null;
  }
};

// Загрузка статистики бронирований
const fetchBookingStatistics = async () => {
  try {
    const response = await fetch(BOOKING_API, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });

    if (!response.ok) throw new Error('Ошибка загрузки статистики бронирований');
    return await response.json();
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Ошибка загрузки статистики бронирований';
    return null;
  }
};

// Загрузка всей статистики
const fetchAllStatistics = async () => {
  loading.value = true;
  error.value = null;

  try {
    const [flightData, bookingData] = await Promise.all([
      fetchFlightStatistics(),
      fetchBookingStatistics()
    ]);

    if (flightData) flightStats.value = flightData;
    if (bookingData) bookingStats.value = bookingData;

    lastUpdated.value = new Date();
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Неизвестная ошибка';
  } finally {
    loading.value = false;
  }
};

// Загрузка при монтировании
onMounted(() => {
  fetchAllStatistics();
});
</script>

<style scoped>
.statistics-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  font-family: 'Segoe UI', system-ui, sans-serif;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e8e8e8;
}

.page-header h1 {
  margin: 0;
  color: #2c3e50;
  font-size: 32px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.page-header p {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 16px;
  font-weight: 400;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* Кнопка обновления */
.refresh-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.btn-refresh {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-refresh:hover:not(:disabled) {
  background: #2563eb;
  transform: translateY(-1px);
}

.btn-refresh:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.refresh-icon {
  font-size: 18px;
  transition: transform 0.3s;
}

.refresh-icon.spinning {
  animation: spin 1s linear infinite;
}

.last-updated {
  font-size: 14px;
  color: #64748b;
}

/* Состояния загрузки/ошибки */
.loading-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
  text-align: center;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

.loading-state p {
  color: #6b7280;
  font-size: 16px;
}

.error-state p {
  color: #dc2626;
  font-size: 16px;
  margin-bottom: 16px;
}

.btn-retry {
  padding: 8px 20px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-retry:hover {
  background: #2563eb;
}

/* Секции статистики */
.statistics-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 24px 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.section-icon {
  font-size: 24px;
}

/* Карточки статистики */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s;
}

.stat-card:hover {
  border-color: #cbd5e1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  line-height: 1.4;
}

/* Сводная информация */
.summary-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.summary-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.summary-label {
  font-size: 15px;
  color: #475569;
  font-weight: 500;
}

.summary-value {
  font-size: 16px;
  color: #1e293b;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-bar {
  width: 120px;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  transition: width 0.5s ease;
}

.summary-hint {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 400;
}

/* Анимации */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Адаптивность */
@media (max-width: 768px) {
  .statistics-page {
    padding: 16px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .page-header p {
    font-size: 14px;
  }

  .refresh-section {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .btn-refresh {
    width: 100%;
    justify-content: center;
  }

  .last-updated {
    text-align: center;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .stat-card {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 24px;
  }

  .summary-item {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .summary-value {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .statistics-section,
  .summary-section {
    padding: 16px;
  }

  .section-title {
    font-size: 18px;
  }

  .stat-value {
    font-size: 20px;
  }

  .progress-bar {
    width: 100px;
  }
}
</style>