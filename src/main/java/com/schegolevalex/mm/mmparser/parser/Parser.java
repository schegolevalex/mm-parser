package com.schegolevalex.mm.mmparser.parser;

import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProxyService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.List;

@Slf4j
public abstract class Parser {
    private final ChromeOptions options = new ChromeOptions();
    private final ProxyService proxyService;
    WebDriver driver;
    @Value("${mm.baseUrl}")
    private String baseUrl;
    private boolean baseUrlOpened = false;
    @Value("${mm.parser.max-attempts-to-open-url}")
    private int maxAttempts;

    @Autowired
    public Parser(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    public abstract List<Offer> parseProduct(Product product);

    @PostConstruct
    private void prepareOptions() {
        options.addArguments("user-agent=Mozilla/5.0 (X11; Ubuntu; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.6167.139 Safari/537.36");
        options.addArguments("accept-language=ru-Ru");
        options.addArguments("--disable-blink-features");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-gpu");

        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-setuid-sandbox");

        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--headless=new");
    }

    @PreDestroy
    private void quitWebDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    void openUrl(String url) {
        int currentAttempt = 0;
        boolean openUrlSuccess = false;

        while (currentAttempt < maxAttempts && !openUrlSuccess) {
            try {
                if (!baseUrlOpened && driver == null) {
                    driver = new ChromeDriver(options);
                    log.info("Создан новый экземпляр WebDriver");
                    proxyService.setRandomProxy(options);
                    log.info("Попытка открыть страницу {}", baseUrl);
                    driver.get(baseUrl);
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("html")));
                    baseUrlOpened = true;
                    log.info("Успешно открыта страница {}", baseUrl);
                }

                log.info("{} попытка открыть страницу {}", currentAttempt + 1, url);
                proxyService.setRandomProxy(options);
                driver.get(url);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("html")));
                openUrlSuccess = true;
                log.info("Успешно открыта страница {} после {} попыток", url, currentAttempt + 1);
            } catch (TimeoutException e) {
                log.warn("Не удалась {} попытка открыть страницу {}: таймаут", currentAttempt + 1, url);
                currentAttempt++;
            } catch (WebDriverException e) {
                log.error("При попытке открыть страницу {} произошла ошибка WebDriver: {}", url, e.getMessage());
                quitWebDriver();
                currentAttempt++;
                baseUrlOpened = false;
            } catch (Exception e) {
                log.error("При попытке открыть страницу {} произошла неизвестная ошибка: {}", url, e.getMessage());
                quitWebDriver();
                currentAttempt++;
                baseUrlOpened = false;
            }
        }
    }
}
