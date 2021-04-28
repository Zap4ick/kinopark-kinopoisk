package kino.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import java.util.logging.Logger;

import static com.codeborne.selenide.Selenide.*;

public class KinopoiskPage {

    private final SelenideElement txbSearch = $("input[name=kp_query]");
    private final SelenideElement btnVote = $(".voting__closed");
    private final SelenideElement lblVotingValue = $(".voting__value");

    private ElementsCollection elSuggestions = $$x("//a[contains(@href,'film')]//div[@class='kinopoisk-header-suggest-item__title-container'][.//span[(not(contains(text(),'2022')))][(not(contains(text(),'2021')))]]"); //todo

    private ElementsCollection ecStars = $$(".voting__choice-star");

    public KinopoiskPage searchFor(String text) {
        Logger.getGlobal().info(String.format("Searching for %s", text));
        txbSearch.clear();
        txbSearch.sendKeys(text);
        Selenide.sleep(1000);
        elSuggestions = elSuggestions.snapshot();
        if (elSuggestions.isEmpty()) {
            Logger.getGlobal().warning(String.format("Film %s was not found!", text));
            return this;
        }
        SelenideElement exactTextElement = elSuggestions.stream().filter(element ->
                element.find("h4").text().equals(text)).findAny().orElse(null);
        if (exactTextElement != null) {
            exactTextElement.click();
            return this;
        }
        elSuggestions.get(0).click();
        return this;
    }

    public boolean isVoted() {
        return lblVotingValue.isDisplayed();
    }

    public KinopoiskPage vote(int num) {
        btnVote.should(Condition.appear).click();
        ecStars.get(num - 1).click();
        return this;
    }
}
