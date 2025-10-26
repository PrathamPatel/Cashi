
# Cashi Technical Assessment

A Kotlin Multiplatform (KMP) fintech demo app that showcases sending and tracking payments across Android and backend services. Built with Jetpack Compose, Ktor, Firebase Firestore, and Koin, it includes full-stack integration — a Ktor-based backend API, realtime transaction history and testing (unit, BDD, UI, and load tests).

It includes:
- A **Ktor backend API**
- A **shared Kotlin Multiplatform logic module**
- A **Jetpack Compose Android UI**
- Comprehensive testing **(Unit, BDD, UI, and API Performance (JMeter))**

---

# Project Architecture

The application follows a Clean Architecture MVI (Model-View-Intent) pattern to ensure a scalable, testable and predicatable design.MVI enforces a unidirectional data flow where user actions are represented as intents, the state is updated through stateFlow, and the UI simply reacts to changes.

The application makes use of **Koin** Dependency Injection. All core business logic is in the `shared` module.

At a high level, the system is divided into three primary layers: Presentation, Domain, and Data — each with distinct responsibilities.

#### Presentation Layer (UI)
- Implemented with Jetpack Compose, responsible for rendering the UI and handling user interactions.
- The UI is stateless — it simply observes changes from the ViewModel and displays them reactively.
- User actions (typing, button clicks, etc.) are expressed as Intents, which are passed down to the ViewModel.

**Example: PaymentIntent.SendPayment, PaymentIntent.ChangeAmount, etc.**

#### Domain Layer (ViewModel & Intents)
- Contains the business logic shared across platforms — implemented in the shared KMP module.
- The ViewModel mediates between UI and data, reacting to user Intents, performing validation, and updating immutable State objects.
- Each screen’s state is defined as a `"UiState"` data class that represents the current UI snapshot(loading, error, etc).
- The MVI pattern ensures:
  - Model (State): Single source of truth for what the UI displays.
  - View: Observes state updates and renders them.
  - Intent: Represents user actions that trigger logic or data changes.

This predictable one-way data flow makes behavior consistent and easier to debug or test.

#### Data Layer (Repository and API)
- The Repository abstracts all data operations and communicates with:
- The Ktor Client for making API calls to the backend /payments endpoint.
- Firebase Firestore for storing and fetching transaction data in real time.
- Data is validated, processed, and transformed here before being passed back to the domain layer.
- On the backend side, a Ktor Server handles incoming requests, validates them, and returns appropriate responses.

### Data Flow
Here’s how data moves through the app:
- The user enters payment details and taps Send Payment.
- The UI layer sends a `SendPayment` intent to the `PaymentsViewModel`.
- The ViewModel validates inputs using `PaymentValidator` and calls the repository.
- **The Repository:**
  - Makes a `POST` request to the backend /payments endpoint via `Ktor`.
  - On success, saves the payment in `Firebase Firestore`.
- The ViewModel updates the UI state (Payment sent! or an error message).
- The UI observes the new state and automatically updates.
- The `TransactionHistoryViewModel` listens to `Firestore` changes and reflects new transactions in real time.

# Running the App & tests
#### 1. Prerequisites
Before running the project, make sure you have the following installed:
- Android Studio Koala+ (or newer)
- JDK 17+
- Gradle 8.5+
- Apache JMeter (for performance testing)

**PLEASE NOTE THAT THE `Ktor` SERVER IS HOSTED LOCALLY, SO THIS WILL HAVE TO BE RUN AND TESTED IN AN EMULATOR**
#### 2. Project Setup
- Clone the repository
 ```bash
  git clone https://github.com/PrathamPatel/Cashi.git
```
- Sync the gradle project
- Connect an Android Emulator (API 33+)

#### 3. Start the Backend Server (Required!)
**Important: The app communicates with the backend API built using Ktor Server.
You must start the server before running the app or any tests.**

- In the **project root** folder, run
 ```bash
  ./gradlew :server:run
```
The server will start on:
 ```bash
  http://localhost:8080
```
You will this in the terminal log:
`Responding at http://127.0.0.1:8080`

#### 4. Run the Android App
- Select the **composeApp** module from the configurations dropdown
- Choose your Emulator
- Click on **Run** or the green "play" button
- The app will launch in the emulator, allow you to:
  - Enter payment details and send a payment
  - View transaction history with real-time updates from Firestore

#### 5. Run the Tests
**Unit tests:**
To test business logic and ViewModels (mocked repositories, validation logic, etc.):
- Ensure `ktor` server is running (see above number 3)
- From the **project root** folder, run:
 ```bash
 ./gradlew :shared:testDebugUnitTest
```
You will see something like this in the terminal:
```
BUILD SUCCESSFUL in 2s
16 actionable tasks: 5 executed, 11 from cache
Configuration cache entry stored. 
```

