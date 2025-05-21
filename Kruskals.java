import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class Edge implements Comparable<Edge> {
    int src, dest, weight;

    public Edge(int src, int dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge compareEdge) {
        return this.weight - compareEdge.weight;
    }
}

class Graph {
    private int V, E;
    private Edge[] edges;

    public Graph(int v, int e) {
        V = v;
        E = e;
        edges = new Edge[E];
    }

    public void addEdge(int src, int dest, int weight, int edgeNum) {
        edges[edgeNum] = new Edge(src, dest, weight);
    }

    private int find(int[] parent, int i) {
        if (parent[i] == -1)
            return i;
        return find(parent, parent[i]);
    }

    private void union(int[] parent, int x, int y) {
        int xSet = find(parent, x);
        int ySet = find(parent, y);
        parent[xSet] = ySet;
    }

    public void kruskalMST() {
        Edge[] result = new Edge[V - 1]; // V-1 edges in MST
        int e = 0;
        int i = 0;

        Arrays.sort(edges);

        int[] parent = new int[V + 1]; // Adjusting for 1-based indexing
        Arrays.fill(parent, -1);

        while (e < V - 1 && i < E) {
            Edge next_edge = edges[i++];
            int x = find(parent, next_edge.src);
            int y = find(parent, next_edge.dest);

            if (x != y) {
                result[e++] = next_edge;
                union(parent, x, y);
            }
        }

        if (e != V - 1) {
            JOptionPane.showMessageDialog(null, "The graph is disconnected. Minimum Spanning Tree cannot be formed.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Edges in the Minimum Spanning Tree:\n");
        for (i = 0; i < e; ++i)
            sb.append(result[i].src + " - " + result[i].dest + ": " + result[i].weight + "\n");

        String numSourcesStr = JOptionPane.showInputDialog("Enter the number of water sources: ");
        int numSources = Integer.parseInt(numSourcesStr);
        int[] sources = new int[numSources];
        for (int j = 0; j < numSources; j++) {
            String sourceStr = JOptionPane.showInputDialog("Enter water source " + (j + 1) + ": ");
            sources[j] = Integer.parseInt(sourceStr);
        }

        String destStr = JOptionPane.showInputDialog("Enter the destination node: ");
        int destination = Integer.parseInt(destStr);

        dijkstraMST(sources, destination, result, sb.toString());
    }

    private void dijkstraMST(int[] sources, int dest, Edge[] result, String mstEdges) {
        Map<Integer, java.util.List<Edge>> adjList = new HashMap<>();
        for (Edge edge : result) {
            if (edge != null) {
                adjList.computeIfAbsent(edge.src, k -> new ArrayList<>()).add(edge);
                adjList.computeIfAbsent(edge.dest, k -> new ArrayList<>()).add(new Edge(edge.dest, edge.src, edge.weight));
            }
        }

        int minDistance = Integer.MAX_VALUE;
        int nearestSource = -1;
        PathInfo bestPath = null;

        for (int src : sources) {
            PathInfo pathInfo = dijkstra(src, adjList);
            if (pathInfo.dist[dest] < minDistance) {
                minDistance = pathInfo.dist[dest];
                nearestSource = src;
                bestPath = pathInfo;
            }
        }

        if (nearestSource == -1) {
            JOptionPane.showMessageDialog(null, "No path available to the destination node.");
        } else {
            StringBuilder resultStr = new StringBuilder();
            resultStr.append("Shortest route is from water source " + nearestSource + " to destination " + dest + ":\n");
            printRoute(nearestSource, dest, bestPath.dist, bestPath.prev, resultStr);
            JOptionPane.showMessageDialog(null, mstEdges + "\n" + resultStr.toString());
        }
    }

    private PathInfo dijkstra(int src, Map<Integer, java.util.List<Edge>> adjList) {
        int[] dist = new int[V + 1]; // Adjusting for 1-based indexing
        int[] prev = new int[V + 1]; // To reconstruct the path
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[src] = 0;

        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(u -> dist[u]));
        pq.offer(src);

        while (!pq.isEmpty()) {
            int u = pq.poll();

            if (adjList.containsKey(u)) {
                for (Edge edge : adjList.get(u)) {
                    int v = edge.dest;
                    int weight = edge.weight;

                    if (dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                        pq.offer(v);
                        prev[v] = u;
                    }
                }
            }
        }
        return new PathInfo(dist, prev);
    }


    private void printRoute(int src, int dest, int[] dist, int[] prev, StringBuilder resultStr) {
        if (dist[dest] == Integer.MAX_VALUE) {
            resultStr.append("To node " + dest + ": No path available.\n");
        } else {
            resultStr.append("To node " + dest + ": Distance = " + dist[dest]);

            // Printing the route taken
            java.util.List<Integer> path = new ArrayList<>();
            for (int at = dest; at != -1; at = prev[at]) {
                path.add(at);
            }
            Collections.reverse(path);
            resultStr.append(", Route: " + path);
            resultStr.append("\n");
        }
    }

    private class PathInfo {
        int[] dist;
        int[] prev;

        PathInfo(int[] dist, int[] prev) {
            this.dist = dist;
            this.prev = prev;
        }
    }
}

public class Kruskals {
    public static void main(String[] args) {
        JTextField verticesField = new JTextField(30);
        JTextField edgesField = new JTextField(60);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Enter the number of vertices:"));
        panel.add(verticesField);
        panel.add(new JLabel("Enter the number of edges:"));
        panel.add(edgesField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Graph Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            int V = Integer.parseInt(verticesField.getText());
            int E = Integer.parseInt(edgesField.getText());

            Graph graph = new Graph(V, E);

            for (int i = 0; i < E; i++) {
                JTextField srcField = new JTextField(5);
                JTextField destField = new JTextField(5);
                JTextField weightField = new JTextField(5);

                JPanel edgePanel = new JPanel(new GridLayout(0, 1));
                edgePanel.add(new JLabel("Enter details for edge " + (i + 1) + ":"));
                edgePanel.add(new JLabel("Source vertex:"));
                edgePanel.add(srcField);
                edgePanel.add(new JLabel("Destination vertex:"));
                edgePanel.add(destField);
                edgePanel.add(new JLabel("Weight:"));
                edgePanel.add(weightField);

                int edgeResult = JOptionPane.showConfirmDialog(null, edgePanel, "Edge " + (i + 1),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (edgeResult == JOptionPane.OK_OPTION) {
                    int src = Integer.parseInt(srcField.getText());
                    int dest = Integer.parseInt(destField.getText());
                    int weight = Integer.parseInt(weightField.getText());
                    graph.addEdge(src, dest, weight, i);
                } else {
                    JOptionPane.showMessageDialog(null, "Edge input cancelled. Exiting.");
                    return;
                }
            }

            graph.kruskalMST();
        }
    }
}

