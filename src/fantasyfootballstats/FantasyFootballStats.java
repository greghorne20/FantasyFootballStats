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

/**
 *
 * @author Greg Horne
 */
public class FantasyFootballStats {

    final static int NUMOFTEAMS = 12;
    final static int NUMOFWEEKS = 13;
    final static int ROWS_BTWN_WEEKS = 9;
    
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException
    {
        
        
        String url = "http://games.espn.go.com/ffl/schedule?leagueId=69673";
          
        Document doc = Jsoup.connect(url).get();
        
        Team[] teams = new Team[NUMOFTEAMS];
        
        fetchTeamNames(teams, doc);
        getSeasonInfo(teams, doc);
        

        
        System.out.println(teams[1].getName());
        System.out.println(teams[1].getScoresByWeek(1));
        
    
           
               
                
        
                
    }
    
    /*
    * Takes a document of an ESPN fantasy Schedule page, creates NUMOFTEAMS Teams
    * and sets their team names
    */
    public static void fetchTeamNames(Team[] teams, Document doc)
    {
                // for loop that iterators through the rows of teams and sets their names
        int t = 0;  // additional iterator to go through teams since there are two teams in each row
        for(int i=2; i < 2 + (NUMOFTEAMS / 2); i++)
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
        for(int week = 1; i < NUMOFWEEKS; week++ )
        {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            String test = cols.get(5).text();
            if ( !(cols.get(5).text().equals("Preview")) )
                getWeekInfo(teams, doc, week);
            else
                return; // no more weeks have been played.
                
            i = i + ROWS_BTWN_WEEKS;
        }
        
    }
    
    public static void getWeekInfo(Team[] teams, Document doc, int week)
    {
                        
        
        Element table = doc.select("table").get(1);
        Elements rows = table.select("tr");
            
        int startingRow = 2 + (week-1) * ROWS_BTWN_WEEKS;
        
        for(int i = startingRow; i < startingRow + (NUMOFTEAMS / 2); i++)
        {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            
            for( Team team: teams)
            {
                Double awayScore = Double.parseDouble(cols.get(5).text().split("\\-")[0]);                
                Double homeScore = Double.parseDouble(cols.get(5).text().split("\\-")[1]);                
                
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
                }
            }



        }
    }
    
}
