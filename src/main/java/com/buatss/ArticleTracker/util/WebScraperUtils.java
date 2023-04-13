package com.buatss.ArticleTracker.util;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.Random;

@UtilityClass
public class WebScraperUtils {
    private final Random random = new Random();
    private final long MIN_WAIT_TIME = 1000L;
    private final long MAX_WAIT_TIME = 3000L;
    private final int MIN_SCROLLS = 10;
    private final int MAX_SCROLLS = 20;

    public static void randomlyScrollPage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < random.nextInt(MIN_SCROLLS, MAX_SCROLLS); i++) {
            js.executeScript("window.scrollBy(0, 1000)");
            waitRandomMilis();
        }
    }

    public static void waitRandomMilis() {
        try {
            Thread.sleep(random.nextLong(MIN_WAIT_TIME, MAX_WAIT_TIME));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
