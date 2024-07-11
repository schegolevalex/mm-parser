package com.schegolevalex.mm.mmparser.parser;

import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public abstract class Parser {
    protected final ChromeOptions options = new ChromeOptions();
    protected final ProxyService proxyService;
    protected WebDriver driver;
    @Value("${mm.baseUrl}")
    private String baseUrl;

    protected Parser(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    public abstract List<Offer> parseProduct(Product product);

    @PostConstruct
    private void prepareOptions() {
        options.addArguments("user-agent=Mozilla/5.0 (X11; Ubuntu; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.6167.139 Safari/537.36");
        options.addArguments("accept-language=ru-Ru");
        options.addArguments("--disable-blink-features");
        options.addArguments("--disable-blink-features=AutomationControlled");

        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--headless=new");
    }

    @PostConstruct
    private void openBaseUrl() {
        openUrl(baseUrl);
    }

    @PreDestroy
    private void closeDriver() {
        driver.quit();
    }

    protected void openUrl(String url) {
        int maxAttempts = 3;
        int currentAttempt = 0;
        boolean success = false;

        if (driver == null)
            driver = new ChromeDriver(options);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        while (currentAttempt < maxAttempts && !success) {
            try {
                log.info("Попытка №{} открыть страницу: {}", currentAttempt + 1, url);
                proxyService.setProxy(options);
                driver.get(url);
                driver.manage().window().maximize();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("html")));
                success = true;
                log.info("Открыли страницу: {}", url);
            } catch (Exception e) {
                log.warn("Попытка №{} открыть страницу не удалась: {}", currentAttempt + 1, url);
                currentAttempt++;
            }
        }

        if (!success) {
            log.error("Не удалось открыть страницу: {}", url);
        }
    }
}
