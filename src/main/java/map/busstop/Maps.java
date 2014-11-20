package map.busstop;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;


public class Maps extends JFrame  {

	public static void main(String[] args) throws Exception{
		
		JXMapViewer mapViewer = new JXMapViewer();

		// Display the viewer in a JFrame
		JFrame frame = new JFrame("Buszmegallok");
		frame.getContentPane().add(mapViewer);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		tileFactory.setThreadPoolSize(8);
		mapViewer.setTileFactory(tileFactory);
	    
	    ArrayList<GeoPosition> megall = new ArrayList<GeoPosition>();
	    String line;
	    FileReader fr = new FileReader("ki") ;
		BufferedReader br = new BufferedReader(fr);
		
		while ((line = br.readLine()) != null) {
		       String [] st = line.split("\\s+"); 
		       try{
		    	   System.out.println(st[1] + "     " +st[0]);
		    	   megall.add(new GeoPosition(Double.parseDouble(st[0]),Double.parseDouble(st[1])));}
		       catch(Exception e){}
		}
	    br.close();
	    

		// Create a track from the geo-positions
		RoutePainter routePainter = new RoutePainter(megall);

		// Set the focus
		mapViewer.zoomToBestFit(new HashSet<GeoPosition>(megall), 0.7);

		 //Create waypoints from the geo-positions
		 Set<Waypoint> waypoints = new HashSet <Waypoint> ();
		 /*for (GeoPosition b : megall) {
			waypoints.add(new DefaultWaypoint(b));
		}*/
		 waypoints.add(new DefaultWaypoint(megall.get(0)));
		 waypoints.add(new DefaultWaypoint(megall.get(megall.size()-1)));

		// Create a waypoint painter that takes all the waypoints
		WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
		waypointPainter.setWaypoints(waypoints);
		
		// Create a compound painter that uses both the route-painter and the waypoint-painter
		List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
		painters.add(routePainter);
		painters.add(waypointPainter);
		
		
		
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);

		mapViewer.addMouseListener(new CenterMapListener(mapViewer));
		
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
		
		mapViewer.addKeyListener(new PanKeyListener(mapViewer));
		
		// Add a selection painter
		SelectionAdapter sa = new SelectionAdapter(mapViewer); 
		SelectionPainter sp = new SelectionPainter(sa); 
		mapViewer.addMouseListener(sa); 
		mapViewer.addMouseMotionListener(sa); 
		mapViewer.setOverlayPainter(sp);
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
		mapViewer.setOverlayPainter(painter);
		}
}