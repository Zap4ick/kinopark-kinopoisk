package kino.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class KinopoiskErrorPage {

    private SelenideElement lblErrorPage = $(".error-page");

    public boolean isOpen() {
        return lblErrorPage.isDisplayed();
    }
}
