package com.schegolevalex.mm.mmparser.parser;

import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public abstract class Parser {
    protected final ChromeOptions options = new ChromeOptions();
    protected final ProxyService proxyService;
    protected WebDriver driver;

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
        proxyService.setProxy(options);
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        String baseUrl = "https://megamarket.ru/";
        log.info("Открываем главную страницу: {}", baseUrl);
        driver.get(baseUrl);
    }

    @PreDestroy
    private void closeDriver() {
        driver.quit();
    }

    protected void openProductUrl(String url) {
        proxyService.setProxy(options);
        log.info("Открываем страницу товара: {}", url);
        driver.get(url + "#?details_block=prices");
    }
}
