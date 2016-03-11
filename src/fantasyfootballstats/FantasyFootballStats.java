/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** THINGS TO DO:
 *  Put most of main method in separate methods, allowing years as 
 * Create a Score class, that keeps the year, week, and team, along with the score
 *  Calculate playoff chances
 */

package fantasyfootballstats;


import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.helper.*; 
import org.jsoup.parser.*;
import org.jsoup.safety.*;
import org.jsoup.select.*;

import java.io.IOException;
import java.lang.Double;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import org.jsoup.Connection.Method;

/**
 *
 * @author Greg Horne
 */
public class FantasyFootballStats {

    // League id for our league: 69673
    
    final static boolean USE_TABS_FOR_EXCEL = true;
    final static boolean DISPLAY_TEAM_INFO = false;
    final static boolean DO_EVERY_YEAR = false;
    
    
    
    static int NUM_OF_TEAMS;
    static String LEAGUE_NAME;
    static int NUM_OF_WEEKS;
    static int ROWS_BTWN_WEEKS;
    
    static double highestScore = 0;
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException
    {
        
        System.out.println("Please enter the league ID (i.e. the number found at the end of league URL)");
        System.out.print("League ID: ");
        Scanner scanner = new Scanner(System.in);
        String leagueID = scanner.nextLine();
        //String our leagueID = "69673";
        
        String settingsUrl = "http://games.espn.go.com/ffl/leaguesetup/settings?leagueId=" + leagueID;
        Document doc = Jsoup.connect(settingsUrl).get();
        Element table = doc.select("table").get(1);
        Elements rows = table.select("tr");
        
        Elements row = doc.select("tr:contains(Number Of Teams)");
        NUM_OF_TEAMS = Integer.parseInt( row.get(1).text().substring(16));
        ROWS_BTWN_WEEKS = NUM_OF_TEAMS / 2 + 3;
        
        row = doc.select("tr:contains(League Name)");
        LEAGUE_NAME = ( row.get(1).text().substring(12));
        
        row = doc.select("tr:contains(Regular Season Matchups)");
        NUM_OF_WEEKS = Integer.parseInt( row.get(1).text().substring(24,26));
        
        System.out.println(LEAGUE_NAME);
        System.out.println("TEAMS: " + NUM_OF_TEAMS);
        System.out.println("WEEKS IN REG SEASON: " + NUM_OF_WEEKS);
        System.out.println("======================================\n");
        
     
        String url = "http://games.espn.go.com/ffl/schedule?leagueId=" + leagueID;    
          
        doc = Jsoup.connect(url).get();
        
        Team[] teams = new Team[NUM_OF_TEAMS];
        
        fetchTeamNames(teams, doc);
        getSeasonInfo(teams, doc);
        

        if(DISPLAY_TEAM_INFO)
        {
        for(Team team: teams)
            {
                System.out.println("Name: " + team.getName());
                System.out.println("Record: " + team.getWins() + "-" + team.getLosses() + "-" + team.getTies());
                System.out.println("Rank: " + team.getRank());
                System.out.println("Points For: " + team.getPointsFor());
                System.out.println("Points Rank: " + team.getPointsRank());
                System.out.println("Breakdown Record: " + team.getBreakdownWins() + "-" + team.getBreakdownLosses() + "-" + team.getBreakdownTies());
                System.out.println("Breakdown Rank: " + team.getBreakdownRank());
                System.out.println("----------------------------");
            }
        }
        
        printPowerRankings(USE_TABS_FOR_EXCEL, teams);
        
        if(DO_EVERY_YEAR)
        {
            for(int i = 2014; i > 2004; i--)
            {
                System.out.println(i);
                settingsUrl = "http://games.espn.go.com/ffl/leaguesetup/settings?leagueId=" + leagueID + "&seasonId=" + i;
                doc = Jsoup.connect(settingsUrl).get();
                table = doc.select("table").get(1);
                rows = table.select("tr");

                row = doc.select("tr:contains(Number Of Teams)");
                NUM_OF_TEAMS = Integer.parseInt( row.get(1).text().substring(16));
                ROWS_BTWN_WEEKS = NUM_OF_TEAMS / 2 + 3;

                row = doc.select("tr:contains(League Name)");
                LEAGUE_NAME = ( row.get(1).text().substring(12));

                row = doc.select("tr:contains(Regular Season Matchups)");
                NUM_OF_WEEKS = Integer.parseInt( row.get(1).text().substring(24,26));
                
                url = "http://games.espn.go.com/ffl/schedule?leagueId=" + leagueID + "&seasonId=" + i;    
          
                doc = Jsoup.connect(url).get();
        
                teams = new Team[NUM_OF_TEAMS];
        
                fetchTeamNames(teams, doc);
                getSeasonInfo(teams, doc);
                
                
            }
            
            System.out.println("Highest Score ever:" + highestScore);      
            
        }
        
        
    
           
         
                
        
                
    }
    
