/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    static int NUM_OF_TEAMS;
    static String LEAGUE_NAME;
    static int NUM_OF_WEEKS;
    final static int ROWS_BTWN_WEEKS = 9;
    
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException
    {
        /*
        // NOT WORKING
        //With this you login and a session is created
        Connection.Response res = Jsoup.connect("https://cdn.registerdisney.go.com/v2/responder/responder.js")
        .data("text", "greghorne20+espn@gmail.com", "password", "bday1390")
        .method(Method.POST)
        .execute();
        

        //This will get you cookies
        Map<String, String> loginCookies = res.cookies();
        */
        System.out.println("Please enter the league ID (i.e. the number found at the end of league URL");
        System.out.print("League ID: ");
        Scanner scanner = new Scanner(System.in);
        String leagueID = scanner.nextLine();
        //String leagueID = "69673";
        
        String settingsUrl = "http://games.espn.go.com/ffl/leaguesetup/settings?leagueId=" + leagueID;
        Document doc = Jsoup.connect(settingsUrl).get();
        Element table = doc.select("table").get(1);
        Elements rows = table.select("tr");
        
        Elements row = doc.select("tr:contains(Number Of Teams)");
        NUM_OF_TEAMS = Integer.parseInt( row.get(1).text().substring(16));
        
        row = doc.select("tr:contains(League Name)");
        LEAGUE_NAME = ( row.get(1).text().substring(12));
        
        row = doc.select("tr:contains(Regular Season Matchups)");
        NUM_OF_WEEKS = Integer.parseInt( row.get(1).text().substring(24,26));
        
        System.out.println(LEAGUE_NAME);
        System.out.println("TEAMS: " + NUM_OF_TEAMS);
        System.out.println("WEEKS IN REG SEASON: " + NUM_OF_WEEKS);
        System.out.println("======================================\n");
        
        
        /*
        //THIS WORKED FOR OUR LEAGUE BUT IS NOT UNIVERSAL
        Element row = rows.get(2);
        Elements cols = row.select("td");
        NUM_OF_TEAMS = Integer.parseInt( cols.get(1).text());
        
        row = rows.get(1);
        cols = row.select("td");
        LEAGUE_NAME =  cols.get(1).text();
        
        table = doc.select("table").get(17);
        rows = table.select("tr");
        row = rows.get(3);
        cols = row.select("td");
        NUM_OF_WEEKS =  Integer.parseInt( cols.get(1).text().split(" ")[0]);
        */
        
        
        
        String url = "http://games.espn.go.com/ffl/schedule?leagueId=" + leagueID;    
          
        doc = Jsoup.connect(url).get();
        
        Team[] teams = new Team[NUM_OF_TEAMS];
        
        fetchTeamNames(teams, doc);
        getSeasonInfo(teams, doc);
        

        
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
        
        System.out.println("\nPower Rankings");
        System.out.printf("%-25s%-15s%-15s%-15s%-15s", new Object[]{ "Team" , "PointsFor" , "Record" , "Brkdwn. Rec." , "Power"} );
        System.out.println();
        for(Team team: teams)
        {
            System.out.printf("%-25s%-15d%-15d%-15d%-15d",  team.getName() , team.getPointsRank() , team.getRank() , team.getBreakdownRank() , team.getPowerRanking());
            System.out.println();
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
    
    public static void getSeasonInfo(Team[] teams, Document doc)
    {
        Element table = doc.select("table").get(1);
        Elements rows = table.select("tr");
        
        int i = 2; //where first tr starts with teams
        for(int week = 1; i < NUM_OF_WEEKS; week++ )
        {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            String test = cols.get(5).text();
            if ( (cols.get(5).text().equals("Preview")) || ( (cols.get(5).text().equals("Box")))  )
                break; // no more weeks have been played
            else
                getWeekInfo(teams, doc, week);
                
            i = i + ROWS_BTWN_WEEKS;
        }
        
        rankTeamsByBreakdownRecord(teams);
        rankTeamsByPointsFor(teams);
        rankTeamsByWinPerct(teams);
        setPowerRankings(teams);
        
    }
    
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

            
            Double awayScore = Double.parseDouble(cols.get(5).text().split("\\-")[0]);                
            Double homeScore = Double.parseDouble(cols.get(5).text().split("\\-")[1]);                
                
                     
            for( Team team: teams)
            {
                
                if(cols.get(0).text().split("\\(")[0].equals(team.getName())) // if they are away team
                {
                    team.setScoresByWeek(week, awayScore );
                    if(awayScore > homeScore)
                        team.setWins( team.getWins() + 1);
                    else if(homeScore > awayScore)
                        team.setLosses( team.getLosses() + 1);
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
