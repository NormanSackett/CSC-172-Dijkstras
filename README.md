name: Norman Sackett
email: nsackett@u.rochester.edu

lab partner: Julien Diamond

CSC-172 project 3 has us implement Dijkstra's Algorithm along with a visual of the graph. Dijkstra's algorithm uses an adjacency list to hold vertex information along with their distance to neighboring verticies, an array of distances to keep track of the current shortest distance to a given vertex (which are initialized as Double.MAX_VALUE), a min heap to fetch verticies in order of shortest distance, and an arraylist that holds all of the visited verticies (all distances are calculated with the curvature of the Earth in mind using the Haversine formula). The algorithm walks inserts the starting node into the heap, then, while the heap isn't empty, we poll the heap, grabbing the minimum element as the current node. Then, we check each neighbor of the current node and, if their distance from the current node plus the distance of the current node from the start node is less than the current distance in the distances array, we insert that node into the heap. This will find the shortest distance to all nodes in the graph from the starting node, but since we only care about the distance to the end node, we will break out of the loop once the end node is checked. Since we stop the loop once we check the end node, we can reconstruct the path of nodes by setting previous pointers as we update their distances and then following the previous pointers starting at the last node in the vistitedNodes arraylist.
The graphing uses java's built-in Graphics library and is only shown if the --show command line argument is present. It creates a canvas that holds a graph within. For each intersection in the graph, we will need to find the least and greatest latitude and longitude and use those to fit the graph to the size of the frame. The minimum of the two scale factors found (one from the latitude, one from the longitude) is used so as to maintain the aspect ratio of the map. Then, for each road in the graph, we draw a line on the frame using the latitude and longitude values of the road's intersections and the previously calculated scale factor. If the --directions command line argument is present, the graph will also draw the roads in the shortest path from the start node to the end node in red.

In the StreetMap.java file:
main handles command line arguments and file reading
showMap creates java swing objects to contain and display graphics
Canvas is a custom object that has a paintComponent method that is called every time its container is created or resized - this displays the map
Dijkstras performs the Dijkstra's Algorithm and returns an arraylist with the nodes in the final path
path follows the pointers from the last dequeued node until the beginning of the path and adds each to the returned arraylist
Node is a custom object that has an id, an index, a distance, and a previous pointer (used for the adjacency list)

In the Graph.java file:
there are three ways to get intersections: one that takes a String and returns one vertex, one that takes an integer and returns one vertex, and one that takes two Strings and returns two verticies
there are two ways to get roads: one that takes an integer and returns one edge, and one that takes two verticies and returns one edge
there are methods to add intersections and fetch their values
there are two ways to add a road: one that takes two verticies, and one that takes to vertex id's
containsRoad returns true if the parameterized edge is present in the roadList arraylist (handles edges that are the same, but with their intersections swapped)
this also contains the custom Vertex and Edge objects used for drawing
