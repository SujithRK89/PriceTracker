# PriceTracker - Real-Time Stock Price App

PriceTracker is a high-performance Android application built with Jetpack Compose. It demonstrates real-time data streaming via WebSockets, following strict Clean Architecture and MVI (Model-View-Intent) principles.

## Features

- **Live Price Feed**: Tracks 25 stock symbols with real-time updates.
- **WebSocket Echo Integration**: Connects to `wss://ws.postman-echo.com/raw`, simulating a live trading floor.
- **Dynamic Sorting**: The feed is automatically sorted by price (highest first) in real-time.
- **Visual Intelligence**: 
    - Color-coded indicators (🟢 connected / 🔴 disconnected).
    - Price change direction arrows (↑/↓) that appear only on delta changes.
    - Elegant 1-second price flash animations (Green/Red) optimized for performance.
- **Symbol Details**: Deep-dive into specific symbols with live-updating hero cards and detailed descriptions.
- **Deep Linking**: Full support for `stocks://symbol/{symbol}` URI scheme.
- **Theme Support**: Adaptive Light and Dark modes.

##  Architecture & Clean Code

The app is built on the foundation of **Clean Architecture** and **MVI**, ensuring a scalable and testable codebase:

- **Unidirectional Data Flow (MVI)**: State is managed via `StateFlow` in ViewModels. UI interacts by sending `Intents`, and receives updates through a single `UiState`. Side effects (navigation, etc.) are handled via `SharedFlow`.
- **Multi-Model Layering**: 
    - `StockDto`: Data layer representation for network operations.
    - `StockDomainModel`: Pure business logic model in the domain layer.
    - `StockUiModel`: UI-optimized model with helper properties like `isUp` and `hasChanged`.
- **Decoupled Business Logic**: UseCases encapsulate single units of logic, keeping ViewModels lean and Repositories focused on data orchestration.
- **Error Handling**: A centralized `NetworkResult<T>` wrapper manages Loading, Success, and Error states globally, with errors displayed via a reusable `AppShell` component.

## Performance Optimizations

To handle high-frequency WebSocket updates smoothly:
- **Batch Processing**: Incoming messages are buffered and processed in batches every 500ms to prevent UI thread starvation.
- **Draw Phase Animations**: Flash animations use `drawBehind` to avoid unnecessary recompositions.
- **Computation Offloading**: Sorting and mapping operations are offloaded to `Dispatchers.Default`.
- **Stable Keys**: `LazyColumn` uses symbol-based keys to optimize item recycling and animations.

##  Tech Stack

- **UI**: Jetpack Compose (100%) with Material3
- **Navigation**: Type-safe Navigation Compose
- **DI**: Dagger Hilt
- **Network**: OkHttp (WebSockets)
- **Concurrency**: Kotlin Coroutines & Flow
- **Testing**: MockK, JUnit 4, Compose Test Rule

##  Setup

1. **API URL**: Add the following to your `local.properties` file:
   ```properties
   WS_URL="wss://ws.postman-echo.com/raw"
   ```
2. **Build**: Sync Gradle and run on an emulator or physical device (API 24+).

## Project Structure

```text
com.srk.pricetracker
├── core
│   ├── components      # AppShell & Reusable UI
│   ├── navigation      # Navigator interfaces
│   └── network         # WebSocket Service & NetworkResult wrapper
├── data
│   ├── mapper          # DTO <-> Domain mapping logic
│   ├── model           # StockDto (Data layer)
│   ├── remote          # PriceDataSource (WebSocket implementation)
│   └── repository      # PriceRepositoryImpl
├── domain
│   ├── model           # StockDomainModel (Domain layer)
│   ├── repository      # Repository interfaces
│   └── usecase         # Pure business logic (StartFeed, GetStocks, etc.)
├── presentation
│   ├── navigation      # App NavHost configuration & AppNavigator
│   └── pricefeed       # MVI Screens
│       ├── details     # Symbol Details Screen
│       │   ├── FeedDetailsContract.kt
│       │   ├── FeedDetailsScreen.kt
│       │   └── FeedDetailsViewModel.kt
│       ├── feed        # Price Feed Screen
│       │   ├── components 
│       │   │   ├── FeedTopBar.kt
│       │   │   └── StockItem.kt
│       │   ├── FeedContract.kt
│       │   ├── FeedScreen.kt
│       │   └── FeedViewModel.kt
│       ├── model       # StockUiModel (Presentation layer)
│       └── navigation  # Feed-specific navigation graph
└── ui.theme            # Material3 Branding
```

## Testing

The project maintains high code quality through:
- **Unit Tests**: Full coverage for ViewModels, UseCases, and Repository logic (`app/src/test`).

## Deep Linking

Test the deep link using the following command:
```bash
adb shell am start -W -a android.intent.action.VIEW -d "stocks://symbol/NVDA"
```
