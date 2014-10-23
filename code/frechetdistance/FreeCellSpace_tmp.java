import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;
import java.util.Arrays;

class FreeCellSpace
{
   PolyCurve   P, Q;
   FreeCell    Array[];
   Object[]    CriticalValues;   
   int         nbOfCriticalValues;
   public Vector      solution;
   int         currentStep;
   int         ii, jj;
   Vector      listOfCriticalValues;
   Point       scanPointA, scanPointB;

   public FreeCellSpace()
   {
      solution = new Vector();
      nbOfCriticalValues = 0;
   }

   public void computeFreeSpace(PolyCurve P, PolyCurve Q, double epsilon)
   {
      int i, j;

      Point p1, p2;
      Point q1, q2;

      this.P = P;
      this.Q = Q;

      Array = new FreeCell[(P.getSize()) * (Q.getSize())];
      
      for (j = 0; j < Q.getSize() - 1; j++)
      {
	 for (i = 0; i < P.getSize() - 1; i++)
	 {
	    p1 = P.getPoint(i);
            q1 = Q.getPoint(j);

	    p2 = P.getPoint(i + 1);
            q2 = Q.getPoint(j + 1);
            
            Array[j * P.getSize() + i] = new FreeCell(p1, p2, q1, q2, epsilon);
	 }
      }

      if ((Q.getSize() > 1) && (P.getSize() > 1))
      {
         Array[0].l = new FreeSeg(Array[0].l1, Array[0].l2);
         Array[0].b = new FreeSeg(Array[0].b1, Array[0].b2);

         if ((Array[0].l.x != 0) || (Array[0].b.x != 0))
         {
            Array[0].l = new FreeSeg(-1.0, -1.0);
            Array[0].b = new FreeSeg(-1.0, -1.0);
         }

         Array[0].t = new FreeSeg(-1.0, -1.0);
         Array[0].r = new FreeSeg(-1.0, -1.0);
      
         if (Array[0].l.Empty() == 0)
         {
	    Array[0].t.Union(0, 1);
            Array[0].r.Union(Array[0].l.x, 1.0);
         }
         if (Array[0].b.Empty() == 0)
         {
	    Array[0].r.Union(0, 1);
            Array[0].t.Union(Array[0].b.x, 1.0);
         }

	 Array[0].t.Inter(Array[0].t1, Array[0].t2);
         Array[0].r.Inter(Array[0].r1, Array[0].r2);
      }

      if (Q.getSize() > 1)
      {      
         for (i = 1; i < P.getSize() - 1; i++)
         {
	    Array[i].l = Array[i-1].r; 
	    Array[i].b = new FreeSeg(-1.0, -1.0);
	    Array[i].t = new FreeSeg(-1.0, -1.0);
	    Array[i].r = new FreeSeg(-1.0, -1.0);

	    if (Array[i].l.Empty() == 0)
	    {
	       Array[i].t.Union(0, 1);
               Array[i].r.Union(Array[i].l.x, 1.0);
	    }

            Array[i].t.Inter(Array[i].t1, Array[i].t2);
            Array[i].l.Inter(Array[i].l1, Array[i].l2);
            Array[i].r.Inter(Array[i].r1, Array[i].r2);
         }
      }

      if (P.getSize() > 1)
      {
         for (j = 1; j < Q.getSize() - 1; j++)
         {
	    Array[j * P.getSize()].l = new FreeSeg(-1.0, -1.0);
	    Array[j * P.getSize()].b = Array[(j - 1) * P.getSize()].t;
	    Array[j * P.getSize()].t = new FreeSeg(-1.0, -1.0);
	    Array[j * P.getSize()].r = new FreeSeg(-1.0, -1.0);

	    if (Array[j * P.getSize()].b.Empty() == 0)
	    {
	       Array[j * P.getSize()].r.Union(0, 1);
               Array[j * P.getSize()].t.Union(Array[j * P.getSize()].b.x, 1.0);
	    }

            Array[j * P.getSize()].t.Inter(Array[j * P.getSize()].t1, Array[j * P.getSize()].t2);
            Array[j * P.getSize()].l.Inter(Array[j * P.getSize()].l1, Array[j * P.getSize()].l2);
            Array[j * P.getSize()].r.Inter(Array[j * P.getSize()].r1, Array[j * P.getSize()].r2);
         }
      }

      for (j = 1; j < Q.getSize() - 1; j++)
      {
         for (i = 1; i < P.getSize() - 1; i++)
         {
	    Array[j * P.getSize() + i].l = Array[j * P.getSize() + i - 1].r;
	    Array[j * P.getSize() + i].b = Array[(j - 1) * P.getSize() + i].t;
	    Array[j * P.getSize() + i].t = new FreeSeg(-1.0, -1.0);
	    Array[j * P.getSize() + i].r = new FreeSeg(-1.0, -1.0);

	    if (Array[j * P.getSize() + i].b.Empty() == 0)
	    {
	       Array[j * P.getSize() + i].r.Union(0, 1);
               Array[j * P.getSize() + i].t.Union(Array[j * P.getSize() + i].b.x, 1.0);
	    }

	    if (Array[j * P.getSize() + i].l.Empty() == 0)
	    {
	       Array[j * P.getSize() + i].t.Union(0, 1);
               Array[j * P.getSize() + i].r.Union(Array[j * P.getSize() + i].l.x, 1.0);
	    }

            Array[j * P.getSize() + i].b.Inter(Array[j * P.getSize() + i].b1, Array[j * P.getSize() + i].b2);
            Array[j * P.getSize() + i].t.Inter(Array[j * P.getSize() + i].t1, Array[j * P.getSize() + i].t2);
            Array[j * P.getSize() + i].l.Inter(Array[j * P.getSize() + i].l1, Array[j * P.getSize() + i].l2);
            Array[j * P.getSize() + i].r.Inter(Array[j * P.getSize() + i].r1, Array[j * P.getSize() + i].r2);
	 }
      }
   }
    
