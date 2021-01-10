import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Valorant_Scraper {
    private static String base_url = "https://tracker.gg/valorant/profile/riot/Gwoodz%23NA1/overview?playlist=competitive";
    private static String working_url = base_url;
    public static void main(String[] args) throws InterruptedException {
        /*
        x make it quicker
        make it so there is a database // clear playerdata //
        make it so i dont need to run it over all the days again
        make a graph to show ranks
         */

        System.setProperty("webdriver.chrome.driver", "C:/Users/zgarw/Documents/Projects/valorant_stats_2/chromedriver/chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        ChromeDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.navigate().to(base_url);

        final WebElement allGames = driver.findElementByClassName("trn-gamereport-list");

        final List<String> dateGamesPlayed = createDatesGamesPlayed(allGames);
        final List<String> linksToMatches = createLinksToGames(allGames);
        List<List<String>> playerData = new ArrayList<>(); // first list is links // second list is names // third list is ranks


        playerData = createPlayerData(linksToMatches, driver);

        for (String pr : playerData.get(2)) {
            System.out.println(pr);
        }
        driver.quit();
    }

    private static List<String> createDatesGamesPlayed(WebElement allGames) {
        final List<WebElement> dateGamesPlayedWE = allGames.findElements(By.tagName("h3"));
        final List<String> dateGamesPlayed = new ArrayList<>();
        for (WebElement we : dateGamesPlayedWE) {
            dateGamesPlayed.add(we.getText());
        }
        return dateGamesPlayed;
    }

    private static List<String> createLinksToGames(WebElement allGames) {
        final List<WebElement> webElementsForLinksToMatches = allGames.findElements(By.tagName("a"));
        final List<String> linksToMatches = new ArrayList<>();
        for (WebElement we : webElementsForLinksToMatches) {
            linksToMatches.add(we.getAttribute("href"));
        }
        return linksToMatches;
    }

    private static List<String> createListPlayerNames(WebElement allPlayers) {
        List<WebElement> playerNamesWE = allPlayers.findElements(By.className("trn-ign"));
        List<String> playerNames = new ArrayList<>();
        for (WebElement we : playerNamesWE) {
            playerNames.add(we.getText().replace(" #", "#"));
        }
        return playerNames;
    }

    private static List<String> createLinksToPlayers(WebElement allPlayers, ChromeDriver driver) {
        List<WebElement> playerNamesWE = allPlayers.findElements(By.className("trn-ign"));
        List<WebElement> linksToPlayersWE = new ArrayList<>();
        List<String> linksToPlayers = new ArrayList<>();
        for(int i = 0; i < playerNamesWE.size(); i++) {
            JavascriptExecutor js = driver;
            js.executeScript("arguments[0].click();", playerNamesWE.get(i));

            allPlayers = driver.findElementByClassName("overview__agent");
            linksToPlayersWE.add(allPlayers.findElement(By.tagName("a")));
            linksToPlayers.add(linksToPlayersWE.get(i).getAttribute("href"));
            linksToPlayers.set(i, linksToPlayers.get(i) + "?playlist=competitive");
        }
        return linksToPlayers;
    }

    private static List<String> createPlayerRanks(List<List<String>> playerData, ChromeDriver driver) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        final List<String> playerRanks = new ArrayList<>();
        for(int i = 0; i < playerData.get(0).size(); i++) {
            working_url = (String) playerData.get(0).get(i);
            driver.navigate().to(working_url);
            //TimeUnit.SECONDS.sleep(3);
            try {
                WebElement playerRankWE = driver.findElementByClassName("valorant-highlighted-stat__value");
                playerRanks.add(playerRankWE.getText());
            } catch (NoSuchElementException ex){
                playerRanks.add("NONE");
            }
        }
        return playerRanks;
    }

    private static List<List<String>> createPlayerData(List<String> linksToMatches, ChromeDriver driver) throws InterruptedException {
        List<List<String>> playerData = new ArrayList<>();
        for(int i = 0; i < linksToMatches.size(); i++) {
            working_url = linksToMatches.get(i);
            driver.navigate().to(working_url);
            WebElement allPlayers = driver.findElementByClassName("overview__rosters");
            if(i == 0) {
                playerData.add(createLinksToPlayers(allPlayers, driver));
                playerData.add(createListPlayerNames(allPlayers));
            } else {
                playerData.get(0).addAll(createLinksToPlayers(allPlayers, driver));
                playerData.get(1).addAll(createListPlayerNames(allPlayers));
            }
        }

        playerData.set(0, removeDuplicates(playerData.get(0)));
        playerData.set(1, removeDuplicates(playerData.get(1)));

        final List<String> playerRanks = createPlayerRanks(playerData, driver);
        playerData.add(playerRanks);

        return playerData;
    }

    private static List<String> removeDuplicates(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

}


