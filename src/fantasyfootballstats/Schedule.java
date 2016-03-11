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
public class Schedule 
{
    Team[] teams;
    int numOfWeeks;
    
    public Schedule(int numOfWeeks, Team[] teams)
    {
        this.numOfWeeks = numOfWeeks;
        this.teams = teams;
    }
    
    
}