   public void computeSolution()
   {
       solution.removeAllElements();

       double x, y;

       if ((Q.getSize() > 1) && (P.getSize() > 1))
       {
	  if ((Array[(Q.getSize() - 2) * P.getSize() + P.getSize() - 2].r.y == 1) &&
              (Array[(Q.getSize() - 2) * P.getSize() + P.getSize() - 2].t.y == 1))
	  {
	      // We have a solution
	      solution.addElement(new Double(P.getSize() - 1));
	      solution.addElement(new Double(Q.getSize() - 1));

              x = P.getSize() - 1;
              y = Q.getSize() - 1;
 
              int i = P.getSize() - 2;
              int j = Q.getSize() - 2;
              
	      while ( !((i == 0) && (j == 0)) )
	      {
		 if ((Array[j * P.getSize() + i].l.Empty() == 0) && (j + Array[j * P.getSize() + i].l.x <= y))
	         {
		    y = j + Array[j * P.getSize() + i].l.x;

                    solution.addElement(new Double(i));
		    solution.addElement(new Double(y));
                    i--;		
	         }
                 else if ((Array[j * P.getSize() + i].b.Empty() == 0) && (i + Array[j * P.getSize() + i].b.x <= x))
	         {
		    x = i + Array[j * P.getSize() + i].b.x;

                    solution.addElement(new Double(x));
		    solution.addElement(new Double(j));
                    j--;
	         }
	      }

	      solution.addElement(new Double(0));
	      solution.addElement(new Double(0));
	  }
       }
   }

   public void resetCriticalValues()
   {
       listOfCriticalValues = new Vector();
       currentStep = 0;
       ii = 0;
       jj = 0;  
       nbOfCriticalValues = 0;
   }

   public Point GetScanPointA()
   {
       return scanPointA;
   }

   public Point GetScanPointB()
   {
       return scanPointB;
   }

