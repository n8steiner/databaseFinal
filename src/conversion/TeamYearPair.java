package conversion;

public class TeamYearPair {
	private String year;
	private String tid;
	
	public TeamYearPair(String id, String y) {
		tid = id;
		year = y;
	}
	
	public String getTeamID() {
		return tid;
	}
	
	public String getYear() {
		return year;
	}
}
