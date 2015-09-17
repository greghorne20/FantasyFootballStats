/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fantasyfootballstats;

/**
 *
 * @author Greg Horne
 */
public class Team
{
    public final int NUMOFWEEKS = 16;
    
    String name;
    int wins = 0;
    int losses = 0;
    int ties = 0;
    Double pointsFor = 0.0;
    Double[] scoresByWeek = new Double[NUMOFWEEKS];
    
    
    Team()
    {
        //empty constructor
    }
    
    //ACCESSOR METHODS
    
    public int getWins()
    {
        return wins;
    }
    
    public int getLosses()
    {
        return losses;
    }
   
    public int getTies()
    {
        return ties;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Double getPointsFor()
    {
        return pointsFor;
    }
    
    public Double getScoresByWeek(int week)
    {
        return scoresByWeek[week];
    }
    
    
    //Mutator Methods
    
    public void setWins(int i)
    {
        wins = i;
    }
    
    public void setLosses(int i)
    {
        losses = i;
    }
    
    public void setTies(int i)
    {
        ties = i;
    }
    
    public void setName(String n)
    {
        name = n;
    }
    
    public void setPointsFor(Double i)
    {
        pointsFor = i;
    }
    
    public void setScoresByWeek(int week, Double score)
    {
        scoresByWeek[week] = score;
    }
}