   public int computeCriticalValues(PolyCurve P, PolyCurve Q)
   {
      Point p1, p2, p3, p4;
      double dx, dy;
      double t;
      double epsilon = 0.0;
      int i, j;
      int finished = 0;

      if ((P.getSize() > 1) && (Q.getSize() > 1))
      {
	 // case a)

	 if (currentStep == 0)
	 {
            p1 = P.getPoint(0);
            p2 = Q.getPoint(0);
     
	    epsilon = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));

	    scanPointA = p1;
	    scanPointB = p2;
      
            listOfCriticalValues.addElement(new Double(epsilon));
            currentStep++;
	    nbOfCriticalValues++;
	 }
         
         if (currentStep == 1)
	 {
            p1 = P.getPoint(P.getSize() - 1);
            p2 = Q.getPoint(Q.getSize() - 1);
     
            epsilon = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));

            scanPointA = p1;
            scanPointB = p2;
         
            listOfCriticalValues.addElement(new Double(epsilon));
            currentStep++;
	    nbOfCriticalValues++;
	 }
 
         // case b)

         if (currentStep == 2)
         {
            p3 = Q.getPoint(jj);

            scanPointA = p3;

	    {         
	       p1 = P.getPoint(ii);
               p2 = P.getPoint(ii + 1);

               dx = p2.x - p1.x;
               dy = p2.y - p1.y;
               t = (dx * (p3.x - p1.x) + dy * (p3.y - p1.y)) / (dx * dx + dy * dy);

               if (t < 0)
	       {
		  scanPointB = p1;
                  epsilon = Math.sqrt((p1.x - p3.x) * (p1.x - p3.x) + (p1.y - p3.y) * (p1.y - p3.y));
	       }
	       else if (t > 1)
	       {
		  scanPointB = p2;
		  epsilon = Math.sqrt((p2.x - p3.x) * (p2.x - p3.x) + (p2.y - p3.y) * (p2.y - p3.y));
	       } 
               else
 	       {
		  scanPointB = new Point((int)(p1.x + t * dx),  (int)(p1.y + t * dy));

                  epsilon = Math.sqrt(((p1.x + t * dx) - p3.x) * ((p1.x + t * dx) - p3.x) + 
                                      ((p1.y + t * dy) - p3.y) * ((p1.y + t * dy) - p3.y));
	       }
    
               listOfCriticalValues.addElement(new Double(epsilon));
	       nbOfCriticalValues++;
	    }
            ii++;

            if (ii >= (P.getSize() - 1))
	    {
		ii = 0;
                jj++;
	    }
            if (jj >= Q.getSize())
	    {
                currentStep++;
		ii = 0;
                jj = 0;
	    }
	 }       

         if (currentStep == 3)
         {
            p3 = P.getPoint(jj);

	    scanPointA = p3;

	    {         
	       p1 = Q.getPoint(ii);
               p2 = Q.getPoint(ii + 1);

               dx = p2.x - p1.x;
               dy = p2.y - p1.y;
               t = (dx * (p3.x - p1.x) + dy * (p3.y - p1.y)) / (dx * dx + dy * dy);

               if (t < 0)
	       {
                  epsilon = Math.sqrt((p1.x - p3.x) * (p1.x - p3.x) + (p1.y - p3.y) * (p1.y - p3.y));
		  scanPointB = p1;
	       }
	       else if (t > 1)
	       {
		  epsilon = Math.sqrt((p2.x - p3.x) * (p2.x - p3.x) + (p2.y - p3.y) * (p2.y - p3.y));  
		  scanPointB = p2;
	       } 
               else
 	       {
                  epsilon = Math.sqrt(((p1.x + t * dx) - p3.x) * ((p1.x + t * dx) - p3.x) + 
                                      ((p1.y + t * dy) - p3.y) * ((p1.y + t * dy) - p3.y));      
		  scanPointB = new Point((int)(p1.x + t * dx), (int)(p1.y + t * dy));
	       }
    
               listOfCriticalValues.addElement(new Double(epsilon));
	       nbOfCriticalValues++;
	    }

            ii++;

            if (ii >= (Q.getSize() - 1))
	    {
		ii = 0;
                jj++;
	    }
            if (jj >= P.getSize())
	    {
                currentStep++;
		ii = 0;
                jj = 0;
	    }

	 }        

         // case c)

      }      
      
      if (currentStep == 4)
      {
         CriticalValues = listOfCriticalValues.toArray();
         Arrays.sort(CriticalValues);
         finished = 1;
      }

      return finished;
   }

   public int getNumberOfCriticalValues()
   {
      return nbOfCriticalValues;
   }

   public double getEpsilon(int index)
   {
      return ((Double)(CriticalValues[index])).doubleValue();
   }
   
