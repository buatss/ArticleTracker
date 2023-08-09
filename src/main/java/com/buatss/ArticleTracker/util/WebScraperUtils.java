package com.buatss.ArticleTracker.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.Random;

public final class WebScraperUtils {
    private static final Random random = new Random();
    private static final long MIN_WAIT_TIME = 1000L;
    private static final long MAX_WAIT_TIME = 3000L;
    private static final int MIN_SCROLLS = 10;
    private static final int MAX_SCROLLS = 20;

    private WebScraperUtils() {
        // Private constructor to prevent instantiation
    }

    public static void randomlyScrollPage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < random.nextInt(MAX_SCROLLS - MIN_SCROLLS) + MIN_SCROLLS; i++) {
            js.executeScript("window.scrollBy(0, 1000)");
            waitRandomMilis();
        }
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
}
