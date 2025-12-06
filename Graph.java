import java.util.ArrayList;

public class Graph {
	ArrayList<Vertex> intersectList;
	ArrayList<Edge> roadList;
	
	public Graph() {
		intersectList = new ArrayList<Vertex>();
		roadList = new ArrayList<Edge>();
	}
	
	public Graph(ArrayList<Vertex> intersects, ArrayList<Edge> roads) {
		intersectList = intersects;
		roadList = roads;
	}

	public Vertex getIntersect(int i) {
		return intersectList.get(i);
	}
	
	public Vertex getIntersect(String id) {
		for(int i = 0; i < intersectList.size(); i++) {
			if (intersectList.get(i).id.equals(id)) {
				return intersectList.get(i);
			}
		}
		return null;
	}
	
	public Edge getRoad(int i) {
		return roadList.get(i);
	}
	
	public Edge getRoad(Vertex[] v) {
		for (int i = 0; i < roadList.size(); i++) {
			Vertex[] intersects = roadList.get(i).intersects;
			if ((intersects[0].equals(v[0]) && intersects[1].equals(v[1]))
				|| (intersects[1].equals(v[0]) && intersects[0].equals(v[1]))) {
				return roadList.get(i);
			}
		}
		return null;
	}
	
	public void addIntersect(String id, int i, double lat, double lon) {
		intersectList.add(new Vertex(id, i, lat, lon));
	}
	
	public int getIntersectVal(int i) {
		return intersectList.get(i).intNum;
	}
	
	public int numIntersects() {
		return intersectList.size();
	}
	
	public void addRoad(String id, Vertex[] verts) {
		roadList.add(new Edge(id, verts.clone()));
	}
	
	public void addRoad(String id, String vert1, String vert2) {
		Vertex[] verts = new Vertex[2];
		int vertIndex = 0;
		for (int i = 0; i < intersectList.size(); i++) {
			if (intersectList.get(i).id.equals(vert1)) {
				verts[vertIndex++] = intersectList.get(i);
			} else if (intersectList.get(i).id.equals(vert2)) {
				verts[vertIndex++] = intersectList.get(i);
			}
			
			if (vertIndex == 2) break;
		}
		roadList.add(new Edge(id, verts.clone()));
	}
	
	public int numRoads() {
		return roadList.size();
	}
	
	public boolean containsRoad(Edge r) {
		for (int i = 0; i < roadList.size(); i++) {
			//this is a beautiful piece of logic and I will not hear otherwise
			if ((roadList.get(i).intersects[0].compareTo(r.intersects[0]) == 1
				&& roadList.get(i).intersects[1].compareTo(r.intersects[1]) == 1) ||
				(roadList.get(i).intersects[1].compareTo(r.intersects[0]) == 1 && 
				roadList.get(i).intersects[0].compareTo(r.intersects[0]) == 1)) {
				return true;
			}
		}
		return false;
	}
	
	public class Vertex implements Comparable<Vertex> {
		String id;
		int intNum; //stands for intersection number (trust)
		double lat, lon;
		
		public Vertex(String s, int i, double coord1, double coord2) {
			id = s;
			intNum = i;
			lat = coord1;
			lon = coord2;
		}

		@Override
		public int compareTo(Vertex o) {
			if (o.lat == lat && o.lon == lon) return 1;
			else return 0;
		}
	}
	
	public class Edge {
		String id;
		Vertex[] intersects;
		double length;
		
		public Edge(String s, Vertex[] i) {
			id = s;
			intersects = i.clone();
			//distance between verticies with Haversine formula implementation from
			//https://gist.github.com/vananth22/888ed9a22105670e7a4092bdcf0d72e4
			
			 final int R = 6371; // Radius of the earth
			 Double lat1 = intersects[0].lat;
			 Double lon1 = intersects[0].lon;
			 Double lat2 = intersects[1].lat;
			 Double lon2 = intersects[1].lon;
			 Double latDistance = Math.toRadians(lat2-lat1);
			 Double lonDistance = Math.toRadians(lon2-lon1);
			 Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
					 	Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
					 	Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
			 Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			 length = R * c;
		}
	}
}
