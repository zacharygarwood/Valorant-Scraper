import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Valorant_Scraper {
    private static String base_url = "https://tracker.gg/valorant/profile/riot/Gwoodz%23NA1/overview?playlist=competitive";
    private static String working_url = base_url;
    public static void main(String[] args) throws Exception {
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
        List<List<String>> playerData = createPlayerData(linksToMatches, driver); // first list is links // second list is names // third list is ranks

        createPlayerDataCSV(playerData);
        removeDuplicatesFromCSV("C:/Users/zgarw/Documents/Projects/valorant_stats_2/data/player_data.csv");
        driver.quit();
    }

    private static List<String> createDatesGamesPlayed(WebElement allGames) {
        System.out.println("Getting dates to matches...");
        final List<WebElement> dateGamesPlayedWE = allGames.findElements(By.tagName("h3"));
        final List<String> dateGamesPlayed = new ArrayList<>();
        for (WebElement we : dateGamesPlayedWE) {
            dateGamesPlayed.add(we.getText());
        }
        return dateGamesPlayed;
    }

    private static List<String> createLinksToGames(WebElement allGames) {
        System.out.println("Getting links to matches...");
        final List<WebElement> webElementsForLinksToMatches = allGames.findElements(By.tagName("a"));
        final List<String> linksToMatches = new ArrayList<>();
        for (WebElement we : webElementsForLinksToMatches) {
            linksToMatches.add(we.getAttribute("href"));
        }
        return linksToMatches;
    }

    private static List<String> createListPlayerNames(WebElement allPlayers) {
        System.out.println("Getting player names...");
        List<WebElement> playerNamesWE = allPlayers.findElements(By.className("trn-ign"));
        List<String> playerNames = new ArrayList<>();
        for (WebElement we : playerNamesWE) {
            playerNames.add(we.getText().replace(" #", "#"));
        }
        return playerNames;
    }

    private static List<String> createLinksToPlayers(WebElement allPlayers, ChromeDriver driver) {
        System.out.println("Getting links to players...");
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
        System.out.println("Getting player ranks...");
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
        System.out.printf("Creating player data...");
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

    private static void createPlayerDataCSV(List<List<String>> playerData) {
        System.out.println("Creating player data CSV file...");
        try {
            FileWriter fw = new FileWriter("C:/Users/zgarw/Documents/Projects/valorant_stats_2/data/player_data.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            for(int i = 0; i < playerData.get(0).size(); i++) {
                pw.println(playerData.get(1).get(i) + "," + playerData.get(2).get(i) + "," + playerData.get(0).get(i));
                pw.flush();
            }
            pw.close();
        } catch (Exception e) {

        }
    }

    private static List<String> removeDuplicates(List<String> list) {
        System.out.println("Removing duplicate players...");
        List<String> newList = new ArrayList<>();
        for (String element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    private static void removeDuplicatesFromCSV(String filepath) throws Exception {
        System.out.printf("Removing duplicates from CSV file...");

        String filePath = "C:/Users/zgarw/Documents/Projects/valorant_stats_2/data/player_data.csv";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        Set<String> lines = new HashSet<String>(10000); // maybe should be bigger
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (String unique : lines) {
            writer.write(unique);
            writer.newLine();
        }
        writer.close();
    }
}




