# GrooveCrate 💿✨

**GrooveCrate** is an elegant, AI-powered Android application designed for vinyl collectors to inventory their collections with lightning speed. It combines high-performance camera scanning with state-of-the-art Multimodal AI to identify records and sync them directly to your Discogs account.

## ✨ Features

- **Hybrid Identification**: Point at a barcode for instant precision or snap a photo of the cover to let the **Gemini 3 Flash AI** identify the record.
- **"Supermarket Style" Bulk Scanning**: Scan an entire crate of records in one go. Review your list, delete mismatches, and upload everything in a single tap.
- **Dynamic Dashboard**: View your collection stats at a glance—total records, artist counts, and estimated collection value—with a personalized greeting.
- **Seamless Discogs Sync**: Full integration with the Discogs API, including a remote library browser with infinite scroll and pull-to-refresh.
- **Secure Profile System**: Multi-user support with encrypted on-device storage for Discogs credentials using `EncryptedSharedPreferences`.
- **Luxury Dark UI**: A refined "Analog-inspired" interface using a deep black and gold color palette.
- **Privacy Centric**: Build-time secrets are managed via `local.properties`, and user tokens are encrypted on-device.

## 🛠️ Prerequisites

To unleash the full power of GrooveCrate, you need:
1.  **Gemini API Key**: Obtain one for free at [Google AI Studio](https://aistudio.google.com/).
2.  **Discogs Personal Access Token**: Generate one in your [Discogs Developer Settings](https://www.discogs.com/settings/developers).

## 🚀 Setup Instructions

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/astamato/RecordInventory.git
    ```

2.  **Configure Gemini API Key**:
    Open (or create) the `local.properties` file in the root directory and add your key:
    ```properties
    GEMINI_API_KEY=your_gemini_api_key_here
    ```

3.  **Build & Launch**:
    Open in **Android Studio**, sync Gradle, and deploy to your device.

4.  **In-App Onboarding**:
    On first launch, you will be prompted to enter your **Discogs Username** and **Personal Access Token** to link your account.

## 📱 How to Use

1.  **Crate Dashboard**: Start here to see your collection stats. Tap the profile icon to update your credentials.
2.  **Scan a Shelf**: Launch the "Shelf Mode" camera.
    -   Point at a barcode for auto-detection.
    -   Tap the capture button to identify by cover art.
3.  **Inventory Review**: Tap the list icon to see your "shopping cart" of scanned records.
4.  **Bulk Upload**: Tap "Upload All" to sync your batch to your Discogs account.
5.  **Remote Library**: Browse your entire existing collection and pull down to refresh.

## 🏗️ Architecture & Tech Stack

-   **UI**: Jetpack Compose with a modular component architecture.
-   **DI**: **Koin** for modern, lightweight dependency injection.
-   **Brain**: Google **Gemini 3 Flash** for advanced multimodal image identification.
-   **Vision**: **Jetpack CameraX** + **ML Kit Barcode Scanning**.
-   **Networking**: **Retrofit** + **OkHttp** for robust API communication.
-   **Images**: **Coil** for efficient remote thumbnail loading.
-   **Storage**: **EncryptedSharedPreferences** for secure credential management.
-   **Code Quality**: **ktlint** integration for consistent formatting.

---
*Built with ❤️ for record lovers. Keep spinning.*