// /Users/dennis/Downloads/test.txt
   public String getSolution_toString(int x, int y, int CW, int CH)
   {
	   String result = "";
		 computeSolution();

         if (solution.size() > 0)
         {
            double x1, y1, x2, y2;

            x1 = ((Double)solution.elementAt(0)).doubleValue();
            y1 = ((Double)solution.elementAt(1)).doubleValue();

            for (int i = 1; i < (solution.size() / 2); i++)
            {
            	x2 = ((Double)solution.elementAt(i * 2)).doubleValue();
            	y2 = ((Double)solution.elementAt(i * 2 + 1)).doubleValue();

//            	result += ("Line: (" + (int)(x + x1 * CW) + ", "
//            			+ (int)(y + (Q.getSize() - 1 - y1) * CH) + ") -- (" 
//            			+ (int)(x + x2 * CW) + ", "
//            			+ (int)(y + (Q.getSize() - 1 - y2) * CH) + ");\n");
            	result += "line: [" + x1 + ", "+ y1 + "] -- [" + x2 + ", " + y2+"]";
            	
            	Random random = new Random();
//            	int ran = random.nextInt(3)+2;
            	int ran = 2;
            	ArrayList<Double> percents = new ArrayList<Double>();
            	result += " == " + ran + " random points:\n"; 
            	for (int j = 0; j < ran ; j++) {
            		double percent = random.nextDouble();
            		percents.add(percent);
            	}
            	Collections.sort(percents);
            	int j = 0;
            	for (Double percent: percents) {
            		double x3, y3;
            		
            		x3 = x1 + percent * (x2-x1);
            		y3 = y1 + percent * (y2-y1);
            		result += ""+percent+ " -- "+"x1:"+x1+" x2:"+x2 + " x3:"+x3 + "\n";
            		Point random_P = computePoint(P, percent, x3);
            		Point random_Q = computePoint(Q, percent, y3);
            		Point median = computeMedian(random_P, random_Q);
            		result += j +" P: ("+random_P.getX()+", "+random_P.getY()+") -- Q: (" 
            			+ random_Q.getX()+", "+random_Q.getY()+") -- Median: ("
            			+ median.getX() + ", "+ median.getY()+")\n";
            		j ++;
            	}
            	
//            	result += "\n";
               x1 = x2;
               y1 = y2;
            }


	 }
	  return result; 
   }
   
   public PolyCurve getMedian() {
	   PolyCurve result = new PolyCurve(Color.black);
		 computeSolution();

       if (solution.size() > 0)
       {
          double x1, y1, x2, y2;

          x1 = ((Double)solution.elementAt(0)).doubleValue();
          y1 = ((Double)solution.elementAt(1)).doubleValue();
          
          Point end_P = P.getPoint(P.getSize()-1);
          Point end_Q = Q.getPoint(Q.getSize()-1);
          Point end_med = computeMedian(end_P, end_Q);
          
          result.addPoint(end_med);

          for (int i = 1; i < (solution.size() / 2); i++)
          {
          	x2 = ((Double)solution.elementAt(i * 2)).doubleValue();
          	y2 = ((Double)solution.elementAt(i * 2 + 1)).doubleValue();

//          	result += ("Line: (" + (int)(x + x1 * CW) + ", "
//          			+ (int)(y + (Q.getSize() - 1 - y1) * CH) + ") -- (" 
//          			+ (int)(x + x2 * CW) + ", "
//          			+ (int)(y + (Q.getSize() - 1 - y2) * CH) + ");\n");
          	
          	Random random = new Random();
          	int ran = 2;//random.nextInt(2)+1;
//          	ArrayList<Double> percents = new ArrayList<Double>();
          	double percents[] = new double[ran];
          	for (int j = 0; j < ran ; j++) {
          		double percent = random.nextDouble();
//          		percents.add(percent);
          		percents[j] = percent;
          	}
//          	Collections.sort(percents);
          	Arrays.sort(percents);
          	double tmpD = percents[0];
		percents[0] = percents[1];
		percents[1] = tmpD;
		int j = 0;
          	for (Double percent: percents) {
          		double x3, y3;
          		
          		x3 = x1 + percent * (x2-x1);
          		y3 = y1 + percent * (y2-y1);
          		
          		Point random_P = computePoint(P, percent, x3);
          		Point random_Q = computePoint(Q, percent, y3);
          		Point median = computeMedian(random_P, random_Q);
          		if (!result.getPoint(result.getSize()-1).equals(median)) {
          			result.addPoint(median);
          			j ++;
          		}
          	}
          	
//          	result += "\n";
             x1 = x2;
             y1 = y2;
          }
          Point start_P = P.getPoint(0);
          Point start_Q = Q.getPoint(0);
          Point start_med = computeMedian(start_P, start_Q);
          result.addPoint(start_med);

	 }
	  return result; 
   }

   private Point computeMedian(Point random_P, Point random_Q) {
	   Point p = new Point();
	   p.setLocation((double)(random_P.getX()+random_Q.getX())/2, 
			   (double)(random_P.getY()+random_Q.getY())/2);
	   return p;
   }

   private Point computePoint(PolyCurve PC, double percent, double x) {
		double x_p1 = ((Point)(PC.getPoint((int)x+1))).getX();
		double y_p1 = ((Point)(PC.getPoint((int)x+1))).getY();
		double x_p2 = ((Point)(PC.getPoint((int)x))).getX();
		double y_p2 = ((Point)(PC.getPoint((int)x))).getY();
		
		double x_sp = x_p2 + (x_p1 -x_p2) * (x - (int)x);
		double y_sp = y_p2 + (y_p1 -y_p2) * (x - (int)x);
		Point p = new Point();
		p.setLocation(x_sp, y_sp);
		return p;
   }

