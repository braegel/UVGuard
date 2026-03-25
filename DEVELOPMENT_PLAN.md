# UVGuard - Development Plan

## Overview

UVGuard is a free, open-source Android app that displays the current and maximum forecasted UV index for today based on the user's location. It periodically updates location and UV data, sends notifications when a configurable UV threshold is exceeded (and when it drops below again), and provides skin-type-based recommendations for safe sun exposure time with and without UV protection.

---

## Phase 1: Project Foundation & Architecture

### Step 1 - Project Setup & Dependencies

- Add dependencies: Retrofit/OkHttp (API calls), Hilt (dependency injection), DataStore (preferences), Google Play Services Location, WorkManager (background tasks)
- Set up MVVM architecture with Repository pattern
- Create package structure: `data/`, `domain/`, `ui/`, `service/`, `worker/`

### Step 2 - Data Models & Domain Layer

- UV index data model (current value, forecast, timestamp, coordinates)
- Skin type enum (Fitzpatrick scale types I-VI) with associated maximum exposure times
- Settings model (threshold, skin type, update interval)

---

## Phase 2: Location

### Step 3 - Location Service

- Request `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` permissions
- Runtime permission handling with Compose
- Use FusedLocationProviderClient for current position
- Fallback handling when location is unavailable

---

## Phase 3: UV Index API Integration

### Step 4 - Fetch UV Data

- API: **Open-Meteo API** (free, no API key required, provides current UV index + hourly forecast) - ideal for open source
- Implement Retrofit interface + repository
- Parse current UV index and daily maximum forecast
- Error handling (no network, API errors)

---

## Phase 4: Main Screen (UI)

### Step 5 - UV Index Display

- Large UV index value with color coding (Green -> Yellow -> Orange -> Red -> Violet)
- Daily maximum forecast with time of day
- UV category text (Low / Moderate / High / Very High / Extreme)
- Location display (city / coordinates)
- Pull-to-refresh for manual update
- Last updated timestamp

---

## Phase 5: Skin Type & Recommendations

### Step 6 - Settings Screen

- Skin type selection (Fitzpatrick I-VI) with description
- UV index notification threshold (slider/dropdown)
- Configurable update interval
- Persistence via Jetpack DataStore

### Step 7 - Recommendation Display

- Calculate maximum sun exposure time based on: UV index x skin type x protection factor
- Display: "Without protection: X minutes", "With SPF 30: Y minutes"
- Protection recommendations (sunscreen, clothing, shade, avoid sun)

---

## Phase 6: Background Updates & Notifications

### Step 8 - WorkManager for Periodic Updates

- PeriodicWorkRequest for regular location and UV data fetching
- Configurable interval from settings (minimum 15 min due to WorkManager constraint)

### Step 9 - Notifications

- Create notification channel
- Notify when UV index **exceeds** threshold
- Notify when UV index **drops below** threshold again
- Track state to prevent duplicate notifications

---

## Phase 7: Polish & Play Store

### Step 10 - UI Polish

- Design app icon matching UV theme
- Test dark/light mode
- Landscape support
- Localization (English + German)

### Step 11 - Open Source & Play Store Preparation

- `LICENSE` file (e.g. MIT or Apache 2.0)
- Create privacy policy (location data disclosure, required for Play Store)
- Configure ProGuard/R8 for release build
- Signing config for release
- Play Store listing (screenshots, description)

---

## Recommended Implementation Order

| # | What | Why first |
|---|------|-----------|
| 1 | Project setup & dependencies | Foundation for everything |
| 2 | Data models | Defines the structure |
| 3 | Location service | Prerequisite for API call |
| 4 | UV API integration | Core data of the app |
| 5 | Main screen | First visible functionality |
| 6 | Settings | Skin type & threshold |
| 7 | Recommendations | Core feature |
| 8 | Background updates | Automation |
| 9 | Notifications | Threshold alerts |
| 10 | UI polish & localization | Quality |
| 11 | Play Store release | Publication |
