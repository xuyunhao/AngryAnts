package model;

public class Profile {

	int vid, ant, highestMult, highestScore;
	String user, vid2;

	public Profile(String u, int vidNum, String vidNum2, int antNum, int score, int mult) {
		user = u;
		vid = vidNum;
		vid2 = vidNum2;
		ant = antNum;
		highestMult = mult;
		highestScore = score;
	}

	public String getName() {
		return user;
	}

	public int getVid() {
		return vid;
	}

	public String getvid2() {
		return vid2;
	}
	
	public int getAnt(){
		return ant;
	}
	
	public int getScore(){
		return highestScore;
	}
	
	public int getMult(){
		return highestMult;
	}

}
