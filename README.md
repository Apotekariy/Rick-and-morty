Rick and Morty Android App
Современное Android приложение для просмотра персонажей из сериала "Rick and Morty", построенное на базе Rick and Morty API. Приложение реализовано с использованием современного Android tech stack и поддерживает полнофункциональную работу в офлайн режиме.
Основные требования

Кэширование и офлайн работа
Поиск и фильтрация (офлайн)
Навигация
Обработка пустых состояний
Pull-to-Refresh
Индикаторы загрузки

Технологический стек
Архитектура

Clean Architecture с разделением на слои (data, domain, presentation)
MVVM паттерн с использованием ViewModel
Repository паттерн для управления данными

Dependency Injection

Dagger Hilt

База данных

Room Database
TypeConverters для сложных типов данных

Сетевые операции

Retrofit
OkHttp
Kotlinx Serialization

Пагинация

Paging 3 с RemoteMediator для синхронизации сетевых и локальных данных

Изображения

Coil 3 для автоматического кеширования изображений

Пользовательский интерфейс

Jetpack Compose
Material Design 3 компоненты

Навигация

Navigation Compose

Асинхронность

Kotlin Coroutines
Flow

Структура проекта
com.example.rickandmorty/
├── data/
│   ├── local/          # Room database, DAO, entities
│   ├── remote/         # API service, DTOs, RemoteMediator
│   └── mappers/        # Mappers между слоями
├── domain/             # Business logic, repository interfaces, models
├── presentation/       # UI, ViewModels, Compose screens
├── di/                 # Dagger Hilt modules
└── navigation/         # Navigation setup
Дополнительные возможности
Система логирования
Реализуется комплексная система логирования для всех компонентов приложения. Логирование включает отслеживание состояний фильтров и поиска, мониторинг сетевых запросов и ошибок, логирование состояний пагинации и кеширования для эффективной отладки и мониторинга работы системы.
Кнопка ручного обновления
Аналогичная функциональность Pull-to-Refresh, реализованная через кнопку в интерфейсе для альтернативного способа обновления данных.
