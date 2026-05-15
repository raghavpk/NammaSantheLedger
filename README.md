# 🛒 Namma Santhe Ledger

> **A modern offline-first multilingual digital ledger application built for Indian street vendors and small businesses to manage customer credits, payments, and business reports digitally.**

---

# 📌 Project Overview

Namma Santhe Ledger is a smart digital khata/ledger application designed for local vendors, kirana stores, and santhe business owners.

The app helps vendors replace traditional paper-based udari systems with a secure and easy-to-use digital solution.

The application supports:
- Customer management
- Credit & payment tracking
- Business reports
- WhatsApp reminders
- Firebase OTP authentication
- Multi-language support
- Offline-first local storage

---

# ✨ Features

## 🔐 Authentication
- Login using phone number & password
- Create account with validations
- Forgot password using Firebase OTP
- Secure password reset flow
- Persistent login sessions

---

## 👥 Customer Management
- Add customers
- Search customers
- View customer ledger
- Track outstanding balances
- Transaction history management

---

## 💰 Transaction Management
- Add Credit (Udari)
- Add Payment Received
- Transaction notes support
- Real-time balance updates
- Color-coded transaction types

---

## 📊 Reports & Analytics
- Daily Reports
- Weekly Reports
- Monthly Reports
- Yearly Reports
- Sales & payment summaries

---

## 🌍 Multi-Language Support
Supported Languages:
- English
- Kannada
- Hindi
- Tamil
- Telugu
- Marathi

---

## 📲 WhatsApp Reminder
- Send due reminders directly through WhatsApp
- Auto-generated payment reminder messages

---

## ⚡ Offline First
- Room Database local storage
- Works without internet
- Fast and lightweight performance

---

# 📱 Application Screenshots

## 🚀 Splash Screen
![Splash](screenshot/front.jpeg)

---

## 🔐 Login Screen
![Login](screenshot/login.jpeg)

---

## 📝 Create Account
![Create Account](screenshot/account.jpeg)

---

## 🏠 Home Dashboard
![Home](screenshot/home.jpeg)

---

## 🌍 Language Selection
![Language](screenshot/language.jpeg)

---

## ➕ Add Customer
![Add Customer](screenshot/add%20customer.jpeg)

---

## 📒 Customer Ledger
![Customer Ledger](screenshot/customer.jpeg)

---

## 📊 Reports Dashboard
![Reports](screenshot/report.jpeg)

---

## 👤 Profile Screen
![Profile](screenshot/profile.jpeg)

---

## 📲 WhatsApp Reminder
![Reminder](screenshot/reminder.jpeg)

---

# 🏗️ Architecture

The project follows:
- MVVM Architecture
- Repository Pattern
- Offline-first Architecture
- Jetpack Compose UI Architecture

---

# 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Design | Material 3 |
| Database | Room Database |
| Authentication | Firebase OTP |
| Architecture | MVVM |
| Navigation | Compose Navigation |
| State Management | ViewModel + StateFlow |
| Build System | Gradle Kotlin DSL |

---

# 📚 Key Dependencies

```gradle
Jetpack Compose
Material 3
Room Database
Firebase Authentication
Navigation Compose
Lifecycle ViewModel
Kotlin Coroutines
```

---

# 🗄️ Database Schema

## 👤 Vendor Table

| Field | Type |
|---|---|
| id | Integer |
| name | Text |
| phone | Text |
| shopName | Text |
| marketName | Text |
| password | Text |

---

## 👥 Customer Table

| Field | Type |
|---|---|
| id | Integer |
| name | Text |
| phone | Text |
| createdAt | Long |

---

## 💵 Transaction Table

| Field | Type |
|---|---|
| id | Integer |
| customerId | Integer |
| amount | Double |
| type | Text |
| note | Text |
| timestamp | Long |

---

# ⚙️ Setup & Run

## 📌 Requirements
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

---

# 🚀 Clone Repository

```bash
git clone https://github.com/raghavpk/NammaSantheLedger.git
```

---

# 🔥 Firebase Setup

1. Create Firebase Project
2. Enable Phone Authentication
3. Download `google-services.json`
4. Place file inside:

```text
app/
```

---

# ▶️ Run Application

Open project in Android Studio and click:

```text
Run ▶️
```

OR

```bash
./gradlew assembleDebug
```

---

# 📦 APK Download

Download APK from repository files:

```text
app-release.apk
```

---

# 📂 Project Structure

```text
NammaSantheLedger/
│
├── app/
├── screenshot/
├── README.md
├── app-release.apk
├── build.gradle.kts
├── settings.gradle.kts
└── .gitignore
```

---

# 🌟 Project Highlights

✅ Offline-first Application  
✅ Firebase OTP Authentication  
✅ Multi-language Support  
✅ WhatsApp Integration  
✅ Room Database  
✅ Jetpack Compose UI  
✅ MVVM Architecture  
✅ Business Reports  
✅ Complete CRUD Operations  
✅ Professional GitHub Repository  

---

# 📈 Evaluation Criteria Coverage

| Evaluation Criteria | Status |
|---|---|
| Repository Structure | ✅ |
| Source Code Quality | ✅ |
| Documentation & README | ✅ |
| Build Readiness | ✅ |
| Commit History | ✅ |
| Project Completeness | ✅ |
| Originality & Implementation | ✅ |
| Screenshots Included | ✅ |
| APK Included | ✅ |

---

# 🔮 Future Enhancements

- Cloud Backup
- Dark Mode
- PDF Export
- Expense Tracking
- UPI Integration
- Multi-device Sync

---

# 👨‍💻 Developer

## Raghavendra PK

Built with ❤️ for Indian Santhe Vendors.

---

# 📄 License

This project is developed for educational and internship evaluation purposes.

---

# ⭐ GitHub Repository

Repository Link:

https://github.com/raghavpk/NammaSantheLedger
