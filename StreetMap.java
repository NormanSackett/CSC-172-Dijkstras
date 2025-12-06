import java.io.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Optional;
import javax.swing.*;

public class StreetMap {

	public static void main(String[] args) {
		Graph graph = new Graph();	
		try {
			//first command line argument must be the map file name
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			String line = "";
			int intNum = 0; //stands for intersection number (this is so readable)
			while ((line = reader.readLine()) != null) {
				char firstChar = line.charAt(0);
				String[] lineArr = line.split("\t");
				if (firstChar == 'i') {
					graph.addIntersect(lineArr[1], intNum++, Double.valueOf(lineArr[2]), Double.valueOf(lineArr[3]));
				} else if (firstChar == 'r') {
					graph.addRoad(lineArr[1], lineArr[2], lineArr[3]);
				}
			}
			reader.close();
		
			Graph pathGraph = null;
			//check for --directions
			for (int i = 1; i < args.length; i++) {
				if (args[i].equals("--directions")) {
					ArrayList<Node> pathList = Dijkstras(graph, args[i+1], args[i+2]);
					pathGraph = new Graph();
					for (int j = 0; j < pathList.size(); j++) {
						if (j != 0) {
							System.out.print(", ");
							Graph.Vertex[] pathVerts = graph.getIntersects(pathList.get(j-1).id, pathList.get(j).id);
							pathGraph.addRoad(graph.getRoad(pathVerts).id, pathVerts);
						}
						System.out.print(pathList.get(j).id);
					}
					break;
				}
			}
			//check for --show (this separation makes the graphing nicer)
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--show"))
					if (pathGraph != null)
						showMap(graph, args[0] + " map", Optional.of(pathGraph));
					else showMap(graph, args[0] + "map", Optional.empty());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	//Graphics implementation
	public static void showMap(Graph g, String title, Optional<Graph> pg) {
		//set up JFrame
		int w = 500;
		int h = 500;
		JFrame frame = new JFrame();
		Canvas c;
		if (pg.isPresent()) c = new Canvas(g, pg.get());
		else c = new Canvas(g);
		
		frame.setSize(w, h);
		frame.setTitle(title);
		frame.add(c);
		frame.setVisible(true);
	}
	
	@SuppressWarnings("serial") //I was too lazy to figure out how to actually fix this warning so I will just ignore it
	private static class Canvas extends JComponent {
		Graph graph;
		Graph pathGraph;
		
		public Canvas(Graph g) {
			graph = g;
			pathGraph = null;
		}
		
		public Canvas(Graph g, Graph pg) {
			graph = g;
			pathGraph = pg;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHints(rh);
			//wow I really feel like a graphics programmer now

			double largeLat = graph.getIntersect(0).lat;
			double smallLat = largeLat;
			double largeLon = graph.getIntersect(0).lon;
			double smallLon = largeLon;
			for (int i = 1; i < graph.numIntersects(); i++) {
				if (graph.getIntersect(i).lat > largeLat) largeLat = graph.getIntersect(i).lat;
				else if (graph.getIntersect(i).lat < smallLat) smallLat = graph.getIntersect(i).lat;
				
				if (graph.getIntersect(i).lon > largeLon) largeLon = graph.getIntersect(i).lon;
				else if (graph.getIntersect(i).lon < smallLon) smallLon = graph.getIntersect(i).lon;
			}
			
			//scale the lattitude and longitude values by one scale factor to maintain aspect ratio
			double deltaLat = largeLat-smallLat;
			double deltaLon = largeLon-smallLon;
			
			double scaleFactor1 = ((getHeight() - 20) / deltaLat);
			double scaleFactor2 = ((getWidth() - 20) / deltaLon);
			double minFactor = Math.min(scaleFactor1,  scaleFactor2);
			
			drawLines(graph, minFactor, largeLat, smallLon, g2d, Color.BLACK);
			if (pathGraph != null) drawLines(pathGraph, minFactor, largeLat, smallLon, g2d, Color.RED);
		}
		
		private void drawLines(Graph g, double scaleFactor, double lat, double lon, Graphics2D g2d, Color c) {
			for (int i = 0; i < g.numRoads(); i++) {
				double[] vals = new double[4];
				
				for (int j = 0; j < 2; j++) {
					//latitude values have to be messed with because lattitude counts up in the opposite direction of the JFrame
					vals[j] = g.getRoad(i).intersects[j].lat;
					vals[j] = -scaleFactor * (vals[j] - lat); //fit latitude values to frame size
					
					vals[j + 2] = g.getRoad(i).intersects[j].lon;
					vals[j + 2] = scaleFactor * (vals[j + 2] - lon); //fit longitude values to frame size
				}
				
				Line2D.Double line = new Line2D.Double(vals[2] + 10, vals[0] + 10, vals[3] + 10, vals[1] + 10);
				
				g2d.setColor(c);
				g2d.draw(line);
			}
		}
	}
	
	
	
	//Dijkstra's algorithm implementation
	public static ArrayList<Node> Dijkstras(Graph g, String startVert, String endVert) {
		@SuppressWarnings("unchecked")
		ArrayList<Node>[] adjList = new ArrayList[g.numIntersects()];
		for (int i = 0; i < g.numIntersects(); i++) {adjList[i] = new ArrayList<Node>();} //initialize arraylists
		
		//sets up adjacency list
		for (int i = 0; i < g.numRoads(); i++) {
			Graph.Vertex intersect1 = g.getRoad(i).intersects[0];
			Graph.Vertex intersect2 = g.getRoad(i).intersects[1];
			adjList[intersect1.intNum].add(new Node(intersect2.id, intersect2.intNum, g.getRoad(i).length));
			adjList[intersect2.intNum].add(new Node(intersect1.id, intersect1.intNum, g.getRoad(i).length));
		}
		double[] distances = new double[g.numIntersects()];
		
		//make the distances for every node max value (except the starting node, which has a distance of 0 to itself)
		for (int i = 0; i < g.numIntersects(); i++) {
			distances[i] = Double.MAX_VALUE;
		}
		Graph.Vertex start = g.getIntersect(startVert);
		distances[start.intNum] = 0;
		
		URHeap<Node> heap = new URHeap<Node>();
		heap.insert(new Node(startVert, start.intNum, 0));
		
		//track all nodes that have been visited to print the path
		ArrayList<Node> visitedNodes = new ArrayList<Node>();
		
		while(!heap.isEmpty()) {
			Node curr = heap.deleteMin();
			visitedNodes.add(curr);
			if (curr.id.equals(endVert)) { //break out of the loop once the end vertex is reached
				break;
			}
			
			for (Node n : adjList[curr.intNum]) {
				if (distances[curr.intNum] + n.distance < distances[n.intNum]) {
					distances[n.intNum] = n.distance + distances[curr.intNum];
					heap.insert(new Node(n.id, n.intNum, distances[n.intNum], curr));
					//new node inserted into the heap with a previous pointer
				}
			}
		}
		return path(visitedNodes.getLast(), startVert);
	}
	
	//this follows the previous pointers of the nodes to reporduce the path I think
	public static ArrayList<Node> path(Node lastNode, String startVert) {
		Node currNode = lastNode;
		ArrayList<Node> pathList = new ArrayList<Node>();
		pathList.add(currNode);
		while(currNode.id != startVert) {
			currNode = currNode.prev;
			pathList.add(0, currNode);
		}
		return pathList;
	}
	
	private static class Node implements Comparable<Node> {
		String id;
		int intNum; //stands for intersection number (this is readable and not confusing)
		double distance;
		Node prev; //the previous node is used to track the path taken in Dijkstra's
		
		public Node() {
			//this constructor may be unused, but who's to say that it is unimportant?
			//this constructor provides valuable moral support to the rest of my code
			//were I to be without this constructor, I fear my heart would be plunged into darkness
			//to take from me this simple constructor would be akin to taking from me my very life
			//for what is life without the simple yet non-functional joys of the day-to-day?
			//
			//and further still, it would be unfair to this constructor if were to be removed simply for existing
			//this constructor did not ask to be written and yet it finds itself, amid a sea of useful lines of code
			//who are any of us to deprive this function of its existence when it is our flaws as humans that spawned it?
			//
			//and who are we to put a label on the functionality of something so ambiguous?
			//we often label code useful if it is called within a program and works properly
			//but in reality, use can take many forms
			//utility can be measured through calculable use, sure
			//but utility can also be measured through the good it brings to the lives of people
			//utilitarians do not scoff at art but rather admire it as an endeavor that evokes joy in the hearts of men
			//to most, this constructor seems totally useless, a stain on an otherwise warning-free program
			//to me, however, it is one of the few sources of happiness I have found in this final week of classes
			id = "";
			intNum = 0;
			distance = 0;
			prev = null;
		}
		
		public Node(String s, int i, double d) {
			id = s;
			intNum = i;
			distance = d;
			prev = null;
		}
		
		public Node(String s, int i, double d, Node n) {
			id = s;
			intNum = i;
			distance = d;
			prev = n;
		}

		@Override
		public int compareTo(Node o) {
			if (distance > o.distance) return 1;
			else if (distance< o.distance) return -1;
			else return 0;
		}
	}
}

