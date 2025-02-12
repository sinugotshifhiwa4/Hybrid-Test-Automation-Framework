package com.hybridframework.pages.base;

import com.hybridframework.drivers.DriverFactory;
import com.hybridframework.utils.Base64Utils;
import com.hybridframework.utils.dynamicWaits.FluentWaitUtils;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hybridframework.utils.dynamicWaits.ExplicitWaitUtils.getWebDriverWait;

public class BasePage {

    private static final Logger logger = LoggerUtils.getLogger(BasePage.class);
    private static final String SCREENSHOT_DIRECTORY = "SCREENSHOT_DIR";
    private final DriverFactory driverFactory = DriverFactory.getInstance();


    public static boolean isElementVisible(WebElement element) {
        try {
            getWebDriverWait().until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (Exception e) {
            logger.warn("Element not visible: {}", element, e);
            return false;
        }
    }

    public static boolean isElementNotVisible(WebElement element) {
        try {
            getWebDriverWait().until(ExpectedConditions.invisibilityOf(element));
            return true;
        } catch (Exception e) {
            logger.warn("Element is still visible: {}", element, e);
            return false;
        }
    }

    public static void waitForElementToBeVisible(WebElement element) {
        try {
            getWebDriverWait().until(ExpectedConditions.visibilityOf(element));
        } catch (Exception error) {
            ErrorHandler.logError(error, "isElementVisible", "Element is not visible within timeout.");
            throw error;
        }
    }

    public static void waitForElementNotToBeVisible(WebElement element) {
        try {
            getWebDriverWait().until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception error) {
            ErrorHandler.logError(error, "isElementNotVisible", "Element is still visible within timeout.");
            throw error;
        }
    }

    public void sendKeys(WebElement element, String value) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(element);
            element.sendKeys(value);
        } catch (Exception error) {
            ErrorHandler.logError(error, "sendKeys", "Failed to send keys");
            throw error;
        }
    }

    public void clickElement(WebElement element) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(element);
            FluentWaitUtils.waitForElementToBeClickable(element);
            element.click();
        } catch (Exception error) {
            ErrorHandler.logError(error, "clickElement", "Failed to click element");
            throw error;
        }
    }

    public void clearElement(WebElement element) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(element);
            element.clear();
        } catch (Exception error) {
            ErrorHandler.logError(error, "clearElement", "Failed to clear element");
            throw error;
        }
    }

    public void selectDropdownElement(WebElement element, String selectMethod, Object value) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(element);
            Select select = new Select(element);

            if (!isElementEnabled(element)) {
                throw new IllegalStateException("Dropdown is disabled or not interactive");
            }

            List<WebElement> options = select.getOptions();
            if (options.isEmpty()) {
                throw new IllegalStateException("Dropdown has no options available");
            }

            String method = selectMethod.trim().toLowerCase();

            switch (method) {
                case "visibletext" -> {
                    String textValue = validateString(value, "visibletext");
                    if (options.stream().noneMatch(opt -> opt.getText().equals(textValue))) {
                        logger.error("Dropdown is disabled or not interactive using visible text method");
                        throw new IllegalArgumentException("Option with text '" + textValue + "' not found. Available options: " +
                                options.stream().map(WebElement::getText).toList());
                    }
                    select.selectByVisibleText(textValue);
                }

                case "value" -> {
                    String stringValue = validateString(value, "value");
                    if (options.stream().noneMatch(opt -> Objects.equals(opt.getDomProperty("value"), stringValue))) {
                        logger.error("Dropdown is disabled or not interactive using value method");
                        throw new IllegalArgumentException("Option with value '" + stringValue + "' not found. Available values: " +
                                options.stream().map(opt -> opt.getDomProperty("value")).toList());
                    }
                    select.selectByValue(stringValue);
                }

                case "index" -> {
                    int index = validateInteger(value);
                    if (index < 0 || index >= options.size()) {
                        logger.error("Dropdown is disabled or not interactive using index method");
                        throw new IllegalArgumentException("Index " + index + " is out of bounds. Available range: 0-" + (options.size() - 1));
                    }
                    select.selectByIndex(index);
                }

                default -> throw new IllegalArgumentException("Invalid select method: " + selectMethod);
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "selectDropdownElement",
                    "Failed to select dropdown element using method: " + selectMethod + " with value: " + value);
            throw error;
        }
    }

    private String validateString(Object value, String method) {
        if (!(value instanceof String strValue)) {
            throw new IllegalArgumentException("Expected a String for '" + method + "' method.");
        }
        return strValue;
    }

    private int validateInteger(Object value) {
        if (!(value instanceof Integer intValue)) {
            throw new IllegalArgumentException("Expected an Integer for 'index' method.");
        }
        return intValue;
    }

    public boolean isElementEnabled(WebElement element) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(element);
            return element.isEnabled() && !Objects.requireNonNull(element.getDomProperty("class")).contains("disabled");
        } catch (Exception e) {
            logger.warn("Element not enabled: {}", element, e);
            return false;
        }
    }

    public boolean isElementSelected(WebElement element) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(element);
            return element.isSelected();
        } catch (Exception e) {
            logger.warn("Failed to check if element is selected: {}", element, e);
            return false;
        }
    }

    public String getElementText(WebElement element) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(element);
            return element.getText().trim(); // Trim whitespace for cleaner output
        } catch (Exception error) {
            ErrorHandler.logError(error, "getElementText", "Failed to get element text");
            throw error;
        }
    }

    public String getNormalizedText(WebElement element) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(element);
            String text = element.getText();
            return text.replaceAll("\\s+", " ").trim(); // Normalize spaces
        } catch (Exception error) {
            ErrorHandler.logError(error, "getNormalizedText", "Failed to get normalized element text");
            throw error;
        }
    }


    public String getInputText(WebElement element) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(element);
            return Objects.requireNonNull(element.getDomProperty("value")).trim();
        } catch (Exception error) {
            ErrorHandler.logError(error, "getInputText", "Failed to get text from input field");
            throw error;
        }
    }

    public List<String> getTextsFromElements(List<WebElement> elements) {
        try {
            return elements.stream()
                    .map(WebElement::getText)
                    .map(String::trim)
                    .filter(text -> !text.isEmpty()) // Exclude empty strings
                    .collect(Collectors.toList());
        } catch (Exception error) {
            ErrorHandler.logError(error, "getTextsFromElements", "Failed to get texts from elements list");
            throw error;
        }
    }

    public String getHiddenElementText(WebElement element) {
        try {
            return Objects.requireNonNull(element.getDomProperty("textContent")).trim();
        } catch (Exception error) {
            ErrorHandler.logError(error, "getHiddenElementText", "Failed to get hidden element text");
            throw error;
        }
    }

    public String getPlaceholderText(WebElement element) {
        try {
            return Objects.requireNonNull(element.getDomProperty("placeholder")).trim();
        } catch (Exception error) {
            ErrorHandler.logError(error, "getPlaceholderText", "Failed to get placeholder text");
            throw error;
        }
    }


    public void waitForModalToBeVisible(WebElement modalElement) {
        try {
            getWebDriverWait().until(ExpectedConditions.visibilityOf(modalElement));
        } catch (Exception error) {
            ErrorHandler.logError(error, "waitForModalToBeVisible", "Modal is not visible within timeout.");
            throw error;
        }
    }

    public void waitForModalToBeHidden(WebElement modalElement) {
        try {
            getWebDriverWait().until(ExpectedConditions.invisibilityOf(modalElement));
        } catch (Exception error) {
            ErrorHandler.logError(error, "waitForModalToBeHidden", "Modal is still visible within timeout.");
            throw error;
        }
    }

    public boolean isModalVisible(WebElement modalElement) {
        try {
            return getWebDriverWait().until(ExpectedConditions.visibilityOf(modalElement)) != null;
        } catch (Exception error) {
            logger.warn("Modal not visible: {}", modalElement, error);
            return false;
        }
    }

    public void closeModal(WebElement closeButton) {
        try {
            FluentWaitUtils.waitForElementToBeClickable(closeButton);
            closeButton.click();
        } catch (Exception error) {
            ErrorHandler.logError(error, "closeModal", "Failed to close modal.");
            throw error;
        }
    }

    public void switchToIframe(WebElement iframeElement) {
        try {
            getWebDriverWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframeElement));
        } catch (Exception error) {
            ErrorHandler.logError(error, "switchToIframe", "Failed to switch to iframe.");
            throw error;
        }
    }

    public void switchToWindow(String windowHandle) {
        try {
            if (windowHandle == null || windowHandle.isEmpty()) {
                ErrorHandler.logError(new IllegalArgumentException("Invalid window handle provided"),
                        "switchToWindow", "Invalid window handle: " + windowHandle);
                return;
            }

            Set<String> windowHandles = driverFactory.getDriver().getWindowHandles();
            if (!windowHandles.contains(windowHandle)) {
                ErrorHandler.logError(new NoSuchWindowException("Window handle not found: " + windowHandle),
                        "switchToWindow", "Window handle not found: " + windowHandle);
                return;
            }

            driverFactory.getDriver().switchTo().window(windowHandle);
            logger.info("Switched to window successfully: {}", windowHandle);

        } catch (NoSuchWindowException error) {
            ErrorHandler.logError(error, "switchToWindow", "No such window exists: " + windowHandle);
            throw error;
        } catch (Exception error) {
            ErrorHandler.logError(error, "switchToWindow", "Failed to switch to window: " + windowHandle);
            throw error;
        }
    }

    public void switchToDefaultContent() {
        try {
            DriverFactory.getInstance().getDriver().switchTo().defaultContent();
        } catch (Exception error) {
            ErrorHandler.logError(error, "switchToDefaultContent", "Failed to switch back to main content.");
            throw error;
        }
    }

    public String captureScreenshot(String screenshotName) {
        try {
            // Generate timestamp and formatted filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s_%s.png", screenshotName, timestamp);

            // Define structured screenshot directory
            String screenshotDir = SCREENSHOT_DIRECTORY + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Path destinationPath = Path.of(screenshotDir, fileName);

            // Take the screenshot
            File screenshot = ((TakesScreenshot) driverFactory.getDriver()).getScreenshotAs(OutputType.FILE);

            return saveScreenshot(destinationPath, screenshot);

        } catch (IOException error) {
            ErrorHandler.logError(error, "captureScreenshot", "Failed to capture screenshot: " + screenshotName);
            throw new RuntimeException("Error capturing screenshot: " + screenshotName, error);
        }
    }

    private String saveScreenshot(Path destinationPath, File screenshot) throws IOException {
        try {
            // Ensure the directory exists before saving the screenshot
            Files.createDirectories(destinationPath.getParent());
            Files.copy(screenshot.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("Screenshot successfully saved at: {}", destinationPath.toAbsolutePath());

            // Convert to Base64 for reporting and return
            return Base64Utils.encodeArray(Files.readAllBytes(destinationPath));
        } catch (IOException ioError) {
            ErrorHandler.logError(ioError, "saveScreenshot",
                    "I/O error occurred while saving screenshot at: " + destinationPath);
            throw new IOException("Failed to save screenshot at: " + destinationPath, ioError);
        } catch (Exception error) {
            ErrorHandler.logError(error, "saveScreenshot",
                    "Unexpected error occurred while processing screenshot: " + screenshot.getName());
            throw new IOException("Unexpected error while processing screenshot: " + screenshot.getName(), error);
        }
    }

    public void uploadFile(WebElement uploadElement, String filePath) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(uploadElement);
            uploadElement.sendKeys(filePath);
        } catch (Exception error) {
            ErrorHandler.logError(error, "uploadFile", "Failed to upload file");
            throw error;
        }
    }

    public void acceptAlert() {
        try {
            getWebDriverWait().until(ExpectedConditions.alertIsPresent()).accept();
        } catch (Exception error) {
            ErrorHandler.logError(error, "acceptAlert", "Failed to accept alert");
            throw error;
        }
    }

    public void dismissAlert() {
        try {
            getWebDriverWait().until(ExpectedConditions.alertIsPresent()).dismiss();
        } catch (Exception error) {
            ErrorHandler.logError(error, "dismissAlert", "Failed to dismiss alert");
            throw error;
        }
    }

    public String getAlertText() {
        try {
            return getWebDriverWait().until(ExpectedConditions.alertIsPresent()).getText();
        } catch (Exception error) {
            ErrorHandler.logError(error, "getAlertText", "Failed to retrieve alert text");
            throw error;
        }
    }

    public void sendKeysToAlert(String text) {
        try {
            getWebDriverWait().until(ExpectedConditions.alertIsPresent()).sendKeys(text);
        } catch (Exception error) {
            ErrorHandler.logError(error, "sendKeysToAlert", "Failed to send keys to alert");
            throw error;
        }
    }

    public void navigateBack() {
        try {
            driverFactory.getDriver().navigate().back();
        } catch (Exception error) {
            ErrorHandler.logError(error, "navigateBack", "Failed to navigate back");
            throw error;
        }
    }

    public void navigateForward() {
        try {
            driverFactory.getDriver().navigate().forward();
        } catch (Exception error) {
            ErrorHandler.logError(error, "navigateForward", "Failed to navigate forward");
            throw error;
        }
    }

    public void refreshPage() {
        try {
            driverFactory.getDriver().navigate().refresh();
        } catch (Exception error) {
            ErrorHandler.logError(error, "refreshPage", "Failed to refresh page");
            throw error;
        }
    }

    public void scrollPageUp() {
        try {
            getJsExecutor().executeScript("window.scrollTo(0, 0)");
        } catch (Exception error) {
            ErrorHandler.logError(error, "scrollPageUp", "Failed to scroll page up");
            throw error;
        }
    }

    public void scrollPageDown() {
        try {
            getJsExecutor().executeScript("window.scrollTo(0, document.body.scrollHeight)");
        } catch (Exception error) {
            ErrorHandler.logError(error, "scrollPageDown", "Failed to scroll page down");
            throw error;
        }
    }

    public void scrollToElement(WebElement element) {
        try {
            getJsExecutor().executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (Exception error) {
            ErrorHandler.logError(error, "scrollToElement", "Failed to scroll to element");
            throw error;
        }
    }

    public void hoverOverElement(WebElement element) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(element);
            getActions().moveToElement(element).perform();
        } catch (Exception error) {
            ErrorHandler.logError(error, "hoverOverElement", "Failed to hover over element");
            throw error;
        }
    }

    public void dragAndDrop(WebElement source, WebElement target) {
        try {
            FluentWaitUtils.waitForElementToBeVisible(source);
            FluentWaitUtils.waitForElementToBeVisible(target);
            getActions().dragAndDrop(source, target).perform();
        } catch (Exception error) {
            ErrorHandler.logError(error, "dragAndDrop", "Failed to drag and drop file");
            throw error;
        }
    }

    private Actions getActions(){
        try {
            WebDriver driver = driverFactory.getDriver();
            return new Actions(driver);
        } catch (Exception error){
            ErrorHandler.logError(error, "getActions", "Failed to get actions");
            throw error;
        }
    }

    private JavascriptExecutor  getJsExecutor(){
        try {
            WebDriver driver = driverFactory.getDriver();
            return (JavascriptExecutor) driver;
        } catch (Exception error){
            ErrorHandler.logError(error, "getJsExecutor", "Failed to get javascript executor");
            throw error;
        }
    }
}
