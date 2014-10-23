import java.awt.*;
import java.util.Vector;
import java.lang.Math;

class PolyCurve
{
   Vector listOfPoints;
   Color  curveColor;
   public int    cursorindex;
   public double cursorpos;
   public Point  cursor;

   public PolyCurve(Color c)
   {
      listOfPoints = new Vector(); 
      curveColor = c;

      cursorindex = 0;
      cursorpos = 0.0;

      cursor = new Point();
   }

   public int getSize()
   {
      return listOfPoints.size();
   }

   public void addPoint (Point p)
   {
      listOfPoints.addElement(p);
   }
   
   public void reverse()
   {
	   Vector newList = new Vector();
	   for (int i = listOfPoints.size()-1; i >= 0; i--) {
		   newList.addElement(listOfPoints.get(i));
	   }
	   listOfPoints = newList;
   }

   public Point getPoint(int index)
   {
      return (Point)listOfPoints.elementAt(index);
   }

   public void removeAllPoints()
   {
       listOfPoints.removeAllElements();
   }

//   // Draw the outline of the polygon on the given graphics. 
//   public void draw (Graphics g)
//   {
//      Point pt, pt2;
//        
//      if (listOfPoints.size() > 0)
//      {
//         g.setColor (Color.red); 
//
//         pt = (Point)listOfPoints.elementAt(0);
//         g.drawOval (pt.x-5, pt.y-5, 10, 10);
//
//         pt = (Point)listOfPoints.elementAt(listOfPoints.size() - 1);
//         g.drawOval (pt.x-5, pt.y-5, 10, 10);
//	
//         g.setColor(curveColor);
//         
//         pt = pt2 = (Point)listOfPoints.firstElement();
//            
//         for (int i = 0; i < listOfPoints.size() - 1; i++)
//         {
//            pt2 = (Point)listOfPoints.elementAt(i+1);
//            g.drawLine (pt.x, pt.y, pt2.x, pt2.y);
//            pt = pt2;
//         }
//
//         g.setColor (Color.black);  
//
//         pt = (Point)listOfPoints.elementAt(0);
//         g.drawBytes("s".getBytes(),0,1,pt.x-2,pt.y+4);   
//      
//         pt = (Point)listOfPoints.elementAt(listOfPoints.size() - 1);
//         g.drawBytes("e".getBytes(),0,1,pt.x-2,pt.y+4);   
//
//         if (cursorindex < listOfPoints.size() - 1)
//	 {
//            pt = (Point)listOfPoints.elementAt(cursorindex);
//            pt2 = (Point)listOfPoints.elementAt(cursorindex+1);
//
//            cursor.x = (int)((pt2.x - pt.x) * cursorpos + pt.x);
//            cursor.y = (int)((pt2.y - pt.y) * cursorpos + pt.y);
//
//            g.setColor (Color.green);
//            g.drawOval(cursor.x - 5, cursor.y - 5, 10, 10);
//	 }
//      }
//   }
}
