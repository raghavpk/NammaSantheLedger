# 🛒 Namma Santhe Ledger

> **A modern, multi-language digital ledger app for Indian street vendors (santhe vendors) to track customer credits, payments, and business overview — all offline-first.**

---

## 📱 Screenshots & Features

### 🏠 Home Dashboard  
- Greeting card with Namaskara hand icon
- Real-time business summary: Total Outstanding, Today's Sales, Credits, Payments
- Customer count overview
- Quick action cards: Customers, Reports, Profile
- **Language selector** (🌐 icon) and **Profile avatar** in top bar

### 👥 Customer Management
- Add customers with name and phone number (+91 prefix)
- Search customers by name
- View individual customer ledger with transaction history
- Customer balance tracking (credit vs payment)
- Delete customer with swipe
    
### 💰 Transaction Entry
- Add **Credit (Udari)** or **Payment Received**
- Custom numeric keypad for quick amount entry  
- Optional transaction notes  
- Color-coded: Red for credit, Green for payment

### 📊 Business Overview (Reports)
- **Daily / Weekly / Monthly / Yearly** report periods
- Total sales, credit given, payments received summary
- Transaction history list for selected period
- Data persists correctly across days

### 👤 Profile Management
- Edit name, shop name, market/santhe name
- Phone number display (read-only)
- Change password with validation
- Profile photo upload
- Language selection
- Logout with confirmation

### 🔐 Authentication
- Phone number + password login
- Account creation with validation
- Password requirements: uppercase, lowercase, digit, special character
- Forgot password with **Firebase Phone OTP**
- Persistent login sessions

### 🌍 Multi-Language Support
- **6 Languages**: English, ಕನ್ನಡ (Kannada), हिन्दी (Hindi), தமிழ் (Tamil), తెలుగు (Telugu), मराठी (Marathi)
- All UI strings externalized to `strings.xml`
- Language persists across app restarts
- Switch language from Home screen (🌐 icon) or Profile page

---

## 🏗️ Architecture

```
NammaSantheLedger/
├── app/
│   ├── google-services.json          # Firebase config
│   ├── build.gradle.kts              # App dependencies
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/namma/santheledger/
│       │   ├── MainActivity.kt       # Entry point, locale & keyboard handling
│       │   ├── NammaSantheApp.kt     # Application class, DB init
│       │   ├── data/
│       │   │   ├── model/
│       │   │   │   ├── Customer.kt         # Customer entity
│       │   │   │   ├── Transaction.kt      # Transaction entity (CREDIT/PAYMENT)
│       │   │   │   └── VendorProfile.kt    # Vendor/shop owner entity
│       │   │   ├── dao/
│       │   │   │   ├── CustomerDao.kt      # Customer DB operations
│       │   │   │   ├── TransactionDao.kt   # Transaction DB operations
│       │   │   │   └── VendorDao.kt        # Vendor DB operations
│       │   │   ├── db/
│       │   │   │   └── AppDatabase.kt      # Room database (Singleton)
│       │   │   └── repository/
│       │   │       └── LedgerRepository.kt # Data access layer
│       │   ├── ui/
│       │   │   ├── components/
│       │   │   │   ├── CustomerCard.kt     # Customer list item
│       │   │   │   ├── NumericKeypad.kt    # Custom number pad
│       │   │   │   └── TransactionItem.kt  # Transaction list item
│       │   │   ├── navigation/
│       │   │   │   └── NavGraph.kt         # Compose Navigation routes
│       │   │   ├── screens/
│       │   │   │   ├── LoginScreen.kt
│       │   │   │   ├── CreateAccountScreen.kt
│       │   │   │   ├── ForgotPasswordScreen.kt
│       │   │   │   ├── SplashScreen.kt
│       │   │   │   ├── HomeScreen.kt
│       │   │   │   ├── CustomerListScreen.kt
│       │   │   │   ├── AddCustomerScreen.kt
│       │   │   │   ├── CustomerLedgerScreen.kt
│       │   │   │   ├── AddTransactionScreen.kt
│       │   │   │   ├── BusinessOverviewScreen.kt
│       │   │   │   └── ProfileScreen.kt
│       │   │   └── theme/
│       │   │       └── Theme.kt, Color.kt, Type.kt
│       │   ├── util/
│       │   │   └── LocaleHelper.kt   # Language switching utility
│       │   └── viewmodel/
│       │       ├── AuthViewModel.kt        # Login, signup, OTP, password
│       │       ├── CustomerViewModel.kt    # Customer CRUD
│       │       ├── HomeViewModel.kt        # Dashboard stats
│       │       └── TransactionViewModel.kt # Credit/payment operations
│       └── res/
│           ├── values/strings.xml          # English (default)
│           ├── values-kn/strings.xml       # ಕನ್ನಡ
│           ├── values-hi/strings.xml       # हिन्दी
│           ├── values-ta/strings.xml       # தமிழ்
│           ├── values-te/strings.xml       # తెలుగు
│           └── values-mr/strings.xml       # मराठी
└── build.gradle.kts                  # Project-level config
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Navigation** | Compose Navigation |
| **Database** | Room (SQLite) — offline-first |
| **Authentication** | Firebase Phone Auth (OTP) + local password |
| **State Management** | ViewModel + StateFlow |
| **Dependency Injection** | Manual (Application class) |
| **Build System** | Gradle KTS |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 34 (Android 14) |

### Key Dependencies
```
Compose BOM: 2024.02.00
Firebase BOM: 33.0.0
Room: 2.6.1
Navigation Compose: 2.7.7
Lifecycle: 2.7.0
Kotlin: 2.0.21
AGP: 8.2.2
KSP: 2.0.21-1.0.26
```

---

## 🚀 Setup & Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android device/emulator (API 24+)

### Steps

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd NammaSantheLedger
   ```

