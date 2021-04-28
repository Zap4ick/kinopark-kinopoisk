package kino.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import kino.Film;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class KinoparkMyFilmsPage {

    private final SelenideElement btnAll = $x("//div[@class='tbs']/a[last()]");

    private ElementsCollection ecFilmName = $$x("//ul[@class='film_list']//a[text()]");
    private ElementsCollection ecFilmRating = $$("ul.film_list > li > em");

    public KinoparkMyFilmsPage clickAll() {
        btnAll.click();
        return this;
    }

    public List<Film> readFilms(){
        List<Film> result = new ArrayList<>();
        ecFilmName = ecFilmName.snapshot();
        ecFilmRating = ecFilmRating.snapshot();
        for (int i = 0; i<ecFilmName.size(); i++){
            result.add(new Film(ecFilmName.get(i).text(), Rating.getIntRatingByName(ecFilmRating.get(i).text())));
        }
        return result;
    }

    @AllArgsConstructor
    @Getter
    public enum Rating {
        _10(10, "шедевр"),
        _9(9, "отлично"),
        _8(8, "очень хорошо"),
        _7(7, "хорошо"),
        _6(6, "неплохо"),
        _5(5, "так себе"),
        _4(4, "слабо"),
        _3(3, "плохо"),
        _2(2, "ужасно"),
        _1(1, "хуже не бывает");

        int num;
        String name;

        public static int getIntRatingByName(String name){
            return Arrays.stream(values()).filter(rating -> rating.getName().equals(name)).findAny().orElseThrow().getNum();
        }
    }
}
