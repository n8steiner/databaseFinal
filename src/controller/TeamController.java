package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import view.PlayerView;
import bo.Player;
import bo.PlayerCareerStats;
import bo.PlayerSeason;
import dataaccesslayer.HibernateUtil;

public class PlayerController extends BaseController {

    //changed
    @Override
    public void init(String query) {
        System.out.println("building dynamic html for team");
        view = new TeamView();
        process(query);
    }

    //unchanged as of yet
    @Override
    protected void performAction() {
        String action = keyVals.get("action");
        System.out.println("teamcontroller performing action: " + action);
        if (action.equalsIgnoreCase(ACT_SEARCHFORM)) {
            processSearchForm();
        } else if (action.equalsIgnoreCase(ACT_SEARCH)) {
            processSearch();
        } else if (action.equalsIgnoreCase(ACT_DETAIL)) {
            processDetails();
        } 
    }

    //unchanged as of yet, should not need changes
    protected void processSearchForm() {
        view.buildSearchForm();
    }

    protected final void processSearch() {
        String name = keyVals.get("name");
        if (name == null) {
            return;
        }
        String v = keyVals.get("exact");
        boolean exact = (v != null && v.equalsIgnoreCase("on"));
        List<Player> bos = HibernateUtil.retrieveTeamsByName(name, exact);
        view.printSearchResultsMessage(name, exact);
        buildSearchResultsTableTeam(bos);
        view.buildLinkToSearch();
    }

    //builds results
    private void buildSearchResultsTableTeam(List<Team> bos){
        // need a row for the table headers
        String[][] table = new String[bos.size() + 1][5];
        table[0][0] = "Id";
        table[0][1] = "Name";
        table[0][2] = "League";
        table[0][3] = "Year Founded";
        table[0][4] = "Most Recent Year";

        //now fill in data
        for (int i = 0; i < bos.size(); i++) {
            Team t = bos.get(i);
            String tid = t.getId().toString();
            table[i + 1][0] = view.encodeLink(new String[]{"id"}, new String[]{tid}, tid, SSP_TEAM);
            table[i + 1][1] = t.getName();
            table[i + 1][2] = t.getLeague();
            table[i + 1][3] = t.getYearLast();
            table[i + 1][4] = t.getYearFounded();
        }
        view.buildTable(table);
    }

    private void buildSearchResultsTableTeamDetail(Team t) {
    	Set<TeamSeason> seasons = t.getSeasons();
    	List<TeamSeason> list = new ArrayList<TeamSeason>(seasons);
    	Collections.sort(list, TeamSeason.teamSeasonsComparator);
    	// build 2 tables.  first the team details, then the season details
        // need a row for the table headers
        String[][] teamTable = new String[2][4];
        teamTable[0][0] = "Name";
        teamTable[0][1] = "League";
        teamTable[0][2] = "Year Founded";
        teamTable[0][3] = "Most Recent Year";
        teamTable[1][0] = t.getName();
        teamTable[1][1] = t.getLeague();
        teamTable[1][2] = t.getYearFounded();
        teamTable[1][3] = t.getYearLast();

        view.buildTable(teamTable);
        // now for seasons
        String[][] seasonTable = new String[seasons.size()+1][6];
        seasonTable[0][0] = "Year";
        seasonTable[0][1] = "Games Played";
        seasonTable[0][2] = "Wins";
        seasonTable[0][3] = "Losses";
        seasonTable[0][4] = "Rank";
        seasonTable[0][5] = "Attendance";
        int i = 0;
        for (TeamSeason ts: list) {
            i++;
            seasonTable[i][0] = ts.getYear().toString();
        	seasonTable[i][1] = ts.getGamesPlayed().toString();
        	seasonTable[i][2] = ts.getWins().toString();
        	seasonTable[i][3] = ts.getLosses().toString();
        	seasonTable[i][4] = ts.getRank().toString();
        	seasonTable[i][5] = ts.getTotalAttendance().toString();
        }
        view.buildTable(seasonTable);
    }

    private void buildRosterTable(TeamSeason ts){
        //pull in data and declare variables
        Set<Player> players = ts.getPlayers();
        double payrollTotal = 0;
        Set<PlayerSeason> ps;
        String tname = ts.getName().toString();

        //build team table
        String[][] teamTable = new String[2][4];
        teamTable[0][0] = "Name";
        teamTable[0][1] = "League";
        teamTable[0][2] = "Year";
        teamTable[0][3] = "Player Payroll";

        teamTable[1][0] = view.encodeLink(new String[]{"id"}, new String[]{tname}, tname, ACT_DETAIL, SSP_TEAM);
        teamTable[1][1] = ts.getTeam().getLeague();
        teamTable[1][2] = ts.getYear().toString();
        //set payroll total after iteraton

        //build player table
        String[][] playerTable = new String [players.size() + 1][3];
        playerTable[0][0] = "Name";
        playerTable[0][1] = "Games Played";
        playerTable[0][2] = "Salary";


        //get all player seasons that match up with this team season
        for(Player p: players){
            for(PlayerSeason ps: p.getSeasons(){
                if(ps.getTeam() == ts.getTeam() && ps.getYear() == ts.getYear()){
                    ps.add(ps);
                }
            }
        }

        //populate player table
        for (PlayerSeason ps: list) {
            i++;
            playerTable[i][0] = ps.getPlayer().getName();
            playerTable[i][1] = ps.getGamesPlayed().toString();
            playerTable[i][2] = "$" + ps.getSalary().toString();
            payrollTotal += ps.getSalary();
        }

        //now set payroll total
        teamTable[1][3] = "$" + payrollTotal.toString();
    }

}