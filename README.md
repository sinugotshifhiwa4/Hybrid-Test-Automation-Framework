
---

# Hybrid-Test-Automation-Framework

A comprehensive test automation framework that combines **Selenium WebDriver for UI testing** and **REST Assured for API testing**, featuring robust **configuration management**, **centralized logging**, and **utility functions** for enhanced efficiency.

## 📁 Project Structure

```
src/
├── main/
│   └── java/
│       └── com.hybridframework/
│           ├── config/                # Configuration management (dotenv & properties)
│           ├── crypto/                 # Encryption & decryption utilities
│           ├── drivers/                # WebDriver management
│           │   ├── BrowserFactory
│           │   ├── BrowserOptionsUtils
│           │   ├── DriverFactory
│           │   └── SeleniumGridFactory
│           ├── pages.base/             # Common web actions
│           │   └── BasePage
│           ├── testDataStorage/        # Runtime data storage
│           │   ├── TestContextIds
│           │   └── TestContextStore
│           └── utils/                  # Helper utilities
│               ├── dynamicWaits/        # Wait utilities
│               │   ├── ExplicitWaitUtils
│               │   ├── FluentWaitUtils
│               │   └── ImplicitWaitUtils
│               ├── excelUtils/          # Excel file processing
│               ├── jacksonUtils/        # JSON processing
│               │   ├── JsonConverter
│               │   └── JsonReader
│               ├── logging/             # Logging & error handling
│               │   ├── ErrorHandler
│               │   └── LoggerUtils
│               ├── Base64Utils          # Encoding & secret key management
│               └── FileUtils            # Environment setup
├── resources/
│   └── configFiles/                     # Configuration files
│       ├── config-uat.properties
│       ├── global-config.properties
│       └── log4j2.xml
└── test/
    └── java/
        └── com.hybridframework/
            ├── api/                     # API testing (RestAssured)
            │   ├── core/                 # REST client setup
            │   ├── endpoints/            # API routes
            │   └── payload/              # API request payloads
            ├── dataProviders/           # Test data providers
            ├── tests/
            │   ├── base/                 # Test setup & utilities
            │   │   ├── BaseUtils
            │   │   └── TestBase
            │   └── unit/                 # Unit tests for framework components
            │       ├── drivers/
            │       ├── excel/
            │       └── json/
            └── utils/
                └── TestRetryAnalyzer     # Retry logic for flaky tests
```

## 🚀 Key Features

### 🔹 **Driver & Selenium Grid Management**
- Flexible **Browser Factory** for launching different browsers
- **Customizable BrowserOptions** for handling browser configurations
- **Selenium Grid Support** for parallel & remote execution

### 🔹 **Dynamic Waits & Synchronization**
- **ExplicitWaitUtils** for conditional waiting
- **FluentWaitUtils** for advanced polling strategies
- **ImplicitWaitUtils** for default wait configuration

### 🔹 **Data Management & Configuration**
- **TestContextStore**: Thread-safe test data storage
- **JSON Handling**: Uses **Jackson** to parse and manipulate API payloads
- **Excel Data Handling**: Reads test data from Excel files
- **Base64 Encoding**: Utility for encoding/decoding secret keys

### 🔹 **Configuration Management**
- **Environment-based properties**: Supports dotenv & properties file configuration
- **Config files**: `global-config.properties`, `config-uat.properties`
- **Efficient data loading**: Uses maps for faster lookup

### 🔹 **Logging & Error Handling**
- **Centralized ErrorHandler** to manage exceptions
- **Log4j2 implementation** for structured logging
- **Custom LoggerUtils** for enhanced logging management

## 🛠️ Technical Components

### Base Classes
- **`TestBase`**: Core test class handling **setup & teardown**
- **`BasePage`**: Provides reusable UI interaction methods (click, sendKeys, etc.)
- **`BaseUtils`**: Handles **configuration loading** and **test data initialization**

### Driver Management
- **`BrowserFactory`**: Handles browser instance creation
- **`DriverFactory`**: Manages **WebDriver lifecycle**
- **`BrowserOptionsUtils`**: Customizes browser configurations
- **`SeleniumGridFactory`**: Supports **remote execution**

### API Testing
- **`core/RestClient`**: Manages API requests
- **`endpoints`**: Stores API routes & request endpoints
- **`payload`**: Stores JSON request bodies

### Test Data Management
- **`TestContextStore`**: Thread-safe data storage for test execution
- **`TestContextIds`**: Ensures unique test execution identifiers
- **`JsonConverter`**: Converts API response data into structured objects
- **`JsonReader`**: Reads JSON test data files

### Wait Utilities
- **`ExplicitWaitUtils`**: Handles **conditional waiting**
- **`FluentWaitUtils`**: Provides **advanced polling strategies**
- **`ImplicitWaitUtils`**: Manages **default waits**

## 🔧 Setup & Configuration

1. **Clone the repository:**
   ```bash
   git clone [repository-url]
   ```  

2. **Configure properties files** in `src/main/resources/configFiles/`:
    - `global-config.properties`: **Global framework settings**
    - `config-uat.properties`: **UAT-specific configuration**

3. **Log4j Configuration:**  
   Modify `log4j2.xml` as needed for **custom logging levels**.

## 📋 Prerequisites

- **Java JDK 8+**
- **Maven**
- **Web Browsers (Chrome/Firefox)**
- **Selenium WebDriver**
- **TestNG**

## ▶️ Running Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=TestClassName

# Run with specific suite
mvn clean test -DsuiteXmlFile=testng.xml
```  

## 📌 Configuration Loading Example

```java
public static void loadConfigurations(){
    try {
        // Load Global Config
        PropertiesConfigManager.loadConfiguration(
            PropertiesFileAlias.GLOBAL.getConfigurationAlias(),
            PropertiesFilePath.GLOBAL.getPropertiesFilePath()
        );

        // Load UAT Config
        PropertiesConfigManager.loadConfiguration(
            PropertiesFileAlias.UAT.getConfigurationAlias(),
            PropertiesFilePath.UAT.getPropertiesFilePath()
        );
    } catch (Exception error) {
        ErrorHandler.logError(error, "loadConfigurations", "Failed to load configurations");
        throw error;
    }
}
```  

## 🤝 Contributing

1. **Fork the repository**
2. **Create a feature branch**:
   ```bash
   git checkout -b feature/YourFeature
   ```  
3. **Commit your changes**:
   ```bash
   git commit -m 'Add YourFeature'
   ```  
4. **Push to the branch**:
   ```bash
   git push origin feature/YourFeature
   ```  
5. **Create a Pull Request**

---