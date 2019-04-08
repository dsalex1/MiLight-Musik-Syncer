package werwer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
class Canvas extends JPanel
{
	ArrayList<ArrayList<ColoredPoint>> PointsLists = new ArrayList<ArrayList<ColoredPoint>>();
	private boolean clear;

    public Canvas()
    {
    	clear();
    }
    
    public void paintComponent(Graphics g)
    {
    	g.setColor(Color.white);
    	g.fillRect(0, 0, this.getWidth(), this.getHeight());
    	g.setColor(Color.red);
    	g.drawLine(0,20,this.getWidth(),20);
    	g.drawLine(0,170,this.getWidth(),170);
    	for (ArrayList<ColoredPoint>l:PointsLists)
	    		for (int j=1;j<l.size();j++){
	    	    	g.setColor(l.get(j).color);
	    			g.drawLine(l.get(j-1).x, l.get(j-1).y, l.get(j).x, l.get(j).y);
	    		}
    	g.dispose();
    }
    
    public void addPoint(ColoredPoint pt){
    	boolean added=false;
    	for (ArrayList<ColoredPoint>l:PointsLists){
    		if (/*!l.isEmpty() &&*/ l.get(0).color.equals(pt.color)){
    			l.add(pt);
    			added=true;
    		}
    	}
    	
    	if (!added){
			ArrayList<ColoredPoint> tmp=new ArrayList<ColoredPoint>();
			tmp.add(pt);
			PointsLists.add(tmp);
    	}
		
    	this.repaint();
    }
    public void clear() {
    	PointsLists.clear();
    		clear=true;
			ArrayList<ColoredPoint> tmp;
			tmp=new ArrayList<ColoredPoint>();
			tmp.add(new ColoredPoint(0,0,Color.GREEN));
			PointsLists.add(tmp);
			tmp=new ArrayList<ColoredPoint>();
			tmp.add(new ColoredPoint(0,0,Color.BLUE));
			PointsLists.add(tmp);
    }
    
}