//// Draw the free cells space
//   public void draw (Graphics g, int x, int y, int CW, int CH, int freeDisplay)
//   {
//      int i, j;      
//
//      for (j = 0; j < Q.getSize() - 1; j++)
//      {
//	 for (i = 0; i < P.getSize() - 1; i++)
//	 {
//	     Array[j * P.getSize() + i].draw(g, x + i * CW, y + (Q.getSize() - 2) * CH - j * CH, CW, CH, freeDisplay);
//	 }
//      }
//
//      if ((Q.getSize() > 1) && (P.getSize() > 1))
//      {
//         g.setColor(Color.green);
//
//         for (j = 0; j < Q.getSize(); j++)
//         {
//            g.drawLine(x, y + CH * j, x + CW * (P.getSize()-1) - 1, y + CH * j); 
//         }
//
//         for (i = 0; i < P.getSize(); i++)
//         {
//            g.drawLine(x + CW * i, y, x + CW * i, y + CH * (Q.getSize()-1) - 1); 
//         }
//
//         g.setColor(Color.black);
//
//	 g.drawLine(x, y + CH * (Q.getSize() - 1) + 10, x + CW * (P.getSize()-1), y + CH * (Q.getSize() - 1) + 10);
//	 g.drawLine(x + CW * (P.getSize()-1), y + CH * (Q.getSize() - 1) + 10, 
//                    x + CW * (P.getSize()-1) - 4, y + CH * (Q.getSize() - 1) + 6);
//	 g.drawLine(x + CW * (P.getSize()-1), y + CH * (Q.getSize() - 1) + 10, 
//                    x + CW * (P.getSize()-1) - 4, y + CH * (Q.getSize() - 1) + 14);
//
//         g.drawBytes("P".getBytes(),0,1, x + CW * (P.getSize()-1) + 5, y + CH * (Q.getSize() - 1) + 14);   
//
//         g.drawLine(x - 10, y + CH * (Q.getSize() - 1), x - 10, y);
//         g.drawLine(x - 10, y, x - 14, y + 4);
//         g.drawLine(x - 10, y, x - 6, y + 4);
//
//         g.drawBytes("Q".getBytes(),0,1, x - 12, y - 4);   
//
//         // We display a possible parametrization (solution)
//         
//         if (solution.size() > 0)
//         {
//            g.setColor (Color.blue);          
//
//            double x1, y1, x2, y2;
//
//            x1 = ((Double)solution.elementAt(0)).doubleValue();
//            y1 = ((Double)solution.elementAt(1)).doubleValue();
//
//            for (i = 1; i < (solution.size() / 2); i++)
//            {
//	       x2 = ((Double)solution.elementAt(i * 2)).doubleValue();
//	       y2 = ((Double)solution.elementAt(i * 2 + 1)).doubleValue();
//
//               g.drawLine ((int)(x + x1 * CW), (int)(y + (Q.getSize() - 1 - y1) * CH), (int)(x + x2 * CW), (int)(y + (Q.getSize() - 1 - y2) * CH));
//
//               x1 = x2;
//               y1 = y2;
//            }
//
//            System.out.println(getSolution_toString(x,y,CW,CH));
//	 }
//      }
//   }
}

