package com.buatss.ArticleTracker.util;

import com.buatss.ArticleTracker.model.MediaSite;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;

import java.util.Random;

@UtilityClass
@Slf4j
public final class WebScraperUtils {
    private static final Random random = new Random();
    private static final long MIN_WAIT_TIME = 1000L;
    private static final long MAX_WAIT_TIME = 3000L;
    private static final int MIN_SCROLLS = 10;
    private static final int MAX_SCROLLS = 20;

    public static void randomlyScrollDownWholePage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < random.nextInt(MAX_SCROLLS - MIN_SCROLLS) + MIN_SCROLLS; i++) {
            js.executeScript("window.scrollBy(0, 1000)");
            waitRandomMilis();
        }
    }

    public static void randomlyScrollDown(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(1000, 3000)");
    }

    public static void waitRandomMilis() {
        long randomWaitTime = MIN_WAIT_TIME + (long) (Math.random() * (MAX_WAIT_TIME - MIN_WAIT_TIME));
        try {
            Thread.sleep(randomWaitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public static void waitMs(Long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public static void acceptCookies(String xpath, WebDriver driver, MediaSite mediaSite) {
        WebScraperUtils.waitRandomMilis();
        try {
            WebElement button = driver.findElement(By.xpath(xpath));
            button.click();
        } catch (NoSuchElementException e) {
            log.warn("[" + mediaSite.getName() + "] Couldn't find button to accept cookies.");
            log.warn("[" + mediaSite.getName() + "] " + e.getLocalizedMessage());
        }
    }
}
