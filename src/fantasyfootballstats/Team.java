/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fantasyfootballstats;

import java.util.Comparator;

/**
 *
 * @author Greg Horne
 */
public class Team implements Comparator<Team>, Comparable<Team>
{
    public final int NUMOFWEEKS = 16;
    
    String name;
    int wins = 0;
    int losses = 0;
    int ties = 0;
    int rank;
            
    Double pointsFor = 0.0;
    int pointsRank;
    
    Double[] scoresByWeek = new Double[NUMOFWEEKS];
    
    // variables for breakdown record
    int breakdownWins = 0;
    int breakdownLosses = 0;
    int breakdownTies = 0;
    int breakdownRank;
    
    int powerRanking;
    
    
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
    
    public int getBreakdownWins()
    {
        return breakdownWins;
    }
    
    public int getBreakdownLosses()
    {
        return breakdownLosses;
    }
    
    public int getBreakdownTies()
    {
        return breakdownTies;
    }
    
    public int getBreakdownRank()
    {
        return breakdownRank;
    }
    
    public int getPointsRank()
    {
        return pointsRank;
    }
    
    public int getRank()
    {
        return rank;
    }
    
    public int getPowerRanking()
    {
        return powerRanking;
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
    
    public void setBreakdownWins(int wins)
    {
        breakdownWins = wins;
    }
    
    public void setBreakdownLosses(int losses)
    {
        breakdownLosses = losses;
    }
    
    public void setBreakdownTies(int ties)
    {
        breakdownTies = ties;
    }
    
    public void setBreakdownRank(int rank)
    {
        breakdownRank = rank;
    }
    
    public void setPointsRank(int rank)
    {
        pointsRank = rank;
    }
    
    public void setRank(int ranking)
    {
        rank = ranking;
    }
    
    public void setPowerRank(int rank)
    {
        powerRanking = rank;
    }
    
    //Override comparator methods.
    //Teams are compared first by winning percentage, then by Points For
    @Override
    public int compareTo(Team otherTeam)
    {
        return compare(this, otherTeam);
    }
    
    
    @Override
    public int compare(Team teamA, Team teamB)
    {
        //If teams have the same winning percentage, compare by PointsFor
        if ((teamA.wins + teamA.ties / 2)  == (teamB.wins + teamB.ties / 2))
        {
            if(teamA.pointsFor > teamB.pointsFor)
                return -1;
            else
                return 1;
        }
        
        else
        {
            if ((teamA.wins + teamA.ties / 2)  > (teamB.wins + teamB.ties / 2))
                return -1;
            else
                return 1;
        }
 
    }
    
    
    public static Comparator<Team> BreakdownComparator = new Comparator<Team>()
    {

        @Override
        public int compare(Team teamA, Team teamB)
        {
            //If teams have the same winning percentage, compare by PointsFor
            if ((teamA.breakdownWins + teamA.breakdownTies / 2)  == (teamB.breakdownWins + teamB.breakdownTies / 2))
            {
                if(teamA.pointsFor > teamB.pointsFor)
                    return -1;
                else
                    return 1;
            }

            else
            {
                if ((teamA.breakdownWins + teamA.breakdownTies / 2)  > (teamB.breakdownWins + teamB.breakdownTies / 2))
                    return -1;
                else
                    return 1;
            }

        }
    };
    
    public static Comparator<Team> PointsForComparator = new Comparator<Team>()
    {

        @Override
        public int compare(Team teamA, Team teamB)
        {

            if(teamA.pointsFor > teamB.pointsFor)
                return -1;
            else
                return 1;

        }
    };
    
    public static Comparator<Team> PowerRankingsComparator = new Comparator<Team>()
    {

        @Override
        public int compare(Team teamA, Team teamB)
        {

            if(teamA.getRank() + teamA.getBreakdownRank() + teamA.getPointsRank()
                    < teamB.getRank() + teamB.getBreakdownRank() + teamB.getPointsRank())
                return -1;
            else
                return 1;

        }
    };
}
