# Record Inventory App 🎶

An Android application that uses AI to identify vinyl records from photos and automatically adds them to your Discogs collection.

## Features
- **Smart Identification**: Uses **Gemini 3 Flash** (Multimodal AI) to recognize album covers, even without clear text.
- **Discogs Integration**: Automatically searches the Discogs database and adds identified records to your collection.
- **CameraX**: High-performance camera implementation for quick snapping.
- **Privacy First**: API keys and personal tokens are stored locally and never committed to version control.

## Prerequisites
Before running the app, you will need:
1. A **Gemini API Key** from [Google AI Studio](https://aistudio.google.com/).
2. A **Discogs Personal Access Token** from [Discogs Developer Settings](https://www.discogs.com/settings/developers).
3. Your **Discogs Username**.

## Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/astamato/RecordInventory.git
   ```

2. **Configure your API Keys**:
   Create a file named `local.properties` in the root directory of the project (if it doesn't already exist) and add the following lines:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   DISCOGS_TOKEN=your_discogs_personal_access_token_here
   DISCOGS_USERNAME=your_discogs_username_here
   ```

3. **Build and Run**:
   Open the project in **Android Studio** and run it on your physical device or emulator.

## How it Works
1. **Snap**: Point your camera at a record cover and press "Take Photo".
2. **Identify**: The image is sent to Gemini AI, which returns the Artist and Album title.
3. **Sync**: Tap "Add to Collection" to find the release on Discogs and save it to your account instantly.

## Architecture
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Networking**: Retrofit & OkHttp
- **AI**: Google Generative AI SDK (Gemini)
- **Camera**: Jetpack CameraX

---
*Developed for personal use to quickly inventory large record collections.*
