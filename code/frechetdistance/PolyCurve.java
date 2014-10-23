import java.awt.*;
import java.util.Vector;
import java.lang.Math;

/**
 *  PolyCurve
 */
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

}
