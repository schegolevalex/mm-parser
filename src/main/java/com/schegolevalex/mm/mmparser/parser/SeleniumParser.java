package com.schegolevalex.mm.mmparser.parser;

import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Seller;
import com.schegolevalex.mm.mmparser.service.ProxyService;
import com.schegolevalex.mm.mmparser.service.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Primary
//@Service
@Transactional
@Slf4j
public class SeleniumParser extends Parser {
    private final SellerService sellerService;

    protected SeleniumParser(ProxyService proxyService, SellerService sellerService) {
        super(proxyService);
        this.sellerService = sellerService;
    }

    @Override
    public List<Offer> parseProduct(Product product) {
        openUrl(product.getUrl() + "#?details_block=prices", 3);

        WebDriverWait wait10sec = new WebDriverWait(driver, Duration.ofSeconds(10));
        if (product.getTitle() == null) {
            String productTitle = wait10sec
                    .until(ExpectedConditions.visibilityOfElementLocated(By.className("pdp-header__title_only-title")))
                    .getText();
            product.setTitle(productTitle);
            log.info("Товару установлено название: {}", productTitle);
        }

        List<WebElement> webElements = waitForElementsIsVisible(wait10sec, By.cssSelector("div[itemtype=\"http://schema.org/Offer\"]"));

        return webElements.stream().map(webElement -> parseOffer(webElement, product)).toList();
    }

    private Offer parseOffer(WebElement webElement, Product product) {
        String sellerName = webElement.findElement(By.className("pdp-merchant-rating-block__merchant-name")).getText();
        Double rating = Double.valueOf(webElement.findElement(By.className("pdp-merchant-rating-block__rating")).findElement(By.tagName("span")).getText());

        WebElement toBeClicked = webElement.findElement(By.className("pdp-merchant-rating-block__merchant-name-with-rating"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", toBeClicked);
        String merchantInfoBlock = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("pdp-merchant-legal-info-block__text")))
                .getText();

        Pattern ogrnPattern = Pattern.compile("ОГРН:\\s(\\d{13}|\\d{15})");
        Pattern emailPattern = Pattern.compile("E-mail:\\s[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}");
        Matcher ogrnMatcher = ogrnPattern.matcher(merchantInfoBlock);
        Matcher emailMatcher = emailPattern.matcher(merchantInfoBlock);

        String ogrn = ogrnMatcher.find() ? ogrnMatcher.group().substring(6) : null;
        String email = emailMatcher.find() ? emailMatcher.group().substring(8) : null;

        Optional<Seller> maybeSeller = sellerService.findByNameAndRatingAndOgrn(sellerName, rating, ogrn);
        Seller seller;
        if (maybeSeller.isPresent()) {
            seller = maybeSeller.get();
            log.trace("Найден существующий продавец: {}", seller);
        } else {
            seller = new Seller();
            seller.setName(sellerName);
            seller.setRating(rating);
            seller.setOgrn(ogrn);
            seller.setEmail(email);
            log.trace("Создан новый продавец: {}", seller);
        }
        seller.addProduct(product);

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
            log.trace("Элементы с информацией о бонусах не найден");
        } catch (NumberFormatException e) {
            log.trace("Не удалось преобразовать информацию в число: ", e);
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

}