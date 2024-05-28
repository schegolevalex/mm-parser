package com.schegolevalex.mm.mmparser.parser;

import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Seller;
import com.schegolevalex.mm.mmparser.service.OfferService;
import com.schegolevalex.mm.mmparser.service.SellerService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class Parser {
    private final ChromeOptions options = new ChromeOptions();
    private final ProxyService proxyService;
    private final SellerService sellerService;
    private WebDriver driver;

    public List<Offer> parseProduct(Product product) {
        WebDriverWait wait10sec = new WebDriverWait(driver, Duration.ofSeconds(10));
        proxyService.setProxy(options);
        driver.get(product.getUrl() + "#?details_block=prices");

        String productTitle = wait10sec
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("pdp-header__title_only-title")))
                .getText();
        product.setTitle(productTitle);

        List<WebElement> webElements = waitForElementsIsVisible(wait10sec, By.cssSelector("div[itemtype=\"http://schema.org/Offer\"]"));

        return webElements.stream().map(webElement -> parseOffer(webElement, product)).toList();
    }

    private Offer parseOffer(WebElement webElement, Product product) {
        String sellerName = webElement.findElement(By.className("pdp-merchant-rating-block__merchant-name")).getText();
        Double rating = Double.valueOf(webElement.findElement(By.className("pdp-merchant-rating-block__rating")).findElement(By.tagName("span")).getText());

        Optional<Seller> maybeSeller = sellerService.findByNameAndRating(sellerName, rating);
        Seller seller;

        if (maybeSeller.isPresent()) {
            seller = maybeSeller.get();
            log.trace("Найден существующий продавец: {}", seller);
        } else {
            seller = new Seller();
            seller.setName(sellerName);
            seller.setRating(rating);
            log.trace("Создан новый продавец: {}", seller);
        }
        seller.addProduct(product); // **************************************

        Offer offer = new Offer();
        offer.setSeller(seller);

        try {
            String tempBonusPercent = webElement.findElement(By.className("bonus-percent")).getText();
            String bonusPercent = tempBonusPercent
                    .substring(0, tempBonusPercent.length() - 1)
                    .replaceAll(" ", "");
            offer.setBonusPercent(Integer.valueOf(bonusPercent));

            String bonus = webElement.findElement(By.className("bonus-amount")).getText().replaceAll(" ", "");
            offer.setBonus(Integer.valueOf(bonus));
        } catch (NoSuchElementException e) {
            offer.setBonusPercent(0);
            offer.setBonus(0);
            log.error("Элементы с информацией о бонусах не найден");
        } catch (NumberFormatException e) {
            log.error("Не удалось преобразовать информацию в число: ", e);
        }

        String tempPrice = webElement.findElement(By.className("product-offer-price__amount")).getText();
        String price = tempPrice.substring(0, tempPrice.length() - 2).replaceAll(" ", "");
        offer.setPrice(Integer.valueOf(price));

        return offer;
    }

    private List<WebElement> waitForElementsIsVisible(WebDriverWait wait, By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (NoSuchElementException | TimeoutException e) {
            return List.of();
        }
    }

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
        driver.get(baseUrl);
    }

    @PreDestroy
    private void closeDriver() {
        driver.quit();
    }
}
