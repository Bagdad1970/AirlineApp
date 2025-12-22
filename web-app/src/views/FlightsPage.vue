<template>
  <div class="flights-page">
    <header class="page-header">
      <h1>Управление рейсами</h1>
    </header>

    <div class="content-wrapper">
      <div class="filters-section">
        <div class="filters-layout">
          <div class="filters-left">
            <div class="filters-grid">
              <div class="filter-group">
                <label>Номер рейса</label>
                <input v-model="filters.number" placeholder="SU1234" type="text" />
              </div>

              <div class="filter-group">
                <label>Город отправления</label>
                <input v-model="filters.fromCity" placeholder="Москва" type="text" />
              </div>

              <div class="filter-group">
                <label>Город назначения</label>
                <input v-model="filters.toCity" placeholder="Санкт-Петербург" type="text" />
              </div>
            </div>

            <div class="time-filters">
              <div class="filter-group">
                <label>Дата вылета с</label>
                <input v-model="filters.departureFrom" type="datetime-local" />
              </div>

              <div class="filter-group">
                <label>Дата вылета по</label>
                <input v-model="filters.departureTo" type="datetime-local" />
              </div>
            </div>
          </div>

          <!-- Правый блок: кнопки фильтрации -->
          <div class="filters-right">
            <div class="filter-buttons">
              <button @click="applyFilters" class="btn-primary">
                <span class="btn-text">Поиск</span>
              </button>
              <button @click="clearFilters" class="btn-secondary">
                <span class="btn-text">Очистить</span>
              </button>
              <button @click="fetchAllFlights" class="btn-secondary">
                <span class="btn-text">Все рейсы</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Кнопки действий -->
      <div class="action-buttons">
        <button @click="showAddModal = true" class="btn-success">
          <span class="btn-text">Добавить</span>
        </button>

        <div class="icon-buttons">
          <label class="icon-btn import-btn" title="Импорт CSV">
            <img src="@/../public/import.png" alt="Импорт" class="btn-icon-img" />
            <input
                type="file"
                accept=".csv"
                @change="handleImport"
                style="display: none;"
            />
          </label>

          <label class="icon-btn export-btn" title="Экспорт CSV" @click="handleExport">
            <img src="@/../public/export.png" alt="Экспорт" class="btn-icon-img" />
          </label>
        </div>
      </div>

      <!-- Список рейсов -->
      <FlightList
          :flights="flights"
          :loading="loading"
          :error="error"
          @edit="handleEdit"
          @delete="handleDelete"
      />
    </div>

    <!-- Модальное окно добавления/редактирования -->
    <div v-if="showAddModal || editingFlight" class="modal-overlay">
      <div class="modal">
        <h3>{{ editingFlight ? 'Редактирование рейса' : 'Добавление рейса' }}</h3>

        <form @submit.prevent="submitForm" class="flight-form">
          <div class="form-grid">
            <div class="form-group">
              <label>Номер рейса</label>
              <input v-model="formData.number" required />
            </div>

            <div class="form-group">
              <label>Город отправления</label>
              <input v-model="formData.fromCity" required />
            </div>

            <div class="form-group">
              <label>Город назначения</label>
              <input v-model="formData.toCity" required />
            </div>

            <div class="form-group">
              <label>Дата и время вылета</label>
              <input v-model="formData.departure" type="datetime-local" required />
            </div>

            <div class="form-group">
              <label>Дата и время прибытия</label>
              <input v-model="formData.arrival" type="datetime-local" required />
            </div>

            <div class="form-group">
              <label>Цена билета</label>
              <input v-model="formData.ticketPrice" type="number" step="0.01" min="0" required />
            </div>
          </div>

          <div class="form-actions">
            <button type="button" @click="closeModal" class="btn-cancel">
              Отмена
            </button>
            <button type="submit" class="btn-submit">
              {{ editingFlight ? 'Сохранить' : 'Создать' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Модальное окно подтверждения удаления -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal">
        <h3>Подтверждение удаления</h3>
        <p>Вы уверены, что хотите удалить этот рейс?</p>
        <div class="modal-actions">
          <button @click="showDeleteModal = false" class="btn-cancel">
            Отмена
          </button>
          <button @click="confirmDelete" class="btn-danger">
            Удалить
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import FlightList from '@/components/FlightList.vue';
import { useFlights } from '@/composables/useFlights';
import type { Flight, FlightCreate, FlightUpdate, FlightQuery } from '@/types/flight';

// Композиция для работы с рейсами
const {
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
} = useFlights();

// Фильтры
const filters = reactive<FlightQuery>({
  number: '',
  fromCity: '',
  toCity: '',
  departureFrom: '',
  departureTo: '',
});

// Состояние модальных окон
const showAddModal = ref(false);
const showDeleteModal = ref(false);
const editingFlight = ref<Flight | null>(null);
const flightToDelete = ref<number | null>(null);

// Данные формы
const formData = reactive({
  id: null as number | null,
  number: '',
  fromCity: '',
  toCity: '',
  departure: '',
  arrival: '',
  passengerCount: 0,
  ticketPrice: 0,
});

// Применение фильтров
const applyFilters = () => {
  const query: FlightQuery = {};
  if (filters.number) query.number = filters.number;
  if (filters.fromCity) query.fromCity = filters.fromCity;
  if (filters.toCity) query.toCity = filters.toCity;
  if (filters.departureFrom) query.departureFrom = new Date(filters.departureFrom).toISOString();
  if (filters.departureTo) query.departureTo = new Date(filters.departureTo).toISOString();

  fetchFlights(query);
};

// Очистка фильтров
const clearFilters = () => {
  Object.keys(filters).forEach(key => {
    (filters as any)[key] = '';
  });
  fetchAllFlights();
};

// Загрузка всех рейсов
const fetchAllFlights = () => {
  fetchAll();
};

// Импорт CSV
const handleImport = async (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const success = await importCSV(input.files[0]);
    if (success) {
      alert('Файл успешно импортирован!');
    }
    input.value = '';
  }
};

// Экспорт CSV
const handleExport = async () => {
  await exportCSV();
};

// Редактирование рейса
const handleEdit = (flight: Flight) => {
  editingFlight.value = flight;
  Object.assign(formData, {
    id: flight.id,
    number: flight.number,
    fromCity: flight.fromCity,
    toCity: flight.toCity,
    departure: flight.departure.slice(0, 16),
    arrival: flight.arrival.slice(0, 16),
    passengerCount: flight.passengerCount,
    ticketPrice: flight.ticketPrice,
  });
};

// Удаление рейса
const handleDelete = (id: number) => {
  flightToDelete.value = id;
  showDeleteModal.value = true;
};

const confirmDelete = async () => {
  if (flightToDelete.value) {
    await deleteFlight(flightToDelete.value);
    showDeleteModal.value = false;
    flightToDelete.value = null;
  }
};

// Закрытие модального окна
const closeModal = () => {
  showAddModal.value = false;
  editingFlight.value = null;
  resetForm();
};

// Сброс формы
const resetForm = () => {
  Object.assign(formData, {
    id: null,
    number: '',
    fromCity: '',
    toCity: '',
    departure: '',
    arrival: '',
    passengerCount: 0,
    ticketPrice: 0,
  });
};

// Отправка формы
const submitForm = async () => {
  if (editingFlight.value) {
    // Редактирование существующего рейса
    const updateData: FlightUpdate = {
      id: formData.id!,
      number: formData.number,
      fromCity: formData.fromCity,
      toCity: formData.toCity,
      departure: new Date(formData.departure).toISOString(),
      arrival: new Date(formData.arrival).toISOString(),
      ticketPrice: formData.ticketPrice,
    };

    await updateFlight(updateData);
  } else {
    // Создание нового рейса
    const createData: FlightCreate = {
      number: formData.number,
      fromCity: formData.fromCity,
      toCity: formData.toCity,
      departure: new Date(formData.departure).toISOString(),
      arrival: new Date(formData.arrival).toISOString(),
      passengerCount: formData.passengerCount,
      ticketPrice: formData.ticketPrice,
    };

    await createFlight(createData);
  }

  closeModal();
  fetchAllFlights();
};

// Загрузка данных при монтировании
onMounted(() => {
  fetchAllFlights();
});
</script>

<style scoped>
.flights-page {
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
  gap: 24px;
}

/* Секция фильтрации */
.filters-section {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e8e8e8;
}

.filters-layout {
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

.filters-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.filters-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.time-filters {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-top: 8px;
}

.filters-right {
  width: 160px;
}

.filter-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 4px;
}

.filter-group input {
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  color: #374151;
  background: #ffffff;
  transition: all 0.2s ease;
  height: 42px;
}

.filter-group input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filter-group input::placeholder {
  color: #9ca3af;
}

/* Основные кнопки */
button {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  text-align: center;
}

button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

button:active:not(:disabled) {
  transform: translateY(0);
}

/* Стили для основных кнопок */
.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #1d4ed8 0%, #1e40af 100%);
}

