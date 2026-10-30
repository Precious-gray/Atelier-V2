# Atelier-V2

A fashion design management tool that keeps a designer's whole workflow in one place: fabrics, garment designs, client records, measurements, appointments, and inspiration.

## The problem
Designers and tailors often keep measurements in notebooks, fabric details in photos, and appointments in chat apps. Information gets lost, and finding a client's last measurements or a particular fabric takes too long. Atelier-V2 puts everything in one organized system.

## Features
- **Fabric library:** store and browse fabrics with their details
- **Garment designs:** save and organize designs in one place
- **Client pages:** a dedicated page for each client
- **Measurements:** record and look up each client's measurements
- **Appointments:** schedule and manage fittings and consultations
- **Inspiration board:** collect garment ideas and references

# Tech stack
- Android (Gradle with Kotlin DSL)
- Kotlin
- Jetpack Compose
- Firebase Firestore for data storage with security rules
- Google Gemini API 

## Getting started
1. Install [Android Studio](https://developer.android.com/studio).
2. Clone the repo:
   `git clone https://github.com/Precious-gray/Atelier-V2.git`
3. Open the `Atelier-V2` folder in Android Studio and wait for Gradle to sync.
4. Copy `.env.example` to `.env`, uncomment the `GEMINI_API_KEY` line, and add your own key from [Google AI Studio](https://aistudio.google.com).
5. Run the app on an emulator or a connected phone.

## Screenshots
![Dashboard](https://1drv.ms/f/c/bf0ab8728d21d921/IgAvr-DvraRlSq2u2q7j-i39AVxFrly81oXbLwhKOSjKTKY?e=2fdBjj)
![Client page](https://1drv.ms/i/c/bf0ab8728d21d921/IQBqFucfj54dSZSI_k-I7SLOAXoUFjzuJTGmXQIdt40TcZY?e=82TZbe)
![Menu](https://1drv.ms/f/c/bf0ab8728d21d921/IgAvr-DvraRlSq2u2q7j-i39AVxFrly81oXbLwhKOSjKTKY?e=1bx1sg)
![More/Settings](https://1drv.ms/f/c/bf0ab8728d21d921/IgAvr-DvraRlSq2u2q7j-i39AVxFrly81oXbLwhKOSjKTKY?e=kgxOzL)

## Design decisions
- **Firebase Firestore for storage.** Designers need to look up client details, measurements, and appointments on a phone during fittings, and Firestore gives a cloud database that syncs without me running a server.
- **Client-centred data in Firebase Firestore.** Each client has their own page, and their measurements and appointments are tied to that client, so everything about one person is in one place. Firestore lets designers look this up on a phone during fittings, with no server for me to run.
- **A shared design library alongside a private workspace.** The app ships with 500 garment designs that every user can browse, so new users get inspiration from day one. Each user can also add their own garment designs, patterns, fabrics, and sketches, which stay private to their account. I kept the shared catalogue and user-owned data separate so a designer's work is never exposed to others, while the public library stays the same for everyone.
- **Locked-down access.** Database access is controlled through `firestore.rules`, so client data isn't open to everyone.

## Future improvements
- **Move the Gemini API key to Firebase Cloud Functions.** The key is currently packaged into the APK at build time, so someone who downloads the app could extract it. Since the app already uses Firebase, I plan to route AI requests through a Cloud Function that stores the key as a secret and calls Gemini on the app's behalf. Firebase Authentication and App Check can restrict who is allowed to call it.
## Author
Built by Precious Okpara · linkedin.com/in/precious-okpara-28153a20b
