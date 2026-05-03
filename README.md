# GrooveCrate 💿✨

**GrooveCrate** is an elegant, AI-powered Android application designed for vinyl collectors to inventory their collections with lightning speed. It combines high-performance camera scanning with state-of-the-art Multimodal AI to identify records and sync them directly to your Discogs account.

## ✨ Features

- **Hybrid Identification**: Point at a barcode for instant precision or snap a photo of the cover to let the **Gemini 3 Flash AI** identify the record.
- **"Supermarket Style" Bulk Scanning**: Scan an entire crate of records in one go. Review your list, delete mismatches, and upload everything in a single tap.
- **Dynamic Dashboard**: View your collection stats at a glance—total records, artist counts, and estimated collection value.
- **Seamless Discogs Sync**: Full integration with the Discogs API, including a remote library browser with infinite scroll and pull-to-refresh.
- **Luxury Dark UI**: A refined "Analog-inspired" interface using a deep black and gold color palette.
- **Privacy Centric**: Your API keys and personal tokens are managed securely via `local.properties` and never touch version control.

## 🛠️ Prerequisites

To unleash the full power of GrooveCrate, you need:
1.  **Gemini API Key**: Obtain one for free at [Google AI Studio](https://aistudio.google.com/).
2.  **Discogs Personal Access Token**: Generate one in your [Discogs Developer Settings](https://www.discogs.com/settings/developers).
3.  **Discogs Username**: Your public username.

## 🚀 Setup Instructions

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/astamato/RecordInventory.git
    ```

2.  **Configure API Keys**:
    Open (or create) the `local.properties` file in the root directory and add your credentials:
    ```properties
    GEMINI_API_KEY=your_gemini_api_key_here
    DISCOGS_TOKEN=your_discogs_personal_access_token_here
    DISCOGS_USERNAME=your_discogs_username_here
    ```

3.  **Build & Launch**:
    Open in **Android Studio**, sync Gradle, and deploy to your device.

## 📱 How to Use

1.  **Crate Dashboard**: Start here to see your collection stats.
2.  **Scan a Shelf**: Launch the "Shelf Mode" camera.
    -   Point at a barcode for auto-detection.
    -   Tap the capture button to identify by cover art.
3.  **Inventory Review**: Tap the list icon to see your "shopping cart" of scanned records.
4.  **Bulk Upload**: Tap "Upload All" to sync your batch to your Discogs account.
5.  **Remote Library**: Browse your entire existing collection and pull down to refresh.

## 🏗️ Architecture

-   **UI**: Jetpack Compose (Modern, declarative UI).
-   **Brain**: Google Gemini 3 Flash (AI identification).
-   **Vision**: Jetpack CameraX + ML Kit Barcode Scanning.
-   **Networking**: Retrofit + OkHttp (Discogs API).
-   **Images**: Coil (Efficient remote thumbnail loading).
-   **Code Quality**: ktlint (Consistent formatting).

---
*Built with ❤️ for record lovers. Keep spinning.*
