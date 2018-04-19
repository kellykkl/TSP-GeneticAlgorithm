import java.awt.Color;
import java.awt.Graphics;
import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JPanel;

public class Map extends JPanel{
	
	ArrayList<Object[]> list_points = generateMap(15);
	Integer[] indexes = new Integer[15];
	
	public Map(int points) {
		list_points = generateMap(points);
		indexes = new Integer[points];
		for (int i=0; i<list_points.size(); i++) {
			//System.out.println(Arrays.toString(list_points.get(i)));	
//			System.out.println(list_points.get(i)[0]);
			indexes[i] = 0;
		}
	}
	
	public ArrayList<Object[]> getMap(){
		return list_points;
	}
	
	public void setSolution(Integer[] index) {
		indexes = index;
	}
	
	public ArrayList<Object[]> generateMap(int num_points) {
		ArrayList<Object[]> list_points = new ArrayList<Object[]>();
		Random r = new Random();
		for (int i=0; i<num_points; i++) {
			list_points.add(new Integer[] {r.nextInt(500), r.nextInt(500)});
		}
		return list_points;
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 600, 600);
		
		g.setColor(Color.BLACK);
		for (int i=0; i<list_points.size(); i++) {
			int xcoord = (int) list_points.get(i)[0];
			int ycoord = (int) list_points.get(i)[1];
			g.fillOval(xcoord, ycoord,8,8);
		}
		
		//draw solution
		for (int i=0; i<indexes.length-1; i++) {
			int x1 = (int)list_points.get(indexes[i])[0];
			int y1 = (int)list_points.get(indexes[i])[1];
			int x2 = (int)list_points.get(indexes[i+1])[0];
			int y2 = (int)list_points.get(indexes[i+1])[1];
			g.drawLine(x1,y1,x2,y2);
		}
		//draw solution connecting last point to first
		int x1 = (int)list_points.get(indexes[indexes.length-1])[0];
		int y1 = (int)list_points.get(indexes[indexes.length-1])[1];
		int x2 = (int)list_points.get(indexes[0])[0];
		int y2 = (int)list_points.get(indexes[0])[1];
		g.drawLine(x1,y1,x2,y2);
		
	}
	
	
}
