package kino;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import kino.pages.KinoparkMyFilmsPage;
import kino.pages.KinopoiskErrorPage;
import kino.pages.KinopoiskPage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
public class App {

    private static final String KINOPARK_COOKIE_FIELD_NAME = "kinobrbyauth";
    private static final String KINOPOISK_COOKIE_FIELD_NAME = "ya_sess_id";
    private static final String START_FROM_FIELD_NAME = "startFrom";

    private static final Properties PROPERTIES = new Properties();

    static {
        readProperties();
    }

    private static void readProperties() {
        try {
            PROPERTIES.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            Optional.ofNullable(System.getenv(KINOPARK_COOKIE_FIELD_NAME)).ifPresent(value -> PROPERTIES.setProperty(KINOPARK_COOKIE_FIELD_NAME, value));
            Optional.ofNullable(System.getenv(KINOPOISK_COOKIE_FIELD_NAME)).ifPresent(value -> PROPERTIES.setProperty(KINOPOISK_COOKIE_FIELD_NAME, value));
            Optional.ofNullable(System.getenv(START_FROM_FIELD_NAME)).ifPresent(value -> PROPERTIES.setProperty(START_FROM_FIELD_NAME, value));
        } catch (IOException e) {
            log.warn("Properties not loaded:", e);
        }
    }

    public static void main(String[] args) {
        Configuration.browser = "chrome";
        //configuration.headless = true;
        Configuration.browserSize = "1366x768";

        Selenide.open(PROPERTIES.getProperty("site1"));
        WebDriverRunner.getWebDriver().manage().deleteCookieNamed(KINOPARK_COOKIE_FIELD_NAME);
        WebDriverRunner.getWebDriver().manage().addCookie(new Cookie(KINOPARK_COOKIE_FIELD_NAME, PROPERTIES.getProperty(KINOPARK_COOKIE_FIELD_NAME)));
        Selenide.refresh();
        List<Film> films = new KinoparkMyFilmsPage().clickAll().readFilms().stream().filter(film -> film.rating() <= Integer.parseInt(PROPERTIES.getProperty(START_FROM_FIELD_NAME))).collect(Collectors.toList());

        Selenide.open(PROPERTIES.getProperty("site2"));
        WebDriverRunner.getWebDriver().manage().deleteCookieNamed(KINOPOISK_COOKIE_FIELD_NAME);
        WebDriverRunner.getWebDriver().manage().addCookie(new Cookie(KINOPOISK_COOKIE_FIELD_NAME, PROPERTIES.getProperty(KINOPOISK_COOKIE_FIELD_NAME)));
        Selenide.refresh();

        films.forEach(film -> {
            KinopoiskPage kinopoiskPage = new KinopoiskPage().searchFor(film.text());
            if (new KinopoiskErrorPage().isOpen()) {
                Selenide.back();
                new KinopoiskPage().searchFor(film.text());
            }
            if (kinopoiskPage.isVoted()) {
                Logger.getGlobal().info(String.format("%s has already been voted for", film.text()));
            } else {
                Logger.getGlobal().info(String.format("Setting %d rating for %s. Page title is '%s'", film.rating(), film.text(), WebDriverRunner.getWebDriver().getTitle()));
                Selenide.sleep(1000);
                kinopoiskPage.vote(film.rating());
            }
        });

        Selenide.closeWebDriver();
    }
}