class FreeCell
{
    public double b1, b2;
    public double l1, l2;
    public double t1, t2;
    public double r1, r2;

    public FreeSeg l;
    public FreeSeg r;
    public FreeSeg b;
    public FreeSeg t;

    public FreeCell(Point p1, Point p2, Point q1, Point q2, double epsilon)
    {
        FreeSeg fs;

        l = new FreeSeg(-1, -1);
        r = new FreeSeg(-1, -1);
        b = new FreeSeg(-1, -1);
        t = new FreeSeg(-1, -1);
        
        fs = SegmentCircleIntersection(p1, p2, q1, epsilon);
        b1 = fs.x;
        b2 = fs.y;

        fs = SegmentCircleIntersection(p1, p2, q2, epsilon);
        t1 = fs.x;
        t2 = fs.y;

        fs = SegmentCircleIntersection(q1, q2, p1, epsilon);
        l1 = fs.x;
        l2 = fs.y;

        fs = SegmentCircleIntersection(q1, q2, p2, epsilon);   
        r1 = fs.x;
        r2 = fs.y;
    }

    public FreeSeg SegmentCircleIntersection(Point p1, Point p2, Point c, double r)
    {
        double cx, cy;
        double dx, dy;
        double tx, ty;
        double distPt1C;
        double distPt2C;
        double o;
	double i1, i2;

        FreeSeg rv = new FreeSeg(-1, -1);

        cx = c.x;
        cy = c.y;
        dx = p2.x - p1.x;
        dy = p2.y - p1.y;
        tx = p1.x - cx;
        ty = p1.y - cy;

        distPt1C = Math.sqrt((p1.x - cx) * (p1.x - cx) + (p1.y - cy) * (p1.y - cy));
        distPt2C = Math.sqrt((p2.x - cx) * (p2.x - cx) + (p2.y - cy) * (p2.y - cy));

        if (distPt1C <= r && distPt2C <= r)
	{
            // Both pt1 and pt2 are inside the circle
	    rv.x = 0;
            rv.y = 1;
	}
	else
	{
            o = (dx*dx + dy*dy)*r*r - (dx*ty - dy*tx) * (dx*ty - dy*tx);

            if (o < 0)
	    {
                // Both pt1 and pt2 are outside the circle and the corresponding line does
                // not intersect the circle, which means that the segment does not
		rv.x = -1;
                rv.y = -1;
	    }
	    else
	    {
                i1 = ( -(dx*tx + dy*ty) - Math.sqrt(o) ) / ( dx*dx + dy*dy);
                i2 = ( -(dx*tx + dy*ty) + Math.sqrt(o) ) / ( dx*dx + dy*dy);

                if ((i1 >= 0) && (i1 <= 1) && (i2 >= 0) && (i2 <= 1))
		{
                    // Both pt1 and pt2 are outside the circle and the segment intersects the
		    // circle at two points.
		    rv.x = i1;
                    rv.y = i2;
		}
		else
		{
		    if ((i1 >= 0) && (i1 <= 1) && !( (i2 >= 0) && (i2 <= 1) ))
		    {
			// pt1 is outside the circle and pt2 is inside
                        rv.x = i1;
                        rv.y = 1;
		    }

		    if ((i2 >= 0) && (i2 <= 1) && !( (i1 >= 0) && (i1 <= 1) ))
		    {
			// pt1 is inside the circle and pt2 is outside
                        rv.x = 0;
                        rv.y = i2;
		    }
		    
                    if (!( (i2 >= 0) && (i2 <= 1) ) && !( (i1 >= 0) && (i1 <= 1) ))
		    {
                	// pt1 and pt2 are outside the circle and the segment does not 
                        // intersect the circle (even if the corresponding line does)
	        	rv.x = -1;
                        rv.y = -1;
		    } 
		}
	    }
	}

	return rv;
    }  

//    // Draw the free cells space
//    public void draw (Graphics g, int x, int y, int w, int h, int fr)
//    {
//        Polygon polygon = new Polygon();
//
//        g.setColor(Color.red);
//	g.fillRect(x, y, w, h);
//        g.setColor(Color.white);
//
//        if (fr == 0)
//	{
//           if (t1 != -1)
//	   {
//              polygon.addPoint( (int)(x + t1 * w) , y);
//              polygon.addPoint( (int)(x + t2 * w) , y);
//	   }
//
//           if (r1 != -1)
//	   {
//              polygon.addPoint( x + w , (int)(y + h - r2 * h) );
//              polygon.addPoint( x + w , (int)(y + h - r1 * h) );
//	   }
//
//           if (b1 != -1)
//	   {
//              polygon.addPoint( (int)(x + b2 * w) , y + h );
//              polygon.addPoint( (int)(x + b1 * w) , y + h );
//	   }
//
//           if (l1 != -1)
//	   {
//              polygon.addPoint( x , (int)(y + h - l1 * h) );
//              polygon.addPoint( x , (int)(y + h - l2 * h) );
//	   }
//	}
//	else
//	{
//           if (t.x != -1)
//	   {
//              polygon.addPoint( (int)(x + t.x * w) , y);
//              polygon.addPoint( (int)(x + t.y * w) , y);
//	   }
//
//           if (r.x != -1)
//	   {
//              polygon.addPoint( x + w , (int)(y + h - r.y * h) );
//              polygon.addPoint( x + w , (int)(y + h - r.x * h) );
//	   }
//
//           if (b.x != -1)
//	   {
//              polygon.addPoint( (int)(x + b.y * w) , y + h );
//              polygon.addPoint( (int)(x + b.x * w) , y + h );
//	   }
//
//           if (l.x != -1)
//	   {
//              polygon.addPoint( x , (int)(y + h - l.x * h) );
//              polygon.addPoint( x , (int)(y + h - l.y * h) );
//	   }
//	}
//        g.fillPolygon(polygon);
//    }
}

class FreeSeg
{
    public double x;
    public double y;

    public FreeSeg(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public int Empty()
    {
	if ((x == -1) && (y == -1))
	{
            return 1;
	}

        return 0;
    }

    public double MidPoint()
    {
        return (x + y) / 2;
    }

    public void Union(double x, double y)
    {
	if (this.x == -1)
	{
	   this.x = x;
	}
	else
	{
           this.x = Math.min(x, this.x);
	}
         
        this.y = Math.max(y, this.y);
    }

    public void Inter(double x, double y)
    {
        this.x = Math.max(x, this.x);   
        this.y = Math.min(y, this.y); 

        if (this.x > this.y)
	{
	    this.x = -1;
	    this.y = -1;
	}
    }
}