**BDD Tests (Spek Framework):** Behavior-driven tests that validate the shared KMP logic and payment flow:
- Ensure `ktor` server is running (see above number 3)
- From the **project root** folder, run:
 ```bash
 ./gradlew :shared:test
```
- Located under `shared/src/commonTest/kotlin/com/cashi/technical/bdd/`
- Written using Spek 2 DSL.
- Includes both success and failure scenarios for payment creation and validation.
- Should all tests pass you will see something like this in the terminal:
```
BUILD SUCCESSFUL in 1s
31 actionable tasks: 1 executed, 30 up-to-date
Configuration cache entry reused.

PaymentFlowSpec > Payment creation and transaction history > when a valid payment is made > com.cashi.technical.bdd.PaymentFlowSpec.should process successfully and show success message PASSED
PaymentFlowSpec > Payment creation and transaction history > when a valid payment is made > com.cashi.technical.bdd.PaymentFlowSpec.should show the updated transaction history PASSED
PaymentFlowSpec > Payment creation and transaction history > when the payment fails due to invalid email > com.cashi.technical.bdd.PaymentFlowSpec.should not process and return validation error PASSED
PaymentFlowSpec > Payment creation and transaction history > when there is a network failure > com.cashi.technical.bdd.PaymentFlowSpec.should show an error message in the state PASSED
```
**UI tests:** To automate UI flow for sending a payment and navigating to transaction history:
- Ensure `ktor` server is running (see above number 3)
- Ensure you have an Emulator running as well
- From the **project root** folder, run:
 ```bash
 ./gradlew :composeApp:connectedDebugAndroidTest
```
- This test uses `Jetpack Compose UI Test Framework`
- Navigation testing(simulates full flow : Payment -> Transaction History)
- You will notice your Emulator will run the app, fill in the details and navigate

**If you see timeout errors, verify your server is running.**

**Performance Tests (JMeter):** To simulate multiple users hitting the /payments endpoint:
- Ensure `ktor` server is running (see above number 3)
- From the **project root** folder, run:
 ```bash
 jmeter -n -t server/src/test/kotlin/com/cashi/technical/jmeter/CashiPaymentJmeterTest.jmx -l results.jtl -e -o report/ -f
```
- This simulates 5 concurrent users sending payments and measures:
  - Average repsonse time
  - Throughput
  - Success/failure rate
- Should all tests pass you will see something like this in the terminal:
```
Creating summariser <summary>
Created the tree successfully using server/src/test/kotlin/com/cashi/technical/jmeter/CashiPaymentJmeterTest.jmx
Starting standalone test @ 2025 Oct 26 15:23:53 SAST (1761485033852)
Waiting for possible Shutdown/StopTestNow/HeapDump/ThreadDump message on port 4445
summary =     50 in 00:00:04 =   12.3/s Avg:     3 Min:     1 Max:    31 Err:     0 (0.00%)
Tidying up ...    @ 2025 Oct 26 15:23:58 SAST (1761485038001)
... end of run
```
- To view the report, in the **project root** folder, you will find another folder called `report`:
 ```
 report/index.html
```
# Remarks
This project demonstrates the strength of **KMP** - a modern approach that enables sharing core business logic, networking and data models across various platforms (Android, iOS, Desktop, etc) while keeping each platform's UI native and optimized. The shared module (`shared/`) contains domain logic, repositories, and models written entirely in Kotlin.

#### Advantages:
- Code reusability : core layers like networking and business logic can be reused across all platforms
- Consistency : shared logic ensures identical behaviour across devices
- Scalability : new plaforms, such as iOS or desktop, can be added without rewriting the entire core logic
- Maintainability : A single source of truth for models and domain rules reduces future maintenance overhead

However, as great as KMP is, there are also some limitations or trade-offs to consider:

#### Limitations:
- Third-party libraries: not all 3rd party libraries are multiplatform ready, which can require custom wrappers or expect/actual implementations.
- Build complexity : gradle setup across multiple targets can be intricate and CI/CD pipelines require extra configuration
- Tooling & Debugging: Tooling, especially on iOS (Kotlin Native), is improving but remains less smooth than purely native development.

With careful modularisation and evolving tooling, Kotlin Multiplatform continues to be one of the most practical ways to achieve true cross-platform consistency with native performance.

# Demo Video

Attached is quick demo video of the app running in an Android Emulator. This shows the following:
- Validation messages for incorrect email and amount
- Sending a payment and viewing the success message
- Viewing transaction history and live update (manually added an entry on Firestore to simulate this)

https://github.com/user-attachments/assets/24d98a08-9ec5-4ea5-a572-f74ab112da6d

