MotoPark Tokyo
MotoPark Tokyo is a full-stack application that helps users find, add, and favorite motorbike parking spots in Tokyo. It features a Kotlin Android frontend (Jetpack Compose) and a Node.js/Express backend with a PostgreSQL database.

Features
Android App (Frontend)
Login/Register with Firebase Authentication (email + Google)

List all bike parking spots (with spot name, address, capacity, etc.)

Add new parking spots (with details and photo URL)

Map view (Google Maps with markers for each spot)

Mark/unmark favorite spots (star icon, toggle)

View favorites & recently added spots

Modern UI with Jetpack Compose

Backend (Node.js/Express + PostgreSQL)
RESTful API for spots and favorites

User authentication via Firebase ID tokens

CRUD for parking spots

Favorites management (add/remove/get)

PostgreSQL database with migration support

Project Structure

MotoParkTokyo/
 ├─ app/            # Android frontend (Kotlin/Jetpack Compose)
 └─ backend/        # Node.js/Express API backend

Getting Started

Prerequisites

Android Studio (for frontend)

Node.js (v18+ recommended)

PostgreSQL (locally or remote)

Firebase project (for Auth)

Google Maps API key

Backend Setup

Clone the repo and install dependencies


cd backend
npm install


Setup PostgreSQL database

Create a database, e.g. tokyobikefinder

Update backend/.env with your DB credentials

Run database migrations


npm run migrate
# or for Knex: npx knex migrate:latest
Set Firebase Admin credentials

Place your Firebase admin private key in backend/serviceAccountKey.json

Start the backend server


npm start
# Runs at http://localhost:4000/

Android App Setup
Open app/ in Android Studio

Add your Google Maps API key

In app/src/main/AndroidManifest.xml:


<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />
Add your google-services.json (from Firebase Console) to app/

Connect the emulator or device

Run the app

API Endpoints (Backend)
Endpoint	Method	Description	Auth Required
/api/spots	GET	Get all spots	No
/api/spots	POST	Add new spot	Yes
/api/favorites	GET	Get user's favorites	Yes
/api/favorites	POST	Add to favorites	Yes
/api/favorites/:spot_id	DELETE	Remove from favorites	Yes

All POST/DELETE routes require Firebase ID token in Authorization: Bearer <token> header.


Future Improvements
Photo upload from Android app (currently just URL)

Comments and ratings for each spot

Real-time updates and notifications

Better error handling and user feedback

PWA/web frontend


Questions?
Open an issue or contact the maintainer!