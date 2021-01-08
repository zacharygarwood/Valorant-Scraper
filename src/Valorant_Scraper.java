import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Valorant_Scraper {
    private static String base_url = "https://tracker.gg/valorant/profile/riot/Gwoodz%23NA1/overview?playlist=competitive";
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:/Users/zgarw/Documents/Projects/valorant_stats_2/chromedriver/chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get(base_url);
        final WebElement allGames = driver.findElementByClassName("trn-gamereport-list");
        final List<WebElement> dateGamesPlayedWE = allGames.findElements(By.tagName("h3"));
        final List<String> dateGamesPlayed = new ArrayList<>();

        final List<WebElement> webElementsForLinksToMatches = allGames.findElements(By.tagName("a"));
        final List<String> linksToMatches = new ArrayList<>();

        /*
        System.out.println("I AM HERE");
        working_url = linksToMatches.get(0);
        driver.get(working_url);
        System.out.println("POPOPOP");
        driver.quit();
        */

        for(int i = 0; i < webElementsForLinksToMatches.size(); i++) {
            linksToMatches.add(webElementsForLinksToMatches.get(i).getAttribute("href"));
            System.out.println(linksToMatches.get(i));
        }
        driver.quit();


        /* prints out the days played
        for(int i = 0; i < dateGamesPlayedWE.size(); i++) {
            dateGamesPlayed.add(dateGamesPlayedWE.get(i).getText());
            dateGamesPlayed.set(i, setStringLength(dateGamesPlayed.get(i), 6));
            System.out.println(dateGamesPlayed.get(i));
        }
        */



        /*
        System.out.println("LOOK AT ME");
        for(int i = 0; i < gamesPerDay.size(); i++) {
            if(gamesPerDay.get(i).getText().equals("Match is enqueued for processing.")) {
                System.out.println("POOOOOOOP");
                gamesPerDay.remove(i);
            }
        }

        for(WebElement l:gamesPerDay) {
            System.out.println(l.getText() + " THIS IS ONE WEBELEMENT");
        }
        */


    }

    private String setStringLength(String str, int num) {
        String fin_str = "";
        for(int i = 0; i < num; i++) {
            fin_str += str.charAt(i);
        }
        return fin_str;
    }
}