.btn-secondary {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background: linear-gradient(135deg, #4b5563 0%, #374151 100%);
}

.btn-success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.btn-success:hover:not(:disabled) {
  background: linear-gradient(135deg, #059669 0%, #047857 100%);
}

.btn-cancel {
  background: linear-gradient(135deg, #6b7280 0%, #4b5563 100%);
  color: white;
}

.btn-danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
}

.btn-submit {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: linear-gradient(135deg, #059669 0%, #047857 100%);
}

.btn-text {
  white-space: nowrap;
}

/* Кнопки действий */
.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
}

.action-buttons button {
  width: auto;
  min-width: 120px;
  height: 44px;
}



/* Контейнер для иконок импорта/экспорта */
.icon-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.icon-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.icon-btn:active:not(:disabled) {
  transform: translateY(0);
}

.import-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  position: relative;
}

.import-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
}

.export-btn {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.export-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #7c3aed 0%, #6d28d9 100%);
}

/* Иконки внутри кнопок */
.btn-icon-img {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

/* Модальные окна */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
  padding: 20px;
}

.modal {
  background: white;
  border-radius: 16px;
  padding: 32px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal h3 {
  margin: 0 0 24px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 700;
}

/* Форма */
.flight-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.icon-btn {
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
}

.form-group input {
  padding: 12px 14px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  color: #374151;
  background: #ffffff;
  transition: all 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e8e8e8;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.form-actions button,
.modal-actions button {
  width: auto;
  min-width: 100px;
  height: 44px;
}

/* Адаптивность */
@media (max-width: 1024px) {
  .flights-page {
    padding: 20px;
  }

  .filters-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .time-filters {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .filters-layout {
    flex-direction: column;
    gap: 20px;
  }

  .filters-right {
    width: 100%;
  }

  .filter-buttons {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .filter-buttons button {
    flex: 1;
    min-width: 120px;
  }

  .filters-grid {
    grid-template-columns: 1fr;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
    align-items: stretch;
  }

  .action-buttons button {
    width: 100%;
  }

  .icon-buttons {
    align-self: center;
    margin-top: 8px;
  }
}

@media (max-width: 480px) {
  .flights-page {
    padding: 16px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .page-header p {
    font-size: 14px;
  }

  .filters-section {
    padding: 20px;
  }

  .modal {
    padding: 24px;
  }

  .form-actions {
    flex-direction: column;
  }

  .modal-actions {
    flex-direction: column;
  }

  .form-actions button,
  .modal-actions button {
    width: 100%;
  }

  .action-buttons {
    gap: 8px;
  }

  .icon-btn {
    width: 40px;
    height: 40px;
  }

  .btn-icon-img {
    width: 22px;
    height: 22px;
  }
}
</style>