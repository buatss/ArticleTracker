package com.buatss.ArticleTracker.config;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
public class WebDriverConfig {
    @Bean
    @Profile(value ={"dev","prod"})
    public WebDriver webDriver() {
        System.setProperty("webdriver.gecko.driver", System.getenv("geckodriver"));

        FirefoxOptions options = new FirefoxOptions();

        //disable graphical interface
        options.setHeadless(true);

        //reduce logs output from geckodriver
        options.addPreference("devtools.console.stdout.content", false);
        options.addPreference("devtools.console.stderr.content", false);

        return new FirefoxDriver(options);
    }

    @Bean
    @Profile("default")
    public WebDriver webDriverNoGecko() {
        FirefoxOptions options = new FirefoxOptions();
        return new FirefoxDriver(options);
    }
}
