# MotoPark Tokyo

MotoPark Tokyo is a modern Android application to help Tokyo’s motorbike and scooter riders **find, share, and save parking spots**—with Google Maps, real-time community updates, and personal favorites.

## Features

- **View Parking Spots:** See legal, up-to-date bike parking on a map or list
- **Add New Spots:** Easily add a new parking location and share it with the community
- **Save Favorites:** Mark your favorite parking places and access them anytime
- **User Authentication:** Sign in with email/password or Google (powered by Firebase Auth)
- **Recent Spots:** Quickly access your recent favorites
- **Modern UI:** Built in Jetpack Compose, Material 3, and optimized for mobile

## Tech Stack

- **Frontend:** Kotlin, Jetpack Compose, Material 3, Google Maps SDK
- **Backend:** Node.js, Express.js, PostgreSQL, Ktor (HTTP client)
- **Authentication:** Firebase Authentication (email/password & Google Sign-in)
- **Image Uploads:** (future: will support photo uploads for spots)
- **API:** RESTful, secure with Firebase token middleware

## Screenshots

![MotoPark Tokyo Logo](./app/src/main/res/drawable/logo.png)
*(Add your screenshots here)*

## How It Works

1. **Sign In:** Use Google or email to log in securely.
2. **Browse or Search:** Find parking spots in the list or directly on the map.
3. **Add Spots:** Tap “Add” to submit a new parking place with name, address, coordinates, and more.
4. **Favorite:** Tap the ⭐ on any spot to save it for quick access.
5. **Manage Favorites:** See your favorites and recent spots in a dedicated screen.

## Backend API Endpoints

- `GET /api/spots` — Get all parking spots
- `POST /api/spots` — Add a new spot (auth required)
- `GET /api/favorites` — Get current user’s favorites (auth required)
- `POST /api/favorites` — Add spot to favorites (auth required)
- `DELETE /api/favorites/:spot_id` — Remove a favorite (auth required)

## Setup (Local Development)

### Prerequisites

- Android Studio (Giraffe or newer recommended)
- Node.js (18+), npm
- PostgreSQL

### 1. Clone the repo

```bash
git clone https://github.com/YOUR_USERNAME/MotoParkTokyo.git
cd MotoParkTokyo
