# Secure Inter-Component Communication (SIL771 - Assignment 1)

## 📌 Overview
This project consists of two Android apps (**Producer** and **Consumer**) that securely communicate using **services, content providers, and broadcasts**. The apps implement **secure IPC (Inter-Process Communication)** with **custom permissions**.

---

## 📂 Project Structure
```
android-secure-ipc/
️️ producer/        # Producer App (Generates and provides tokens)
️️ Consumer/        # Consumer App (Receives and displays tokens)
️️ README.md        # This documentation file
️️ .gitignore       # Ignore unnecessary files (build, .gradle, etc.)
```

---

## 📱 **Producer App (App 1)**
### 🔹 **Components**
1. **`tokenGenerationUserControl` (Activity)**  
   - Provides buttons to start/stop token generation.  
   - Displays generated tokens on the screen.  

2. **`generateTokens` (Service)**  
   - Generates a **token every 15 seconds** (Format: `[timestamp, latitude, longitude]`).  
   - Saves tokens in `TokenRepository` (database).  
   - Sends a **broadcast after every 4 tokens**.  

3. **`TokenRepository` (Database - Room)**  
   - Stores tokens persistently.  

4. **`provideTokens` (Content Provider)**  
   - Allows **read-only** access to stored tokens for the **Consumer App**.  
   - Restricted using **custom permissions**.  

---

## 📱 **Consumer App (App 2)**
### 🔹 **Components**
1. **`tokensReady` (Broadcast Receiver)**  
   - Receives broadcasts when **4 tokens are generated**.  

2. **`displayTokens` (Activity)**  
   - Fetches tokens from **Producer's Content Provider**.  
   - Displays tokens on screen.  

---

## 🔐 **Security & Permissions**
- **Custom permission (`signature` level) for Content Provider**  
- **Restricted broadcasts (only Consumer can receive)**  
- **Provider access limited to Consumer app**  
- **Proper `AndroidManifest.xml` configurations**

---

## 🚀 **Setup & Installation**
### **🔹 Requirements**
- Android Studio **Giraffe | 2023.3.1** or later
- Android 13 (API 33) Emulator / Physical Device

### **🔹 Steps**
1. **Clone the repository**
   ```sh
   git clone https://github.com/Phaneesh-Katti/Android-Secure-IPC.git
   cd Android-Secure-IPC
   ```

2. **Open in Android Studio**  
   - Open **Producer** and **Consumer** projects separately.

3. **Build & Run**
   - Select **Pixel 6 API 33 (Android 13 - Tiramisu) Emulator**.  
   - Run **Producer** first, then **Consumer**.  

4. **Testing IPC**
   - Start **token generation** in Producer.  
   - Consumer should **receive broadcast after 4 tokens**.  
   - Consumer should **fetch & display tokens from Producer**.  

---

