package com.schegolevalex.mm.mmparser.parser;

import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Seller;
import com.schegolevalex.mm.mmparser.repository.ProductRepository;
import com.schegolevalex.mm.mmparser.service.OfferService;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.SellerService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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
    private final OfferService offerService;
    private final ChromeOptions options = new ChromeOptions();
    private final ProxyService proxyService;
    private final SellerService sellerService;
    private WebDriver driver;

    public List<Offer> parseProduct(Product product) {
        WebDriverWait wait10sec = new WebDriverWait(driver, Duration.ofSeconds(10));
        proxyService.setProxy(options);
        driver.get(product.getUrl() + "#?details_block=prices");

        WebElement productTitle = wait10sec.until(ExpectedConditions.visibilityOfElementLocated(By.className("pdp-header__title_only-title")));
        product.setTitle(productTitle.getText());

        List<WebElement> webElements = waitForElementsIsVisible(wait10sec, By.cssSelector("div[itemtype=\"http://schema.org/Offer\"]"));
        List<Offer> newOffers = webElements.stream().map(this::parseOffer).toList();

        newOffers.forEach(offer -> {
            product.addOffer(offer);
            offer.getSeller().addProduct(product);
        });
        return newOffers.stream().map(offerService::checkPrevious).toList();
    }

    private Offer parseOffer(WebElement webElement) {
        String sellerName = webElement.findElement(By.className("pdp-merchant-rating-block__merchant-name")).getText();
        Seller seller = sellerService.findByName(sellerName).orElseGet(() -> {
            Seller newSeller = new Seller();
            newSeller.setName(sellerName);
            return sellerService.save(newSeller);
        });
        seller.setName(sellerName);

        Offer newOffer = new Offer();
        newOffer.setSeller(seller);

        try {
            String tempBonusPercent = webElement.findElement(By.className("bonus-percent")).getText();
            String bonusPercent = tempBonusPercent
                    .substring(0, tempBonusPercent.length() - 1)
                    .replaceAll(" ", "");
            newOffer.setBonusPercent(Integer.valueOf(bonusPercent));

            String bonus = webElement.findElement(By.className("bonus-amount")).getText().replaceAll(" ", "");
            newOffer.setBonus(Integer.valueOf(bonus));
        } catch (NoSuchElementException e) {
            newOffer.setBonusPercent(0);
            newOffer.setBonus(0);
            log.error("Элементы с информацией о бонусах не найден");
        } catch (NumberFormatException e) {
            log.error("Не удалось преобразовать информацию в число: ", e);
        }

        String tempPrice = webElement.findElement(By.className("product-offer-price__amount")).getText();
        String price = tempPrice.substring(0, tempPrice.length() - 2).replaceAll(" ", "");
        newOffer.setPrice(Integer.valueOf(price));

        return newOffer;
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

//        options.addArguments("--user-data-dir=/home/schegolevalex/.config/google-chrome");
//
//        options.addArguments("--display=:1");
//        options.addArguments("referer=https://www.google.com/search?q=%D1%81%D0%B1%D0%B5%D1%80%D0%BC%D0%B5%D0%B3%D0%B0%D0%BC%D0%B0%D1%80%D0%BA%D0%B5%D1%82&sca_esv=5f32bda464ccee7b&ei=mvrAZc3EDM2nwPAPi4aikAs&oq=%D1%81%D0%B1%D0%B5%D1%80%D0%BC%D0%B5&gs_lp=Egxnd3Mtd2l6LXNlcnAiDNGB0LHQtdGA0LzQtSoCCAAyCBAAGIAEGLADMggQABiABBiwAzIIEAAYgAQYsAMyCBAAGIAEGLADMggQABiABBiwAzIIEAAYgAQYsAMyCBAAGIAEGLADMggQABiABBiwAzIIEAAYgAQYsAMyBxAAGB4YsANI9QtQAFgAcAF4AJABAJgBAKABAKoBALgBA8gBAOIDBBgBIEGIBgGQBgo&sclient=gws-wiz-serp");
//        options.addArguments("--no-sandbox");
//        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
//        options.setExperimentalOption("useAutomationExtension", false);
    }

//    private Optional<WebElement> waitForElementIsClickable(WebDriverWait wait, By locator) {
//        try {
//            return Optional.of(wait.until(ExpectedConditions.visibilityOfElementLocated(locator)));
//        } catch (NoSuchElementException | TimeoutException e) {
//            return Optional.empty();
//        }
//    }
    
