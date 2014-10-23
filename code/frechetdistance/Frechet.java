import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Frechet 
{
    boolean         running;

    public ArrayList<PolyCurve> curves = null;
    public FreeCellSpace   freeCells;
    
    public int             currentStep;
    public double          epsilon;
    public int             cellwidth, cellheight;

    public int             freeDisplay;
    public BufferedWriter  bw = null;
    
    private Runtime r;
	public static void main(String args[]) {
		if (args.length < 2) {
			System.out.println("Usage: java Frechet inputFilename outputFilename");
		}
		else {
			new Frechet(args[0], args[1]);			
		}
	}

	public Frechet(String input, String output) {
        currentStep = 0;
        epsilon = 50;
        cellwidth = 40;
        cellheight = 40;
        freeDisplay = 0;
        
    	curves = new ArrayList<PolyCurve>();
    	readfile(input, output);
    	
    	long startTime = System.currentTimeMillis();

        boolean b = false;

        FreeCellSpace fc = new FreeCellSpace();
        fc.resetCriticalValues();

        while (curves.size() > 1) {
            PolyCurve c1 = curves.remove(0);
            PolyCurve c2 = curves.remove(0);
            while (fc.computeCriticalValues(c1, c2) == 0);
            
            b = false;

            int i = findMed(fc, 0, fc.getNumberOfCriticalValues()-1, c1, c2);

            double epsln = fc.getEpsilon(i);
            fc.computeFreeSpace(c1, c2, epsln);
            fc.computeSolution();
            PolyCurve med = fc.getMedian();

            System.out.println("med: "+med.getSize() + " -- epsilon: "+fc.getEpsilon(i));
            curves.add(med);
            System.gc();
        }
        long stopTime = System.currentTimeMillis();
        long cost = stopTime - startTime;
        System.out.println("" + cost/1000.0);
        writefile();
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

                double epsln = fc.getEpsilon((s+l)/2);
		fc.computeFreeSpace(c1, c2, epsln);
       	        fc.computeSolution();
		if (fc.solution.size() > 0) {

			return findMed(fc, s, (l+s)/2 , c1, c2);
		}
		else {

			return findMed(fc, (l+s)/2, l, c1, c2);
		}

	}
    private void writefile() {
        try {
            bw.write(""+curves.get(0).getSize());
            bw.newLine();
            for (int i=0; i<curves.get(0).getSize(); i++) {
                String str = "";
                str += curves.get(0).getPoint(i).getX() + " ";
                str += curves.get(0).getPoint(i).getY();
                bw.write(str);
                bw.newLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void readfile(String input, String output) {
    	try {
    		
    		FileInputStream fstream = new FileInputStream(input);
    		
    		DataInputStream in = new DataInputStream(fstream);
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		
    		bw = new BufferedWriter(new FileWriter(output));
    		
    		String strLine;
    		
    		strLine = br.readLine();
    		int nWaypoint = Integer.parseInt(strLine);
    		for(int i = 0; i < nWaypoint; i++) {
    			strLine = br.readLine();
    		}
		Point startP = null;
		Point endP = null;
		if (nWaypoint > 2) {
			strLine = br.readLine();
			String pts[] = Pattern.compile(" ").split(strLine);
			Double x = Double.parseDouble(pts[0]);
	    		Double y = Double.parseDouble(pts[1]);
			startP = new Point();
    			startP.setLocation(x, y); 

			strLine = br.readLine();
			pts = Pattern.compile(" ").split(strLine);
			x = Double.parseDouble(pts[0]);
	    		y = Double.parseDouble(pts[1]);
			endP = new Point();
    			endP.setLocation(x, y); 
		}
		else {
			strLine = br.readLine();
			strLine = br.readLine();
		}
    		strLine = br.readLine();
    		int nPaths = Integer.parseInt(strLine);
    		for (int n = 0; n < nPaths; n++) {
        		strLine = br.readLine();
        		PolyCurve pc = new PolyCurve(Color.blue);
    			int nPointsP = Integer.parseInt(strLine);
			if (nWaypoint > 2) {
				pc.addPoint(startP);
			}
    			for (int i = 0; i < nPointsP; i++) {
    				strLine = br.readLine();
    				String Points[] = Pattern.compile(" ").split(strLine);
    				Double x = Double.parseDouble(Points[0]);
    				Double y = Double.parseDouble(Points[1]);
    				Point p = new Point();
    				p.setLocation(x, y); 
    				pc.addPoint(p);
	    		}
			if (nWaypoint > 2) {
				pc.addPoint(endP);
			}
    			curves.add(pc);
    		}
    		in.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		
	}
}