2. **Firebase Setup** (for OTP)
   - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
   - Add an Android app with package name: `com.namma.santheledger`
   - Add SHA-1 & SHA-256 fingerprints (run `.\gradlew signingReport`)
   - Enable **Phone Authentication** in Firebase Console → Authentication → Sign-in method
   - Download `google-services.json` → place in `app/` folder

3. **Build & Run**
   ```bash
   .\gradlew assembleDebug
   ```
   Or open in Android Studio and click Run ▶️

---

## 📊 Database Schema

### `vendor_profile`
| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-generated |
| name | TEXT | Vendor name |
| shopName | TEXT | Shop/business name |
| phone | TEXT | Phone with country code (e.g., "918431198119") |
| password | TEXT | Hashed password |
| marketName | TEXT | Market/santhe name |
| photoUri | TEXT | Profile photo URI |
| createdAt | LONG | Timestamp |

### `customers`
| Column | Type | Description |
|--------|------|-------------|
| id | LONG (PK) | Auto-generated |
| name | TEXT | Customer name |
| phone | TEXT | Customer phone |
| createdAt | LONG | Timestamp |

### `transactions`
| Column | Type | Description |
|--------|------|-------------|
| id | LONG (PK) | Auto-generated |
| customerId | LONG (FK) | References customers.id |
| amount | DOUBLE | Transaction amount |
| type | TEXT | "CREDIT" or "PAYMENT" |
| note | TEXT | Optional note |
| date | LONG | Timestamp |

---

## 🌍 Adding a New Language

1. Create `app/src/main/res/values-{code}/strings.xml`
2. Translate all string entries from `values/strings.xml`
3. Add the language to `LocaleHelper.kt`:
   ```kotlin
   Language("xx", "Language Name", "Native Name")
   ```
4. The language will automatically appear in the selector

---

## 📋 App Navigation Flow

```
SplashScreen
    ├── LoginScreen (if not logged in)
    │   ├── CreateAccountScreen
    │   └── ForgotPasswordScreen (Firebase OTP)
    └── HomeScreen (if logged in)
        ├── CustomerListScreen
        │   ├── AddCustomerScreen
        │   └── CustomerLedgerScreen
        │       └── AddTransactionScreen
        ├── BusinessOverviewScreen (Reports)
        └── ProfileScreen (Edit profile, change password, language)
```

---

## ⚠️ Known Notes

- **Firebase Phone Auth** requires enabling Phone provider and adding SHA fingerprints in Firebase Console
- **Password storage** is in plain text in local Room DB (suitable for local-only app; not for production with server sync)
- **Data is local-only** — stored on device using Room/SQLite. No cloud sync.
- The app uses `fallbackToDestructiveMigration()` — schema changes will clear existing data

---

## 📄 License

This project is for personal/educational use.

---

## 👨‍💻 Developer

Built with ❤️ for Indian street vendors — **"Namma Santhe"** (Our Market)
