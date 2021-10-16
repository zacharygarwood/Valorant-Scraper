import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Valorant_Scraper {
    private static String base_url = "https://tracker.gg/valorant/profile/riot/Gwoodz%23NA1/overview?playlist=deathmatch";
    private static String working_url = base_url;
    public static void main(String[] args) throws Exception {

        System.setProperty("webdriver.chrome.driver", "C:/Users/zgarw/Documents/Projects/valorant_stats_2/chromedriver/chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        ChromeDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.navigate().to(base_url);

        WebElement allGames = driver.findElementByClassName("trn-gamereport-list");


        List<String> linksToMatches = createLinksToGames(allGames);
        List<List<String>> playerData = createPlayerData(linksToMatches, driver);

        createPlayerDataCSV(playerData);
        removeDuplicatesFromCSV("C:/Users/zgarw/Documents/Projects/valorant_stats_2/data/player_data.csv");

        driver.quit();
    }



    private static List<String> createLinksToGames(WebElement allGames) {
        System.out.println("Getting links to matches...");
        List<WebElement> webElementsForLinksToMatches = allGames.findElements(By.tagName("a"));
        List<String> linksToMatches = new ArrayList<>();
        for (WebElement we : webElementsForLinksToMatches) {
            linksToMatches.add(we.getAttribute("href"));
        }
        return linksToMatches;
    }

    private static List<String> createListPlayerNames(WebElement allPlayers) {
        System.out.println("Getting player names...");
        List<WebElement> playerNamesWE = allPlayers.findElements(By.className("trn-ign"));
        List<String> playerNames = new ArrayList<>();

        for(WebElement we : playerNamesWE) {
            playerNames.add(we.getText().replace("\n", ""));
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

    private static List<String> createPlayerRanks(List<String> playerRanks, ChromeDriver driver) throws InterruptedException {

        try {
            WebElement playerRankWE = driver.findElementByClassName("valorant-highlighted-stat__value");
            playerRanks.add(playerRankWE.getText());
        } catch (NoSuchElementException ex){
            playerRanks.add("NONE");
        }
        return playerRanks;
    }

    private static List<String> createDatesPlayedWithPlayers(WebElement date, WebElement allPlayers) {
        System.out.println("Getting dates played...");
        List<String> playerDatesGamesPlayed = new ArrayList<>();
        List<WebElement> playerNamesWE = allPlayers.findElements(By.className("trn-ign"));
        for(int i = 0; i < playerNamesWE.size(); i++) {
            playerDatesGamesPlayed.add(date.getText().substring(0,8));
        }
        return playerDatesGamesPlayed;
    }

    private static List<String> createPlayersHSPercentage(List<String> playerHS, ChromeDriver driver) throws InterruptedException {
        System.out.println("Getting HS");
        try {
            WebElement playerHSWE = driver.findElementByClassName("weapon__accuracy-hits");
            playerHS.add(playerHSWE.getText().substring(0,3));
        } catch (NoSuchElementException ex){
            playerHS.add("NONE");
        }
        return playerHS;
    }

    private static List<String> createPlayersDPR(List<String> playerDPR, ChromeDriver driver) throws InterruptedException {
        System.out.println("Getting DPR");
        try {
            WebElement playerHSWE = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[2]/div/main/div[2]/div[3]/div[3]/div[4]/div[3]/div[1]/div/div[1]/span[2]"));
            playerDPR.add(playerHSWE.getText());
        } catch (NoSuchElementException ex){
            playerDPR.add("NONE");
        }
        return playerDPR;
    }
    private static List<String> createPlayersWIN(List<String> playerWIN, ChromeDriver driver) throws InterruptedException {
        System.out.println("Getting WIN");
        try {
            WebElement playerHSWE = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[2]/div/main/div[2]/div[3]/div[3]/div[4]/div[3]/div[4]/div/div[2]/span[2]"));
            playerWIN.add(playerHSWE.getText().substring(0,4));
        } catch (NoSuchElementException ex){
            playerWIN.add("NONE");
        }
        return playerWIN;
    }
    private static List<String> createPlayersKD(List<String> playerKD, ChromeDriver driver) throws InterruptedException {
        System.out.println("Getting KD");
        try {
            WebElement playerKDWE = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[2]/div/main/div[2]/div[3]/div[3]/div[4]/div[3]/div[2]/div/div[1]/span[2]"));
            playerKD.add(playerKDWE.getText());
        } catch (NoSuchElementException ex){
            playerKD.add("NONE");
        }
        return playerKD;
    }

    private static List<String> createPlayersSPR(List<String> playerSPR, ChromeDriver driver) throws InterruptedException {
        System.out.println("Getting SPR");
        try {
            WebElement playerKDWE = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[2]/div/main/div[2]/div[3]/div[3]/div[4]/div[5]/div[6]/div/div[1]/span[2]"));
            playerSPR.add(playerKDWE.getText());
        } catch (NoSuchElementException ex){
            playerSPR.add("NONE");
        }
        return playerSPR;
    }

    private static List<String> createPlayersKPR(List<String> playerKPR, ChromeDriver driver) throws InterruptedException {
        System.out.println("Getting KPR");
        try {
            WebElement playerKDWE = driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div[2]/div/main/div[2]/div[3]/div[3]/div[4]/div[5]/div[7]/div/div[2]/span[2]"));
            playerKPR.add(playerKDWE.getText());
        } catch (NoSuchElementException ex){
            playerKPR.add("NONE");
        }
        return playerKPR;
    }

    private static List<List<String>> createPlayerData(List<String> linksToMatches, ChromeDriver driver) throws InterruptedException {
        System.out.printf("Creating player data...");
        List<List<String>> playerData = new ArrayList<>();

        for(int i = 0; i < linksToMatches.size()-1; i++) {
            working_url = linksToMatches.get(i);
            driver.navigate().to(working_url);
            System.out.println(working_url);
            // getting dates of games played with players
            WebElement datePlayed = driver.findElementByClassName("scoreboard__time-timestamp");

            WebElement allPlayers = driver.findElementByClassName("overview__rosters");
            if(i == 0) {
                playerData.add(createLinksToPlayers(allPlayers, driver));
                playerData.add(createListPlayerNames(allPlayers));
                playerData.add(createDatesPlayedWithPlayers(datePlayed, allPlayers));
            } else {
                playerData.get(0).addAll(createLinksToPlayers(allPlayers, driver));
                playerData.get(1).addAll(createListPlayerNames(allPlayers));
                playerData.get(2).addAll(createDatesPlayedWithPlayers(datePlayed, allPlayers));
            }
        }

        playerData.set(0, removeDuplicates(playerData.get(0)));
        playerData.set(1, removeDuplicates(playerData.get(1)));

        //Get player ranks and HS percentage
        List<String> playerRanks = new ArrayList<>();
        List<String> playerHS = new ArrayList<>();
        List<String> playerDPR = new ArrayList<>();
        List<String> playerWIN = new ArrayList<>();
        List<String> playerKD = new ArrayList<>();
        List<String> playerSPR = new ArrayList<>();
        List<String> playerKPR = new ArrayList<>();
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        System.out.println("Getting player ranks...");
        for(int i = 0; i < playerData.get(0).size(); i++) {
            working_url = (String) playerData.get(0).get(i);
            driver.navigate().to(working_url);
            //if this doesn't work return the text from the methods and add that text to each arraylist
            createPlayerRanks(playerRanks,driver);
            createPlayersHSPercentage(playerHS,driver);
            createPlayersDPR(playerDPR, driver);
            createPlayersWIN(playerWIN, driver);
            createPlayersKD(playerKD, driver);
            createPlayersSPR(playerSPR, driver);
            createPlayersKPR(playerKPR, driver);
        }
        System.out.println("PLAYER RANKS");

        playerData.add(playerRanks);
        playerData.add(playerHS);
        playerData.add(playerDPR);
        playerData.add(playerWIN);
        playerData.add(playerKD);
        playerData.add(playerSPR);
        playerData.add(playerKPR);
        return playerData;
    }

    private static void createPlayerDataCSV(List<List<String>> playerData) {
        System.out.println("Creating player data CSV file...");
        try {
            FileWriter fw = new FileWriter("C:/Users/zgarw/Documents/Projects/valorant_stats_2/data/player_data.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            for(int i = 0; i < playerData.get(0).size(); i++) {
                // 0 = link to player, 1 = name, 2 = rank, 3 = hs percentage, 4 = date played with, 5 = damage per round, 6 = player win percentage, 7 = KD, 8 = SPR, 9 = KPR
                pw.println(playerData.get(1).get(i) + "," + playerData.get(3).get(i) + "," + playerData.get(4).get(i)  + "," + playerData.get(5).get(i) + "," +
                        playerData.get(6).get(i) + "," + playerData.get(2).get(i)+ "," + playerData.get(0).get(i) + "," + playerData.get(7).get(i) + "," + playerData.get(8).get(i) + "," + playerData.get(9).get(i));
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
        Set<String> lines = new HashSet<String>(10000);
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