//    private void setCookies(WebDriver driver) {
//        driver.manage().addCookie(new Cookie("AEC", "AQTF6Hzov47M_Vr7cbDLzzeWE2Oh0JeRsiMuK2nzW2SjbuQ619kWORY4zw"));
//        driver.manage().addCookie(new Cookie("APISID", "oNv9nlOtPfU2Y2v9/Ahu0sfc9kUv-9gP4C"));
//        driver.manage().addCookie(new Cookie("DV", "g9_z001W6Q9WsO9g7sjbrkfqGjuT81idS42dMzB-qAEAAEAIMU5ewDV2cAAAADSuK0_N0U9ZNAAAAFgvVcgWkPXiFQAAAA"));
//        driver.manage().addCookie(new Cookie("HSID", "AvWvc6JtZMVIZiJJn"));
//        driver.manage().addCookie(new Cookie("NID", "513=a8UdkhCs4Rz3rL25ku7IimNru-RGErRQO3H8i358P20L-gEazGNFBWU1GTFsN3M5IFxj7hd6tgBBZP4qbg1IUjfRewzsOeH6JXtvRYQLFUPwS89gKpkSyRgEopRBheOH8GeElDUpJN1yeju5HFmUOLRdWEXvFH0IBxwfG1rBnxnDYuarbMci8xmdhcZtmeHsbbVFGRMo0nB9HZ9FDEbcAtMRrkO8WtEskhmdnWBCc3bn7xBRjhU4TXsrDAiDjmVec5CxiA1GN_U3p8reuKYeKNVJaGloTZPaMl-CuNnoPY9nAugNKDoU2SnyZ0Ly"));
//        driver.manage().addCookie(new Cookie("OTZ", "7539086_44_44_123780_40_436260"));
//        driver.manage().addCookie(new Cookie("SAPISID", "BuyzOJb5t45WXNAu/AcILX_iv36N5VJ5rN"));
//        driver.manage().addCookie(new Cookie("SEARCH_SAMESITE", "CgQI_poB"));
//        driver.manage().addCookie(new Cookie("SID", "g.a000jAj25Fn3zhIWK7IADZhlztWu0JE4tivjkeAGHdjPr3uhqZDVoatXmXOMOSX6GNdEK3br2wACgYKAVISAQASFQHGX2MiGzGGG-7rmkkIeNl3H7EeUhoVAUF8yKpZmUTrFoRMHEDWW0L4yucR0076"));
//        driver.manage().addCookie(new Cookie("SIDCC", "AKEyXzX2glbpyQbl1T12SqybGKMPfWYVlTmZgu0wqho2J-MtFcjO7DEpslNGV0tckrrQMbBS4A"));
//        driver.manage().addCookie(new Cookie("SSID", "AVwdOwsDQibfY_rpY"));
//        driver.manage().addCookie(new Cookie("__Secure-1PAPISID", "BuyzOJb5t45WXNAu/AcILX_iv36N5VJ5rN"));
//        driver.manage().addCookie(new Cookie("__Secure-1PSID", "g.a000jAj25Fn3zhIWK7IADZhlztWu0JE4tivjkeAGHdjPr3uhqZDVZHU6wjA3fWQqx-DIDWuE8AACgYKAVsSAQASFQHGX2MiRHxlMUDfv3sPBDGHaTJRjxoVAUF8yKoMdgJ403jiTYATjMw4xj3A0076"));
//        driver.manage().addCookie(new Cookie("__Secure-1PSIDCC", "AKEyXzVRn0NRR8e46SY4F0iYnPwYwxhO2LLWlLGjbRDIhxXVYqCN2J5TqJg7xxio_xve3vYhPg"));
//        driver.manage().addCookie(new Cookie("__Secure-1PSIDTS", "sidts-CjIBLwcBXH0oGf5DDreB1s80TC3KJgjfIoWtiwpeHS-yso_hEHWQ2jJwTUG80rs1JE6BcxAA"));
//        driver.manage().addCookie(new Cookie("__Secure-3PAPISID", "BuyzOJb5t45WXNAu/AcILX_iv36N5VJ5rN"));
//        driver.manage().addCookie(new Cookie("__Secure-3PSID", "g.a000jAj25Fn3zhIWK7IADZhlztWu0JE4tivjkeAGHdjPr3uhqZDVARePGegZtcGoCxcWbR8kjAACgYKAYkSAQASFQHGX2MiGSoFzCIrGaeLv3IoRlFoVhoVAUF8yKpAlO2KkRlJRmE6-ovAdVwH0076"));
//        driver.manage().addCookie(new Cookie("__Secure-3PSIDCC", "AKEyXzW3jqcncEnSROj-uepXmVuo0q2K8nUqnt-4qwC8WrfHH3RFVc8QISb2Z6FszzfVSUpJQA"));
//        driver.manage().addCookie(new Cookie("__Secure-3PSIDTS", "sidts-CjIBLwcBXH0oGf5DDreB1s80TC3KJgjfIoWtiwpeHS-yso_hEHWQ2jJwTUG80rs1JE6BcxAA"));
//        driver.manage().addCookie(new Cookie("__tld__", "null"));
//        driver.manage().addCookie(new Cookie("__zzatw-smm", "MDA0dC0cTHtmcDhhDHEWTT17CT4VHThHKHIzd2VcRCFTKgofWUgeFwkrXRI5dF0OPnVXLwkqPWx0MGFRUUtiDxwXMlxOe3NdZxBEQE1HQnR0LUFqHmZHYR9MXElraWJRNF0tQUpPJQp0OWV2EA==wxn7wQ=="));
//        driver.manage().addCookie(new Cookie("_sa", "SA1.799c2974-8cf4-4415-a8b7-0f507b46c573.1714651433"));
//        driver.manage().addCookie(new Cookie("adspire_uid", "AS.1584466409.1714651429"));
//        driver.manage().addCookie(new Cookie("cfidsw-smm", "r71JxzM4sGWZ4F+YyyhTcGcEgTRWdc1SmZr4XWlHIixpAvxfGjXSiy5NwlfYVS0OWiAp5DTmIE8AYx0x58l6pVBXf0bsOzonPjiiFynyliFz3tH4ZrlSD04ImwiEqF+lrs98IdWlGIEEKtnsnNwGIEZ9XWSoISmKfyBpr48="));
//        driver.manage().addCookie(new Cookie("cfidsw-smm", "r71JxzM4sGWZ4F+YyyhTcGcEgTRWdc1SmZr4XWlHIixpAvxfGjXSiy5NwlfYVS0OWiAp5DTmIE8AYx0x58l6pVBXf0bsOzonPjiiFynyliFz3tH4ZrlSD04ImwiEqF+lrs98IdWlGIEEKtnsnNwGIEZ9XWSoISmKfyBpr48="));
//        driver.manage().addCookie(new Cookie("device_id", "0908bac6-087c-11ef-91be-fa163e5517ba"));
//        driver.manage().addCookie(new Cookie("ecom_token", "3cd471dc-ad2f-423b-a41c-c8d1f171be7d"));
//        driver.manage().addCookie(new Cookie("isOldUser", "true"));
//        driver.manage().addCookie(new Cookie("region_info", "%7B%22displayName%22%3A%22%D0%9C%D0%BE%D1%81%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%B0%D1%8F%20%D0%BE%D0%B1%D0%BB%D0%B0%D1%81%D1%82%D1%8C%22%2C%22kladrId%22%3A%225000000000000%22%2C%22isDeliveryEnabled%22%3Atrue%2C%22geo%22%3A%7B%22lat%22%3A55.755814%2C%22lon%22%3A37.617635%7D%2C%22id%22%3A%2250%22%7D"));
//        driver.manage().addCookie(new Cookie("remixlang", "0"));
//        driver.manage().addCookie(new Cookie("remixstid", "1120285989_sml3mtVHpMz7GVYdrH7AAHNMp8tS613m3CXQul9GYKw"));
//        driver.manage().addCookie(new Cookie("remixstlid", "9095894452430786481_530c6dh41sXAjViDsp5xzmyttfvvoUuNxMWvOBNzYUc"));
//        driver.manage().addCookie(new Cookie("remixvkcom", "1"));
//        driver.manage().addCookie(new Cookie("sbermegamarket_token", "3cd471dc-ad2f-423b-a41c-c8d1f171be7d"));
//        driver.manage().addCookie(new Cookie("spid", "1714651427122_95293755e3694c76a789c0c7ca904dae_4mek541bxidn7tg1"));
//        driver.manage().addCookie(new Cookie("spsc", "1714651427122_8bdb4198e4dd85449d9f94b32a4c9619_2dc4c47e5beb4aae25be080fa9d16c8093e7e989cef732b63b8bada59af3d7da"));
//        driver.manage().addCookie(new Cookie("ssaid", "0a8c2ba0-087c-11ef-8a99-53062e1b8cf4"));
//        driver.manage().addCookie(new Cookie("uxs_uid", "0c27b330-087c-11ef-ac0b-dbfe4f1636f2"));
//    }

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
