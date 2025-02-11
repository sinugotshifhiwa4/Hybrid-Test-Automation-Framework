package com.hybridframework.pages.base;

import com.hybridframework.drivers.DriverFactory;
import com.hybridframework.utils.dynamicWaits.FluentWaitUtils;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hybridframework.utils.dynamicWaits.ExplicitWaitUtils.getWebDriverWait;

public class BasePage {

    private static final Logger logger = LoggerUtils.getLogger(BasePage.class);

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

            String method = selectMethod.trim().toLowerCase();

            switch (method) {
                case "visibletext":
                    if (!(value instanceof String)) {
                        logger.error("Expected a String for 'visibletext' method.");
                        throw new IllegalArgumentException("Expected a String for 'visibletext' method.");
                    }
                    select.selectByVisibleText((String) value);
                    break;

                case "value":
                    if (!(value instanceof String)) {
                        logger.info("Expected a String for 'value' method.");
                        throw new IllegalArgumentException("Expected a String for 'value' method.");
                    }
                    select.selectByValue((String) value);
                    break;

                case "index":
                    if (!(value instanceof Integer)) {
                        logger.error("Expected a Integer for 'index' method.");
                        throw new IllegalArgumentException("Expected an Integer for 'index' method.");
                    }
                    select.selectByIndex((Integer) value);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid select method: " + selectMethod);
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "selectDropdownElement",
                    "Failed to select dropdown element using method: " + selectMethod + " with value: " + value);
            throw error;
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

    public void switchToDefaultContent() {
        try {
            DriverFactory.getInstance().getDriver().switchTo().defaultContent();
        } catch (Exception error) {
            ErrorHandler.logError(error, "switchToDefaultContent", "Failed to switch back to main content.");
            throw error;
        }
    }

    public void captureScreenshot(String screenshotName) {
        try {
            // Take screenshot
            File screenshot = ((TakesScreenshot) DriverFactory.getInstance().getDriver()).getScreenshotAs(OutputType.FILE);

            // Convert to Base64 (optional, useful for reports)
            byte[] fileScreenshot = Files.readAllBytes(screenshot.toPath());
            String base64EncodedScreenshot = Base64.getEncoder().encodeToString(fileScreenshot);

            // Define screenshot storage path
            String screenshotDir = "screenshots/";
            Path destinationPath = Path.of(screenshotDir, screenshotName + ".png");

            // Create directory if it doesn't exist
            Files.createDirectories(destinationPath.getParent());

            // Save screenshot to file
            Files.copy(screenshot.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("Screenshot saved: {}", destinationPath.toAbsolutePath());
        } catch (IOException error) {
            ErrorHandler.logError(error, "captureScreenshot", "Failed to capture screenshot: " + screenshotName);
            throw new RuntimeException("Error capturing screenshot: " + screenshotName, error);
        }
    }

    public void navigateBack() {
        try {
            DriverFactory.getInstance().getDriver().navigate().back();
        } catch (Exception error) {
            ErrorHandler.logError(error, "navigateBack", "Failed to navigate back");
            throw error;
        }
    }

    public void navigateForward() {
        try {
            DriverFactory.getInstance().getDriver().navigate().forward();
        } catch (Exception error) {
            ErrorHandler.logError(error, "navigateForward", "Failed to navigate forward");
            throw error;
        }
    }

    public void refreshPage() {
        try {
            DriverFactory.getInstance().getDriver().navigate().refresh();
        } catch (Exception error) {
            ErrorHandler.logError(error, "refreshPage", "Failed to refresh page");
            throw error;
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

    public void scrollPageUp() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getInstance().getDriver();
            js.executeScript("window.scrollTo(0, 0)");
        } catch (Exception error) {
            ErrorHandler.logError(error, "scrollPageUp", "Failed to scroll page up");
            throw error;
        }
    }

    public void scrollPageDown() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getInstance().getDriver();
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        } catch (Exception error) {
            ErrorHandler.logError(error, "scrollPageDown", "Failed to scroll page down");
            throw error;
        }
    }

    public void scrollToElement(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getInstance().getDriver();
            js.executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (Exception error) {
            ErrorHandler.logError(error, "scrollToElement", "Failed to scroll to element");
            throw error;
        }
    }
}
