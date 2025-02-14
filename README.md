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

## ▶️ Running  🔑 Crypto & Sanity Tests

### Run Crypto Test for Secret Key Generation & Credential Encryption:
```bash
mvn clean test -Denv=crypto -DskipBrowserSetup=true
```
*Ensure `skipBrowserSetup=true` to prevent WebDriver initialization.*

### Run Sanity Tests:
```bash
mvn clean test -Denv=uat
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

## 🔧 Maven Profiles Setup

The framework includes **Maven Profiles** for better test execution control:

```xml
<profiles>
    <profile>
        <id>sanity</id>
        <activation>
            <property>
                <name>env</name>
                <value>uat</value>
            </property>
        </activation>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <groups>sanity</groups>
                        <suiteXmlFiles>
                            <suiteXmlFile>sanity-runner.xml</suiteXmlFile>
                        </suiteXmlFiles>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>

    <profile>
        <id>encryption</id>
        <activation>
            <property>
                <name>env</name>
                <value>crypto</value>
            </property>
        </activation>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <groups>encryption</groups>
                        <suiteXmlFiles>
                            <suiteXmlFile>encryption-runner.xml</suiteXmlFile>
                        </suiteXmlFiles>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```