    public static void printPowerRankings(boolean useTabsForExcel, Team[] teams)
    {
        if(useTabsForExcel)
        {
            System.out.println("\nPower Rankings");
            System.out.printf("%-25s%-15s%-15s%-15s%-15s", new Object[]{ "Team" + "\t", "PointsFor" + "\t\t", "Record" + "\t\t" , "Brkdwn. Rec." + "\t\t", "Power"} );
            System.out.println();
            for(Team team: teams)
            {
                System.out.printf("%-25s%-15s%-15s%-15s%-15s",  team.getName() + "\t" , team.getPointsRank() + "\t" + team.getPointsFor() + "\t" , team.getRank() + "\t" + team.getRecord() + "\t" , team.getBreakdownRank() + "\t" + team.getBreakdownRecord() + "\t"  , team.getPowerRanking());
                System.out.println();
            }
        
        }
        else
        {
            System.out.println("\nPower Rankings");
            System.out.printf("%-25s%-15s%-15s%-15s%-15s", new Object[]{ "Team", "PointsFor", "Record", "Brkdwn. Rec.", "Power"} );
            System.out.println();
            for(Team team: teams)
            {
                System.out.printf("%-25s%-7d%-8.1f%-5d%-10s%-5d%-10s%-15s",  team.getName() , team.getPointsRank() , team.getPointsFor() , team.getRank() , team.getRecord() , team.getBreakdownRank() ,team.getBreakdownRecord() , team.getPowerRanking());
                System.out.println();
            }
        
        }
    }
    
    /*
    * Takes a document of an ESPN fantasy Schedule page, creates NUM_OF_TEAMS Teams
    * and sets their team names
    */
    public static void fetchTeamNames(Team[] teams, Document doc)
    {
                // for loop that iterators through the rows of teams and sets their names
        int t = 0;  // additional iterator to go through teams since there are two teams in each row
        for(int i=2; i < 2 + (NUM_OF_TEAMS / 2); i++)
        {
            Element table = doc.select("table").get(1);
            Elements rows = table.select("tr");
            
            Element row = rows.get(i);
            Elements cols = row.select("td");
            
            teams[t] = new Team();
            teams[t].setName(cols.get(0).text().split("\\(")[0]);
            
            teams[++t] = new Team();
            teams[t].setName(cols.get(3).text().split("\\(")[0]);
            
            t++;

        }
        
    }
    
    /*
    * Takes an array of Teams, and a doc of an ESPN schedule page.  Goes through
    * the schedule of played games and gets the scores for each team.  Then sets
    * all of the rankings for the teams according to the game info
    */
    public static void getSeasonInfo(Team[] teams, Document doc)
    {
        Element table = doc.select("table").get(1);
        Elements rows = table.select("tr");
        
        int i = 2; //where first tr starts with teams
        for(int week = 1; i < NUM_OF_WEEKS * ROWS_BTWN_WEEKS; week++ )
        {
            
            Element row = rows.get(i);
            Elements cols = row.select("td");
            String test = cols.get(5).text();
            if ( (cols.get(5).text().equals("Preview")) || ( (cols.get(5).text().equals("Box")))  )
            {
                break; // no more weeks have been played
            }
            else
            {

                getWeekInfo(teams, doc, week);
            }
                
            i = i + ROWS_BTWN_WEEKS;
        }
        
        rankTeamsByBreakdownRecord(teams);
        rankTeamsByPointsFor(teams);
        rankTeamsByWinPerct(teams);
        setPowerRankings(teams);
        
    }
    
    /*
    *  Given an array of teams, a doc of an espn fantasy schedule and an int of the
    *  week in which to gather info, this function fetches the info for the games played in that week
    *  and gives the team its scores for that week.
    */
    public static void getWeekInfo(Team[] teams, Document doc, int week)
    {
                        
        
        Element table = doc.select("table").get(1);
        Elements rows = table.select("tr");
            
        int startingRow = 2 + (week-1) * ROWS_BTWN_WEEKS;
        
        double[] weeklyPoints = new double[NUM_OF_TEAMS];
        int weeklyPointsItr = 0;
        
        for(int i = startingRow; i < startingRow + (NUM_OF_TEAMS / 2); i++)
        {
            Element row = rows.get(i);
            Elements cols = row.select("td");

            
            Double awayScore = Double.parseDouble(cols.get(5).text().split("\\-")[0].replace("*", ""));                
            Double homeScore = Double.parseDouble(cols.get(5).text().split("\\-")[1].replace("*", ""));                
                
                     
            for( Team team: teams)
            {
                
                if(cols.get(0).text().split("\\(")[0].equals(team.getName())) // if they are away team
                {
                    team.setScoresByWeek(week, awayScore );
                    if(awayScore > homeScore)
                    {
                        team.setWins( team.getWins() + 1);
                        if(highestScore < awayScore)
                        {
                            highestScore = awayScore;
                            //System.out.println("Team: " + team.getName() + " Week: " + week + " Score: " + highestScore);
                        }
                    }
                    else if(homeScore > awayScore)
                    {
                        team.setLosses( team.getLosses() + 1);
                        if(highestScore < homeScore)
                        {
                            highestScore = homeScore;
                            //System.out.println("Team: " + team.getName() + " Week: " + week + " Score: " + highestScore);
                        }
                            
                    }
                    else
                        team.setTies( team.getTies() + 1); 
                    
                    team.setPointsFor(awayScore + team.getPointsFor());
                    
                    weeklyPoints[weeklyPointsItr] = awayScore;
                    weeklyPointsItr++;
                }
                else if(cols.get(3).text().split("\\(")[0].equals(team.getName()))  // if they are home team
                {
                    
                    team.setScoresByWeek(week, homeScore );
                    
                    if(awayScore < homeScore)
                        team.setWins( team.getWins() + 1);
                    else if(homeScore < awayScore)
                        team.setLosses( team.getLosses() + 1);
                    else
                        team.setTies( team.getTies() + 1); 
                    
                    team.setPointsFor(homeScore + team.getPointsFor());
                    
                    weeklyPoints[weeklyPointsItr] = homeScore;
                    weeklyPointsItr++;
                }
            }
        }
        
        
        updateBreakdownRecord(teams, weeklyPoints, week);
    }
    
