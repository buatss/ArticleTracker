package com.buatss.ArticleTracker.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfig {
    @Bean
    public WebDriver webDriver() {
        System.setProperty("webdriver.gecko.driver", System.getenv("geckodriver"));

        FirefoxOptions options = new FirefoxOptions();

        options.addPreference("devtools.console.stdout.content", false);
        options.addPreference("devtools.console.stderr.content", false);

        return new FirefoxDriver(options);
    }
}
