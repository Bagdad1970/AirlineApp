<template>
  <div class="booking-list">
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Загрузка бронирований...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <button @click="$emit('retry')" class="btn-retry">Повторить</button>
    </div>

    <div v-else-if="bookings.length === 0" class="empty-state">
      <p>Брони не найдены</p>
    </div>

    <div v-else class="bookings-list">
      <div v-for="booking in bookings" :key="booking.id" class="booking-item">
        <div class="booking-header">
          <div class="booking-id">
            <span class="id-label">ID:</span>
            <span class="id-value">{{ booking.id }}</span>
          </div>
          <div class="booking-actions">
            <button @click="emit('edit', booking)" class="btn-action btn-edit" title="Редактировать">
              <img src="@/../public/edit.png" alt="edit" class="edit-icon">
              <span class="action-text">Редактировать</span>
            </button>
            <button @click="emit('delete', booking.id!)" class="btn-action btn-delete" title="Отменить">
              <img src="@/../public/remove.png" alt="remove" class="remove-icon">
              <span class="action-text">Отменить</span>
            </button>
          </div>
        </div>

        <div class="booking-content">
          <div class="booking-info">
            <div class="info-section">
              <div class="info-label">Рейс</div>
              <div class="info-value">ID: {{ booking.flightId }}</div>
            </div>

            <div class="info-section">
              <div class="info-label">Пассажиры</div>
              <div class="info-value">{{ booking.passengerCount }} чел.</div>
            </div>

            <div class="info-section">
              <div class="info-label">Статус</div>
              <div class="info-value">{{ getStatusText(booking.status) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Booking, BookingStatus } from '@/types/booking';

interface Props {
  bookings: Booking[];
  loading?: boolean;
  error?: string | null;
}

interface Emits {
  (e: 'edit', booking: Booking): void;
  (e: 'delete', id: number): void;
  (e: 'retry'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const formatDateTime = (dateString?: string) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const getStatusClass = (status: BookingStatus) => {
  switch (status) {
    case 'PENDING': return 'status-pending';
    case 'CONFIRMED': return 'status-confirmed';
    case 'CANCELLED': return 'status-cancelled';
    case 'COMPLETED': return 'status-completed';
    default: return 'status-unknown';
  }
};

const getStatusText = (status: BookingStatus) => {
  switch (status) {
    case 'PENDING': return 'Ожидание';
    case 'CONFIRMED': return 'Подтверждено';
    case 'CANCELLED': return 'Отменено';
    case 'COMPLETED': return 'Завершено';
    default: return 'Неизвестно';
  }
};
</script>

<style scoped>
.booking-list {
  width: 100%;
}

/* Состояния */
.loading-state, .error-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.error-state p, .empty-state p {
  color: #374151;
  font-size: 16px;
  margin-bottom: 8px;
  font-weight: 500;
}

.empty-subtext {
  color: #6b7280 !important;
  font-size: 14px !important;
  font-weight: 400 !important;
}

.btn-retry {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  margin-top: 16px;
  transition: background-color 0.2s;
}

.btn-retry:hover {
  background: #2563eb;
}

/* Список бронирований */
.bookings-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.booking-item {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
  transition: all 0.2s ease;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

.booking-item:hover {
  border-color: #d1d5db;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-1px);
}

.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e5e7eb;
  flex-wrap: wrap;
  gap: 12px;
}

.booking-id {
  display: flex;
  align-items: center;
  gap: 8px;
}

.id-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.id-value {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
}

.booking-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-pending {
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fbbf24;
}

.status-confirmed {
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #10b981;
}

.status-cancelled {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #ef4444;
}

.status-completed {
  background: #e0e7ff;
  color: #3730a3;
  border: 1px solid #6366f1;
}

.booking-actions {
  display: flex;
  gap: 8px;
}

.btn-action {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.2s;
}

.btn-edit {
  background: white;
  border-color: #d1d5db;
  color: #374151;
}

.btn-edit:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.btn-delete {
  background: #fef2f2;
  border-color: #fecaca;
  color: #dc2626;
}

.btn-delete:hover {
  background: #fee2e2;
  border-color: #fca5a5;
}

.action-text {
  display: none;
}

/* Контент бронирования */
.booking-content {
  padding: 20px;
}

.booking-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

/* Анимации */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Адаптивность */
@media (max-width: 768px) {
  .booking-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .booking-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .booking-info {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .action-text {
    display: inline;
  }
}

@media (max-width: 480px) {
  .booking-content {
    padding: 16px;
  }

  .booking-header {
    padding: 12px 16px;
  }

  .booking-actions {
    flex-direction: column;
  }

  .btn-action {
    width: 100%;
    justify-content: center;
  }
}

.edit-icon, .remove-icon {
  width: 20px;
  height: 20px;
}
</style>