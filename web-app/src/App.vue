<template>
  <div id="app">
    <!-- Навигационная панель -->
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-brand">
          <span class="brand-text">Airline App</span>
        </div>

        <div class="navbar-menu">
          <router-link to="/flights" class="nav-link" active-class="active">
            Рейсы
          </router-link>

          <router-link to="/bookings" class="nav-link" active-class="active">
            Бронирование
          </router-link>

          <router-link to="/statistics" class="nav-link" active-class="active">
            Статистика
          </router-link>
        </div>

        <div class="navbar-actions">
          <button class="btn-refresh" @click="refreshPage" title="Обновить">
            🔄
          </button>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <div class="content-container">
        <div v-if="globalError" class="global-error">
          <div class="error-content">
            <span class="error-icon">⚠️</span>
            <span class="error-text">{{ globalError }}</span>
            <button class="error-close" @click="globalError = null">×</button>
          </div>
        </div>

        <div v-if="globalLoading" class="global-loading">
          <div class="loading-spinner"></div>
          <span class="loading-text">Загрузка...</span>
        </div>

        <!-- Router View -->
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Состояние приложения
const globalError = ref<string | null>(null);
const globalLoading = ref(false);
const showScrollToTop = ref(false);

// Определение статуса подключения
const isOnline = ref(navigator.onLine);
const statusClass = ref(isOnline.value ? 'status-online' : 'status-offline');
const statusText = ref(isOnline.value ? 'Онлайн' : 'Офлайн');

// Обработчики событий сети
const updateOnlineStatus = () => {
  isOnline.value = navigator.onLine;
  statusClass.value = isOnline.value ? 'status-online' : 'status-offline';
  statusText.value = isOnline.value ? 'Онлайн' : 'Офлайн';

  if (!isOnline.value) {
    globalError.value = 'Отсутствует подключение к интернету. Некоторые функции могут быть недоступны.';
  } else {
    globalError.value = null;
  }
};

// Обновление страницы
const refreshPage = () => {
  window.location.reload();
};

// Прокрутка к верху
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
};

// Обработчик прокрутки
const handleScroll = () => {
  showScrollToTop.value = window.scrollY > 300;
};

// Инициализация
onMounted(() => {
  // Слушатели событий
  window.addEventListener('online', updateOnlineStatus);
  window.addEventListener('offline', updateOnlineStatus);
  window.addEventListener('scroll', handleScroll);
});

// Очистка
onUnmounted(() => {
  window.removeEventListener('online', updateOnlineStatus);
  window.removeEventListener('offline', updateOnlineStatus);
  window.removeEventListener('scroll', handleScroll);
});

// Глобальный обработчик ошибок
window.onerror = (message, source, lineno, colno, error) => {
  console.error('Global error:', { message, source, lineno, colno, error });
  globalError.value = `Произошла ошибка: ${message}`;
  return false;
};
</script>

<style>
/* Глобальные стили */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --primary-color: #2563eb;
  --secondary-color: #64748b;
  --success-color: #10b981;
  --danger-color: #ef4444;
  --warning-color: #f59e0b;
  --background-color: #f8fafc;
  --surface-color: #ffffff;
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --border-color: #e2e8f0;
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1);
  --radius-sm: 0.375rem;
  --radius-md: 0.5rem;
  --radius-lg: 0.75rem;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
  background-color: var(--background-color);
  color: var(--text-primary);
  line-height: 1.5;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Навигационная панель */
.navbar {
  background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
  color: white;
  padding: 0 1.5rem;
  box-shadow: var(--shadow-lg);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 0;
  height: 70px;
}

.navbar-brand {
  display: flex;
  align-items: center;
}

.brand-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: white;
  gap: 12px;
  font-size: 1.25rem;
  font-weight: 700;
  transition: opacity 0.2s;
}

.brand-link:hover {
  opacity: 0.9;
}

.brand-icon {
  font-size: 1.5rem;
}

.brand-text {
  font-size: 1.3rem;
  letter-spacing: -0.025em;
}

.navbar-menu {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.nav-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 0.75rem;
  border-radius: var(--radius-md);
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-link.active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-weight: 600;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.btn-refresh {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: white;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.status-indicator {
  font-size: 1.5rem;
  line-height: 1;
  cursor: default;
}

.status-online {
  color: #22c55e;
  text-shadow: 0 0 8px rgba(34, 197, 94, 0.4);
}

.status-offline {
  color: #ef4444;
  text-shadow: 0 0 8px rgba(239, 68, 68, 0.4);
}

/* Основной контент */
.main-content {
  flex: 1;
  padding: 2rem 1.5rem;
}

.content-container {
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

/* Сообщения об ошибках */
.global-error {
  background: linear-gradient(135deg, #fecaca 0%, #fca5a5 100%);
  border-left: 4px solid var(--danger-color);
  border-radius: var(--radius-md);
  padding: 1rem 1.5rem;
  margin-bottom: 1.5rem;
  animation: slideIn 0.3s ease-out;
}

.error-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.error-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.error-text {
  flex: 1;
  font-weight: 500;
}

.error-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--text-primary);
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.error-close:hover {
  background: rgba(0, 0, 0, 0.1);
}

/* Загрузка */
.global-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  background: var(--surface-color);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  margin-bottom: 1.5rem;
  animation: fadeIn 0.3s ease-out;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 3px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

.loading-text {
  font-size: 1.1rem;
  color: var(--text-secondary);
  font-weight: 500;
}

/* Анимации */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@keyframes slideIn {
  from {
    transform: translateX(-20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Футер */
.footer {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  color: white;
  padding: 2.5rem 1.5rem 0;
  margin-top: auto;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  padding-bottom: 2rem;
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-section h4 {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #e2e8f0;
}

.footer-section p {
  color: #cbd5e1;
  line-height: 1.6;
}

.version {
  font-size: 0.875rem;
  color: #94a3b8;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding: 1.5rem 0;
  text-align: center;
}

.footer-bottom p {
  color: #94a3b8;
  font-size: 0.875rem;
}

/* Кнопка "Наверх" */
.scroll-to-top {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), #1d4ed8);
  color: white;
  border: none;
  cursor: pointer;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-lg);
  transition: all 0.3s;
  z-index: 100;
  animation: bounceIn 0.5s ease-out;
}

.scroll-to-top:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(37, 99, 235, 0.3);
}

@keyframes bounceIn {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

/* Адаптивность */
@media (max-width: 768px) {
  .navbar-container {
    padding: 0.75rem 0;
    height: 60px;
  }

  .brand-text {
    display: none;
  }

  .navbar-menu {
    gap: 0.5rem;
  }

  .nav-link {
    padding: 0.4rem 0.6rem;
    font-size: 0.9rem;
  }

  .main-content {
    padding: 1rem;
  }

  .footer-container {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .scroll-to-top {
    bottom: 1rem;
    right: 1rem;
    width: 42px;
    height: 42px;
  }
}

@media (max-width: 480px) {
  .navbar-container {
    flex-direction: column;
    height: auto;
    padding: 1rem;
    gap: 1rem;
  }

  .navbar-menu {
    width: 100%;
    justify-content: center;
    flex-wrap: wrap;
  }

  .footer {
    padding: 2rem 1rem 0;
  }
}

/* Утилиты */
.visually-hidden {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>