    /*
    * Updates the breakdown record of all teams, given the array of teams, the array
    * of points score in each game, and the int of the week.
    * Sets the breakdown record for each team in teams.
    */
    public static void updateBreakdownRecord(Team[] teams, double[] weeklyPoints, int week)
    {
        Arrays.sort(weeklyPoints);
        for(Team team: teams)
        {
            // Check teams score against each score that week
            for(Double score: weeklyPoints)
            {
                if(team.getScoresByWeek(week) > score)
                    team.setBreakdownWins( team.getBreakdownWins() + 1);
                else if(team.getScoresByWeek(week) < score)
                    team.setBreakdownLosses( team.getBreakdownLosses() + 1);
                else
                    team.setBreakdownTies( team.getBreakdownTies() + 1);
            }
            
            // The team tied itself, but that shouldn't count, so
            team.setBreakdownTies( team.getBreakdownTies() - 1);
            
        }
    }
    
    /*
    * Gives each team in teams a rank according to their cumulative breakdown record
    */
    public static void rankTeamsByBreakdownRecord(Team[] teams)
    {
        
        ArrayList<Team> teamsRankedByBreakdown = new ArrayList<>(Arrays.asList(teams));
        Collections.sort(teamsRankedByBreakdown, Team.BreakdownComparator);
        
        
        for(int i = 0; i < teamsRankedByBreakdown.size(); i++)
        {
            for(Team team: teams)
            {
                if(team.getName().equals( teamsRankedByBreakdown.get(i).getName()))
                {
                    team.setBreakdownRank(i+1);
                    break;
                }
            }
        }
        
    }
    
    /*
    * Gives each team in teams a rank according to their cumulative Points For
    */
    public static void rankTeamsByPointsFor(Team[] teams)
    {
        ArrayList<Team> teamsRankedByPF = new ArrayList<>(Arrays.asList(teams));
        Collections.sort(teamsRankedByPF, Team.PointsForComparator);
        
        
        for(int i = 0; i < teamsRankedByPF.size(); i++)
        {
            for(Team team: teams)
            {
                if(team.getName().equals( teamsRankedByPF.get(i).getName()))
                {
                    team.setPointsRank(i+1);
                    break;
                }
            }
        }
    }
    
    /*
    * Gives each team in teams a rank according to their cumulative Win/loss record
    */
    public static void rankTeamsByWinPerct(Team[] teams)
    {
        ArrayList<Team> teamsRankedByWP = new ArrayList<>(Arrays.asList(teams));
        Collections.sort(teamsRankedByWP);
        
        
        for(int i = 0; i < teamsRankedByWP.size(); i++)
        {
            for(Team team: teams)
            {
                if(team.getName().equals( teamsRankedByWP.get(i).getName()))
                {
                    team.setRank(i+1);
                    break;
                }
            }
        }
    }
    
    /*
    * Gives each team in teams a power ranking.  Power Ranking is calculated by 
    * ordering the teams in ascending order according to WinPerctRank + PointsForRank + BreakdownRecord
    */
    public static void setPowerRankings(Team[] teams)
    {
        ArrayList<Team> teamsRankedByPR = new ArrayList<>(Arrays.asList(teams));
        Collections.sort(teamsRankedByPR, Team.PowerRankingsComparator);
        
        
        for(int i = 0; i < teamsRankedByPR.size(); i++)
        {
            for(Team team: teams)
            {
                if(team.getName().equals( teamsRankedByPR.get(i).getName()))
                {
                    team.setPowerRank(i+1);
                    break;
                }
            }
        }
    }
    
}
