<template>
  <div class="flight-list">
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Загрузка рейсов...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <button @click="$emit('retry')" class="btn-retry">Повторить</button>
    </div>

    <div v-else-if="flights.length === 0" class="empty-state">
      <p>Рейсы не найдены</p>
    </div>

    <div v-else class="flights-list">
      <div v-for="flight in flights" :key="flight.id" class="flight-item">
        <div class="flight-header">
          <div class="flight-number">
            <span class="number-text">Рейс {{ flight.number }}</span>
          </div>
          <div class="flight-actions">
            <button @click="emit('edit', flight)" class="btn-action btn-edit" title="Редактировать">
              <img src="@/../public/edit.png" alt="edit" class="edit-icon">
              <span class="action-text">Редактировать</span>
            </button>
            <button @click="emit('delete', flight.id!)" class="btn-action btn-delete" title="Удалить">
              <img src="@/../public/remove.png" alt="remove" class="remove-icon">
              <span class="action-text">Удалить</span>
            </button>
          </div>
        </div>

        <div class="flight-content">
          <div class="route-section">
            <div class="city-block">
              <div class="city-label">Отправление</div>
              <div class="city-name">{{ flight.fromCity }}</div>
              <div class="city-time">{{ formatDateTime(flight.departure) }}</div>
            </div>

            <div class="route-line">
              <div class="duration">{{ calculateDuration(flight.departure, flight.arrival) }}</div>
            </div>

            <div class="city-block">
              <div class="city-label">Прибытие</div>
              <div class="city-name">{{ flight.toCity }}</div>
              <div class="city-time">{{ formatDateTime(flight.arrival) }}</div>
            </div>
          </div>

          <div class="flight-details">
            <div class="detail-item">
              <div class="detail-label">Свободных мест</div>
              <div class="detail-value">{{ flight.passengerCount }}</div>
            </div>
            <div class="detail-item">
              <div class="detail-label">Цена билета</div>
              <div class="detail-value price">{{ formatPrice(flight.ticketPrice) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Flight } from '@/types/flight';
import { computed } from 'vue';

interface Props {
  flights: Flight[];
  loading?: boolean;
  error?: string | null;
}

interface Emits {
  (e: 'edit', flight: Flight): void;
  (e: 'delete', id: number): void;
  (e: 'retry'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const formatDateTime = (dateString: string) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const formatShortDate = (dateString?: string) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleDateString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  });
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(price);
};

const calculateDuration = (departure: string, arrival: string) => {
  if (!departure || !arrival) return '-';
  const dep = new Date(departure);
  const arr = new Date(arrival);
  const diffMs = arr.getTime() - dep.getTime();
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
  const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

  if (diffHours > 0) {
    return `${diffHours}ч ${diffMinutes}м`;
  }
  return `${diffMinutes}м`;
};

const getFlightStatus = (flight: Flight) => {
  const now = new Date();
  const departure = new Date(flight.departure);
  const arrival = new Date(flight.arrival);

  if (now < departure) return 'status-scheduled';
  if (now >= departure && now <= arrival) return 'status-in-progress';
  return 'status-completed';
};

const getFlightStatusText = (flight: Flight) => {
  const status = getFlightStatus(flight);
  switch (status) {
    case 'status-scheduled': return 'Запланирован';
    case 'status-in-progress': return 'В пути';
    case 'status-completed': return 'Завершен';
    default: return 'Неизвестно';
  }
};
</script>

