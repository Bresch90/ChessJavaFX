package application;
import java.util.Arrays;

public class Piece {
	
	private int team;
	private String kind;
	private String imagePath;
	private String loc;
	
	public Piece(int team, String kind, int x, int y) {
		this.team = team;
		String color = (team == 0 ? "l" : "d");
		this.loc = x + " " + y;
		
		//Determining the kind and imagefile to use
		kind = kind.toLowerCase();
		this.kind = kind;
		
		switch (kind) {
			case "king": 	kind = "k"; break;
			case "queen":	kind = "q"; break;	
			case "bishop":  kind = "b"; break;
			case "knight":  kind = "n"; break;
			case "rook":	kind = "r"; break;
			case "pawn":	kind = "p"; break;
			default: throw new IllegalArgumentException("Unexpected value: " + kind);
		}
		this.imagePath = "File:" + kind + color + "t60.png";
	}

	public int getTeam() {
		return team;
	}

	public String getKind() {
		return kind;
	}

	public String getImagePath() {
		return imagePath;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(int x, int y) {
		this.loc = x + " " + y;
	}
	public void setLoc(String x, String y) {
		this.setLoc(Integer.parseInt(x), Integer.parseInt(y));
	}
	
	//TODO should know all the moves i can do etc.
	
	
	
	
	
}
