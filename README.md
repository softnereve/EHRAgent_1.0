# EHRAgent - Epic FHIR Integration Project

EHRAgent is a powerful, standalone microservice designed to bridge the gap between healthcare applications and the **Epic Electronic Health Record (EHR)** system. It uses the modern **FHIR (Fast Healthcare Interoperability Resources)** standard to securely fetch and manage patient data, practitioner details, and clinical observations.

---

## 🚀 What does this project do? (Layman's Terms)

Imagine you have a health app that needs to talk to a hospital's big computer (like Epic). EHRAgent is like a **translator and messenger**. 
- It asks Epic for a doctor's schedule or a patient's medical history.
- It translates the complex medical data into a format that your app can easily understand.
- It also saves some of this information locally in a database (MongoDB) so it's faster to access later.

---

## 🧠 How "Intent" is Used

We use a special **"Intent Bridge"** technology. 
In simple terms, an **"Intent"** is a goal or an action that a user wants to perform (e.g., "Find me a doctor" or "Show my last lab result"). 

Instead of writing complex code for every possible request, we define **Intents** in the code. This allows the system to:
1.  Understand the user's goal.
2.  Map that goal directly to a specific API call.
3.  Handle the authentication and data fetching automatically.

Look for `@IntentDefinition` in the service files to see how these are mapped!

---

## 🛠️ Key Functionalities

### 1. Patient Management
- **Search & Fetch**: Find patient details by their unique Epic ID or even by their registered email.
- **Registration**: Create new patient accounts directly in the Epic sandbox.

### 2. Practitioner (Doctor) Discovery
- **Search**: Look for doctors by name or their medical specialty (e.g., Cardiology, Pediatrics).
- **Details**: Get full summaries, including their roles, qualifications, and clinic details.

### 3. Medical Observations (Test Results)
- **FHIR Labs**: Fetch laboratory results directly from Epic's R4 FHIR server.
- **Local Storage**: Save custom notes or observations in our local database for quick reference.

---

## 📂 Project Structure

- **`controller/`**: The "Doors" of the app. These are the endpoints your app will call.
- **`service/`**: The "Brain". This is where the translation and logic happen.
- **`model/`**: The "Boxes". This defines how the data is shaped.
- **`repo/`**: The "Files". This is how we talk to our local database (MongoDB).
- **`utils/`**: The "Translators". Simple tools to convert data formats.

---

## 🔐 How to Run & Access

### Prerequisite
- **Java 17** installed.
- **Maven** installed.
- **Docker** installed.
- **MongoDB** running (Connection details are in `application.properties`).

### Running the App

#### With Maven
1.  Open your terminal in the `EHRAgent` directory.
2.  Run the command:
    ```bash
    mvn clean spring-boot:run
    ```

#### With Docker
1.  Build the Docker image:
    ```bash
    docker build -t ehr-agent:latest .
    ```
2.  Run the Docker container:
    ```bash
    docker run -p 7979:7979 ehr-agent:latest
    ```

### 📖 Swagger Documentation (Testing via Browser)
We provide a visual interface to test all APIs.
- **Link**: [http://localhost:7979/v1.0.0/apis/fhir/swagger-ui/index.html](http://localhost:7979/v1.0.0/apis/fhir/swagger-ui/index.html)
- **Login Credentials**:
  - **Username**: `patient-service`
  - **Password**: `$$P@t!ent$$`

---

## 🧪 Example API Calls (CURL)

### 1. Discover Sandbox IDs (Start here!)
Find some test IDs to use in other APIs.
```bash
curl -X GET "http://localhost:8282/v1.0.0/apis/epic/sandbox/discover" -u admin:admin123
```

### 2. Search for a Practitioner by Name
```bash
curl -X GET "http://localhost:8282/v1.0.0/apis/epic/practitioner/search?name=Albert" -u admin:admin123
```

### 3. Get Patient Observations by Email
```bash
curl -X GET "http://localhost:8282/v1.0.0/apis/epic/observation/by-email/knixontestemail@epic.com" -u admin:admin123
```

### 4. Fetch Doctor Info by Epic ID
```bash
curl -X GET "http://localhost:8282/v1.0.0/apis/epic/practitioner/eb55Xa5E7EWWnV5eRuWsfKQ3" -u admin:admin123
```

---

## 🛡️ Support & Configuration
All configuration like Client IDs, Secret Keys (JWK), and Database URLs are located in:
`src/main/resources/application.properties`

**Note**: For production, never share your `private-jwk`!