<style scoped>
.flight-list {
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

.error-icon, .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-icon {
  color: #ef4444;
}

.empty-icon {
  color: #9ca3af;
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

/* Список рейсов */
.flights-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* Уменьшение ширины элемента списка */
.flight-item {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
  transition: all 0.2s ease;
  max-width: 800px; /* Добавлено ограничение ширины */
  margin: 0 auto; /* Центрирование */
  width: 100%;
}

.flight-item:hover {
  border-color: #d1d5db;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-1px);
}

.flight-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px; /* Уменьшен padding */
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e5e7eb;
}

.flight-number {
  display: flex;
  align-items: center;
  gap: 10px; /* Уменьшен gap */
}

.number-icon {
  font-size: 18px; /* Уменьшен размер иконки */
}

.number-text {
  font-size: 16px; /* Уменьшен размер текста */
  font-weight: 700;
  color: #1f2937;
}

.flight-actions {
  display: flex;
  gap: 8px;
}

.btn-action {
  padding: 6px 12px; /* Уменьшен padding */
  border-radius: 6px;
  font-size: 13px; /* Уменьшен размер шрифта */
  font-weight: 500;
  border: 1px solid transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px; /* Уменьшен gap */
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

/* Контент рейса */
.flight-content {
  padding: 16px 20px; /* Уменьшен padding */
}

.route-section {
  display: flex;
  align-items: center;
  gap: 16px; /* Уменьшен gap */
  margin-bottom: 16px; /* Уменьшен margin */
  padding-bottom: 16px; /* Уменьшен padding */
  border-bottom: 1px solid #f3f4f6;
}

.city-block {
  flex: 1;
}

.city-label {
  font-size: 11px; /* Уменьшен размер шрифта */
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
  font-weight: 600;
}

.city-name {
  font-size: 16px; /* Уменьшен размер шрифта */
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.city-time {
  font-size: 13px; /* Уменьшен размер шрифта */
  color: #4b5563;
  font-weight: 500;
}

.route-line {
  flex: 1; /* Уменьшена гибкость */
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px; /* Уменьшен gap */
  position: relative;
}

.line {
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #e5e7eb, #d1d5db, #e5e7eb);
  position: relative;
}

.arrow {
  position: absolute;
  font-size: 18px; /* Уменьшен размер стрелки */
  color: #6b7280;
  background: white;
  padding: 0 6px; /* Уменьшен padding */
}

.duration {
  font-size: 14px; /* Уменьшен размер шрифта */
  color: #6b7280;
  font-weight: 500;
  background: #f9fafb;
  padding: 3px 6px; /* Уменьшен padding */
  border-radius: 4px;
  margin-top: 10px; /* Уменьшен margin */
}

.flight-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 2 колонки вместо 3 */
  gap: 12px; /* Уменьшен gap */
}

.detail-item {
  background: #f9fafb;
  padding: 12px; /* Уменьшен padding */
  border-radius: 8px;
  text-align: center;
}

.detail-label {
  font-size: 11px; /* Уменьшен размер шрифта */
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
  font-weight: 600;
}

.detail-value {
  font-size: 15px; /* Уменьшен размер шрифта */
  font-weight: 700;
  color: #1f2937;
}

.detail-value.price {
  color: #059669;
}

.detail-value.code {
  color: #7c3aed;
  font-family: 'Monaco', 'Consolas', monospace;
}

/* Анимации */
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Адаптивность */
@media (max-width: 768px) {
  .flight-item {
    max-width: 600px; /* Уменьшена максимальная ширина на мобильных */
  }

  .flight-header {
    flex-direction: column;
    gap: 12px; /* Уменьшен gap */
    align-items: stretch;
  }

  .flight-actions {
    justify-content: flex-start;
  }

  .route-section {
    flex-direction: column;
    gap: 12px; /* Уменьшен gap */
    text-align: center;
  }

  .route-line {
    width: 100%;
    order: 2;
  }

  .line {
    height: 1px;
    width: 100%;
  }

  .arrow {
    position: relative;
    margin: 6px 0; /* Уменьшен margin */
  }

  .duration {
    margin-top: 6px; /* Уменьшен margin */
  }

  .flight-details {
    grid-template-columns: 1fr;
    gap: 10px; /* Уменьшен gap */
  }

  .action-text {
    display: inline;
  }
}

@media (max-width: 480px) {
  .flight-item {
    max-width: 100%; /* На маленьких экранах занимает всю ширину */
  }

  .flight-content {
    padding: 12px 16px; /* Уменьшен padding */
  }

  .flight-header {
    padding: 12px 16px; /* Уменьшен padding */
  }

  .flight-actions {
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