import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Verification 
{
    boolean         running;

    public ArrayList<PolyCurve> curves = null;
    public FreeCellSpace   freeCells;
    
    public int             currentStep;
    public double          epsilon;
    public int             cellwidth, cellheight;

    public int             freeDisplay;
    public BufferedWriter  bw = null;
    
    private char phase;
    private Runtime r;
    private Connection connection = null;
    private String TruthFilePath = "/cs/cvs/antpaths/data/antsdata/splited3/";
	static double count1 = 0.0;
	public static void main(String args[]) {
		count1 = Double.parseDouble(args[2]);
//		System.out.println(count);
		if (args.length < 2) {
			System.out.println("Usage: java Frechet inputFilename acceptableRange");
		}
		else {
			new Verification(args[0], Integer.parseInt(args[1]));			
		}
	}

	public Verification(String input, int rang) {
        currentStep = 0;
        epsilon = 50;
        cellwidth = 40;
        cellheight = 40;
        freeDisplay = 0;
        
    	curves = new ArrayList<PolyCurve>();
	String [] str = input.split("/");

	int ant_id = getAntID(str[str.length - 1]);
	int video_id = getVideoID(str[str.length - 1]);
	phase = getPhase(str[str.length - 1]);

	if (ant_id <= 0 || ant_id > 50) {
		System.out.println("Invalid ant id");
		System.exit(2);
	}
	System.out.println("Load user input file: "+input);
    	readInputFile(input);
	System.out.println("Load ground truth file: "+TruthFilePath+"video"+(video_id+1)+"_ant_"+(ant_id)+"_"+phase+".csv");
    	loadTruthFile(TruthFilePath+"video"+(video_id+1)+"_ant_"+(ant_id)+"_"+phase+".csv");

	try {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://mysql.cs.arizona.edu/xuyunhao_angryant","angryant", "7yet2go");
	} catch(Exception e) {
		System.out.println("Error in connecting database");
		e.printStackTrace();
                System.exit(3);
	}
//    	long startTime = System.currentTimeMillis();

        boolean b = false;

        FreeCellSpace fc = new FreeCellSpace();
        fc.resetCriticalValues();

//        while (curves.size() > 1) {
            PolyCurve c1 = curves.remove(0);
            PolyCurve c2 = curves.remove(0);
            System.out.println(c1.getSize()+" "+c2.getSize());
            while (fc.computeCriticalValues(c1, c2) == 0);
            
            b = false;
//            int i = 0;
            int i = findMed(fc, 0, fc.getNumberOfCriticalValues()-1, c1, c2);
//            for ( ; i < fc.getNumberOfCriticalValues() && !b; i++)
//            {
//                double epsln = fc.getEpsilon(i);
//                fc.computeFreeSpace(c1, c2, epsln);
//                fc.computeSolution();
//
//                if (fc.solution.size() > 0) 
//                    b = true;
//            }
            if ( i >= fc.getNumberOfCriticalValues()) {
                i = fc.getNumberOfCriticalValues()-1;
            }
//            double epsln = fc.getEpsilon(i);
//            fc.computeFreeSpace(c1, c2, epsln);
//            fc.computeSolution();
//            PolyCurve med = fc.getMedian();
//            med.reverse();
//            System.out.println("Compute epsilon value: " + fc.getEpsilon(i));
	    double result = fc.getEpsilon(i);
//	    double dis1 = distance(c1.getPoint(0),c2.getPoint(0));
//	    double dis2 = distance(c1.getPoint(c1.getSize()-1), c2.getPoint(c2.getSize()-1));
//	    if (video_id == 3) {
//		dis2 = distance(c1.getPoint(c1.getSize()-3), c2.getPoint(c2.getSize()-1));
//	    }
//	    double result = (dis1 > dis2) ? dis1 : dis2;
            System.out.println("Compute distance value: " + result);
	    if (result > rang * count1 || Double.compare(result, Double.NaN)==0) {
		System.out.println("Path reject!");
//		dbUpdate(video_id, ant_id, phase);
		System.exit(1);
	    }
	    else {
		System.out.println("Path accept!");
	    }
//            curves.add(med);
//            System.gc();
//        }
//        long stopTime = System.currentTimeMillis();
//        long cost = stopTime - startTime;
//        System.out.println("" + cost/1000.0);
//        writefile(input);
    }

    private double distance(Point p1, Point p2) {
	return p1.distance(p2);
    }
    private void dbUpdate(int video_id, int ant_id, char phase) {
	Statement s = null;
	ResultSet rs1 = null;
	System.out.println("Update database");
	int times = 0;
	try {
		s = connection.createStatement();

		String query1 = "SELECT `picked` FROM `ant` "
			+ "WHERE "
			+ "`vid`=" + video_id + " AND "
			+ "`aid`=" + ant_id + " AND "
			+ "`vid2`=\'" + phase+"\'";

		rs1 = s.executeQuery(query1);
		if (s == null) throw new Exception("createStatement failed");
	
		int size = 0;
		while (rs1.next()) {
			times = rs1.getInt("picked");
			size++;
		}
		if (size == 0) {
			System.out.println("Not found!");
		}
//		times = times -1;
		String query2 = "UPDATE `ant` SET `picked`=" + (times -1)
				+ " WHERE `vid`=" + video_id 
				+ " AND `aid`=" + ant_id 
				+ " AND `vid2`=\'" + phase+"\'";
		s.executeUpdate(query2);		

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (rs1 != null)	rs1.close();
//			if (rs2 != null)	rs2.close();
			if (s != null) s.close();
			if (connection != null) connection.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
//	System.exit(1);
    }

    private int getAntID(String str) {
	int id = 0;
	String [] substring = str.split("_");
	String antID = substring[1].substring(1,substring[1].length());
	id = Integer.parseInt(antID);
	return id;
    }

    private int getVideoID(String str) {
	int id = 0;
	String [] substring = str.split("_");
	String videoID = substring[0].substring(1,substring[0].length()-1);
	id = Integer.parseInt(videoID);
	return id;
    }

    private char getPhase(String str) {
        char id;
	String [] substring = str.split("_");
	id = substring[0].charAt(substring[0].length()-1);
	return id;
    }

	private int findMed(FreeCellSpace fc, int s, int l, PolyCurve c1, PolyCurve c2) {
		if (s == l) {
			return s;
		}
		else if (s + 1 == l) {
			double epsln = fc.getEpsilon(s);
			fc.computeFreeSpace(c1, c2, epsln);
			fc.computeSolution();
			if (fc.solution.size() > 0) {
				return s;
			}
			else return l;
		}
//		int i = s;
//		int j = l;
//		while(i!=j){
                double epsln = fc.getEpsilon((s+l)/2);
		fc.computeFreeSpace(c1, c2, epsln);
       	        fc.computeSolution();
		if (fc.solution.size() > 0) {
//			j = (i+j)/2;
			return findMed(fc, s, (l+s)/2 , c1, c2);
		}
		else {
//			i = (i+j)/2;
			return findMed(fc, (l+s)/2, l, c1, c2);
		}
//		}
//		return i;
	}
//    private void writefile() {
//        try {
//            bw.write(""+curves.get(0).getSize());
//            bw.newLine();
//            for (int i=0; i<curves.get(0).getSize(); i++) {
//                String str = "";
//                str += curves.get(0).getPoint(i).getX() + " ";
//                str += curves.get(0).getPoint(i).getY();
//                bw.write(str);
//                bw.newLine();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (bw != null) {
//                    bw.flush();
//                    bw.close();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
    
    private void readInputFile(String input) {
    	try {
    		
    		FileInputStream fstream = new FileInputStream(input);
    		
    		DataInputStream in = new DataInputStream(fstream);
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		
    		String strLine;
    		
    		strLine = br.readLine();
		if (strLine.compareTo("process") == 0) {
			System.out.println("already processed");
			System.exit(0);
		}
		String [] strPath = strLine.split("/");
		int nPoint = strPath.length;
       		PolyCurve pc = new PolyCurve(Color.blue);
                if (phase == 'a') {
                    System.out.println(phase);
    		for(int i = 2; i <= 18; i++) {
    			strLine = strPath[i];
			String Points[] = Pattern.compile(",").split(strLine);
    			Double x = Double.parseDouble(Points[1])*count1;
    			Double y = Double.parseDouble(Points[2])*count1;
    			Point p = new Point();
    			p.setLocation(x, y); 
    			pc.addPoint(p);
    		}
                }
                else {
                for(int i = 18; i <= 34; i++) {
                    strLine = strPath[i];
                    String Points[] = Pattern.compile(",").split(strLine);
                    Double x = Double.parseDouble(Points[1])*count1;
                    Double y = Double.parseDouble(Points[2])*count1;
                    Point p = new Point();
                    p.setLocation(x,y);
                    pc.addPoint(p);
                }
                }
		curves.add(pc);
    		in.close();
    	} catch (Exception e) {
    		e.printStackTrace();
		System.exit(2);
    	}
		
	}

    private void loadTruthFile(String input) {
    	try {
    		
    		FileInputStream fstream = new FileInputStream(input);
    		
    		DataInputStream in = new DataInputStream(fstream);
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		
    		String strLine;
    		
       		PolyCurve pc = new PolyCurve(Color.blue);
                int count = 17;
                if (phase == 'a') {
                    System.out.println(phase);
      		while(count > 0) {
                        strLine = br.readLine();
                        String Points[] = Pattern.compile(" ").split(strLine);
			Double x = (Double.parseDouble(Points[0]))*count1;
			Double y = (Double.parseDouble(Points[1]))*count1;
			Point p = new Point();
			p.setLocation(x, y); 
			pc.addPoint(p);
                        count--;
		}
                }
                else {
      		while(count > 1) {
                        strLine = br.readLine();
                        count --;
		}
                count = 17;
    		while((strLine = br.readLine()) != null && count > 0) {
			String Points[] = Pattern.compile(" ").split(strLine);
			Double x = Double.parseDouble(Points[0])*count1;
			Double y = Double.parseDouble(Points[1])*count1;
			Point p = new Point();
			p.setLocation(x, y); 
			pc.addPoint(p);
                        count--;
		}
                }
		curves.add(pc);
    		in.close();
    	} catch (Exception e) {
    		e.printStackTrace();
		System.exit(2);
    	}
		
	}

}
