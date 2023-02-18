import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javalib.worldimages.*;
import java.util.Random;

// represents an individual cell
class Vertex { //cells
  int x;
  int y;
  ArrayList<Edge> outEdges;
  Color color;
  Vertex top;
  Vertex right;
  Vertex bottom;
  Vertex left;
  
  Vertex(int x, int y) {
    this.x = x;
    this.y = y;
    this.outEdges = new ArrayList<Edge>();
    this.right = null;
    this.bottom = null;
    this.top = null;
    this.left = null;
  }
  
  Vertex(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.outEdges = new ArrayList<Edge>();
    this.right = null;
    this.bottom = null;
    this.top = null;
    this.left = null;
  }
  
  Vertex(int x, int y, Vertex right, Vertex bottom, Vertex top, Vertex left) {
    this.x = x;
    this.y = y;
    this.outEdges = new ArrayList<Edge>();
    this.right = right;
    this.bottom = bottom;
    this.top = top;
    this.left = left;
  }
  
  Vertex(int x, int y, ArrayList<Edge> outEdges, Color color,
      Vertex right, Vertex bottom, Vertex top, Vertex left) {
    this.x = x;
    this.y = y;
    this.outEdges = new ArrayList<Edge>();
    this.color = color;
    this.right = right;
    this.bottom = bottom;
    this.top = top;
    this.left = left;
  }
  
  // draws a vertex , aka a "Cell", also checks if the top, bottom, left, or right of them
  // are null, then draws a "wall" if they are. 
  public WorldImage drawVertex() {
    WorldImage vertex = new RectangleImage(MazeGame.VERTEX_SIZE, MazeGame.VERTEX_SIZE, 
        OutlineMode.SOLID, this.color);
    
    WorldImage vertWall = new RectangleImage(2, MazeGame.VERTEX_SIZE, 
        OutlineMode.SOLID, Color.black);
    
    WorldImage horiWall = new RectangleImage(MazeGame.VERTEX_SIZE, 2, 
        OutlineMode.SOLID, Color.black);
   
    if (this.right == null) {
      vertex = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, 
          vertWall, 0, 0, vertex);
    }
    
    if (this.left == null) {
      vertex = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE, 
          vertWall, 0, 0, vertex);
    }
    if (this.top == null) {
      vertex = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, 
          horiWall, 0, 0, vertex);
    }
    if (this.bottom == null) {
      vertex = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, 
          horiWall, 0, 0, vertex);
    }
    
    return vertex;
  }
  
  // overrides hashCode for a Vertex
  @Override
  public int hashCode() {
    return new Posn(this.x, this.y).hashCode();
  }
  
  // overrides equals for a Vertex
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Vertex)) {
      return false;
    }
    
    Vertex that = (Vertex) o;
    return this.x == that.x
        && this.y == that.y;
  }
  
  // gets the neighbors of a vertex
  public ArrayList<Vertex> getVertexNeighbors() {
    ArrayList<Vertex> vertList = new ArrayList<Vertex>();
    
    if (left != null) {
      vertList.add(left);
    }
    if (right != null) {
      vertList.add(right);
    }
    if (top != null) {
      vertList.add(top);
    }
    if (bottom != null) {
      vertList.add(bottom);
    }
    return vertList;
  }
}

// represents two connected cells with no wall between
class Edge {
  Vertex from;
  Vertex to;
  int weight;
  Random rand = new Random();
  
  Edge(Vertex from, Vertex to) {
    this.from = from;
    this.to = to;
    this.weight = rand.nextInt(100);
  }
  
  Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }
  
  // joins the vertices of an Edge together
  void joinEdgeCell() {
    if (from.y == to.y && from.x < to.x) {
      from.right = to;
      to.left = from;
    }
    else if (from.y == to.y && from.x > to.x) {
      from.left = to;
      to.right = from;
    }
    else if (from.x == to.x && from.y < to.y) {
      from.bottom = to;
      to.top = from;
    }
    else if (from.x == to.x && from.y > to.y) {
      from.top = to;
      to.bottom = from;     
    }
  }
}


interface ICollection<T> {
  // adds an item to the collection
  void add(T t);
  
  // removes an item from the collection
  // returns the item that was removed
  T remove();
  
  // returns the size of the worklist
  int size();
  
  // checks if an ICollection is empty or not
  boolean isEmpty();
}

// use for DFS
class Stack<T> implements ICollection<T> {
  ArrayDeque<T> items;

  Stack() {
    this.items = new ArrayDeque<T>();
  }

  // adds to the front of the Stack
  public void add(T t) {
    this.items.addFirst(t);
  }

  // removes the first item of the Stack
  public T remove() {
    return this.items.removeFirst();
  }

  // returns the size of the stack
  public int size() {
    return this.items.size();
  }
  
  // checks if a Stack is empty.
  public boolean isEmpty() {
    return this.items.isEmpty();
  } 
}

// use for BFS
class Queue<T> implements ICollection<T> {
  ArrayDeque<T> items;

  Queue() {
    this.items = new ArrayDeque<T>();
  }
  
  // adda an item to the end of the Queue
  public void add(T t) {
    this.items.addLast(t);
  }

  // removes the item from the front of the Queue
  public T remove() {
    return this.items.removeFirst();
  }

  // returns the size of Queue
  public int size() {
    return this.items.size();
  }

  // checks if the Queue is empty
  public boolean isEmpty() {
    return this.items.isEmpty();
  } 
}

// A Comparator for an Edge to compare two edge objects
class EdgeCompare implements Comparator<Edge> {

  // returns the difference between the weights of 2 edges
  public int compare(Edge o1, Edge o2) {
    return o1.weight - o2.weight;
  }  
}

//represents the union/find data structure for Kruskal's algorithm
class UnionFind {
  HashMap<Vertex, Vertex> representatives;

  UnionFind(HashMap<Vertex, Vertex> r) {
    this.representatives = r;
    
  }

  // finds the representatives of a single Vertex
  Vertex find(Vertex v) {
    if (v.equals(this.representatives.get(v))) {
      return v;
    }
    else {
      return this.find(this.representatives.get(v));
    }
  }
  

  // Unions to's representative's representative with from's representative's representative
  void union(Vertex from, Vertex to) {
    this.representatives.put(this.find(to), this.find(from));
  }
}

// represents the Maze Game
class MazeGame extends World {
  
  int bWidth; // the width of the board
  
  int bHeight; // the height of the board
  
  //int scale;
  
  static int VERTEX_SIZE = 14;
  
  ArrayList<ArrayList<Vertex>> vertices;
  
  HashMap<Vertex, Vertex> reps = new HashMap<Vertex, Vertex>(); // representatives
  
  HashMap<Vertex, Vertex> back = new HashMap<Vertex, Vertex>();
  
  ArrayList<Edge> edgesInTree;
  
  ArrayList<Edge> worklist;
  
  ArrayDeque<Vertex> visited;
  
  ICollection<Vertex> vertexWorklist;
  
  Vertex ancestor;
  
  UnionFind uf;

  Random rand;
  
  MazeGame(Random rand) {
    this.rand = rand;
    this.bWidth = rand.nextInt(101);
    this.bHeight = rand.nextInt(61);
    this.vertices = initialVertices();
    this.vertexWorklist = null;
    this.visited = new ArrayDeque<Vertex>();
    this.initialHash();
    this.edgesInTree = kruskals(initialEdges(), new UnionFind(this.reps));
  }
  
  MazeGame(ArrayList<ArrayList<Vertex>> vertices) {
    this.vertices = initialVertices();
    this.bWidth = rand.nextInt(101);
    this.bHeight = rand.nextInt(61);
    this.initialHash();
    this.vertexWorklist = null;
    this.visited = new ArrayDeque<Vertex>();
    this.edgesInTree = kruskals(initialEdges(), new UnionFind(this.reps));
  }
  
  MazeGame(int bWidth, int bHeight, Random rand) {
    this.rand = rand;
    this.bWidth = bWidth;
    this.bHeight = bHeight;
    this.vertices = initialVertices();
    this.vertexWorklist = null;
    this.visited = new ArrayDeque<Vertex>();
    this.initialHash();
    this.edgesInTree = kruskals(initialEdges(), new UnionFind(this.reps));
  }
  
  // renders the initial list of Vertices for the unsolved maze
  public ArrayList<ArrayList<Vertex>> initialVertices() {
    ArrayList<ArrayList<Vertex>> array = new ArrayList<ArrayList<Vertex>>();
    
    for (int i = 0; i < this.bHeight; i++) {
      ArrayList<Vertex> row = new ArrayList<Vertex>();
      for (int j = 0; j < this.bWidth; j++) {
        row.add(new Vertex(j, i, Color.white));
      }
      array.add(row);

    } 
    array.get(0).get(0).color = Color.GREEN;
    array.get(bHeight - 1).get(bWidth - 1).color = Color.magenta;
    return array;
  }
  
  // renders the initial list of Edges needed for the maze
  public ArrayList<Edge> initialEdges() {
    
    ArrayList<Edge> edges = new ArrayList<Edge>();
    
    for (int i = 0; i < this.bHeight; i++) {
      for (int j = 0; j < this.bWidth; j++) {
        
        Vertex v = this.vertices.get(i).get(j);
        
        if (j + 1 < this.bWidth) {
          edges.add(new Edge(v, this.vertices.get(i).get(j + 1), this.rand.nextInt(10000)));
        }        
        if (i + 1 < this.bHeight) {
          edges.add(new Edge(v, this.vertices.get(i + 1).get(j), this.rand.nextInt(10000)));
        }
      }
    }
    return edges;
  }
  
  // initializes the hashmap of representatives we check later
  void initialHash() {
    for (int i = 0; i < this.bHeight; i++) {
      for (int j = 0; j < this.bWidth; j++) {
        Vertex v = this.vertices.get(i).get(j);
        this.reps.put(v, v);
      }
    }
  }
  
  // renders the board with images of cells
  public WorldScene drawVertices(WorldScene s) {
    for (int i = 0; i < bHeight; i++) {
      for (int k = 0; k < bWidth; k++) {
        Vertex a = vertices.get(i).get(k);
        s.placeImageXY(a.drawVertex(), k * MazeGame.VERTEX_SIZE + (MazeGame.VERTEX_SIZE / 2), 
            i * MazeGame.VERTEX_SIZE + (MazeGame.VERTEX_SIZE / 2));
      }
    }
   
    return s;
  }

  // draws the initial maze
  public WorldScene makeScene() {
    
    return this.drawVertices(
        new WorldScene(bHeight * MazeGame.VERTEX_SIZE * 10, bWidth * MazeGame.VERTEX_SIZE * 10));
  }
  
  // kruskal's algorithm needed to create the maze
  ArrayList<Edge> kruskals(ArrayList<Edge> edgeList, UnionFind uf) {
    
    ArrayList<Edge> c = new ArrayList<Edge>(edgeList);
    ArrayList<Edge> goodEdges = new ArrayList<Edge>();
    
    c.sort(new EdgeCompare());
    while (c.size() > 1) {
      
      Edge nextCheapestEdge = c.remove(0);
      Vertex fromRep = uf.find(nextCheapestEdge.from);
      Vertex toRep = uf.find(nextCheapestEdge.to);
      
      if (!(fromRep.equals(toRep))) {
        nextCheapestEdge.joinEdgeCell();
        uf.union(fromRep, toRep);
        goodEdges.add(nextCheapestEdge); 
      }
    }
    return goodEdges;
  }
  
  // resets the board if user presses b or d again
  void reset() {
    for (int i = 0; i < bHeight; i++) {
      for (int j = 0; j < bWidth; j++) {
        this.vertices.get(i).get(j).color = Color.white;
      }
    }
    this.vertices.get(0).get(0).color = Color.GREEN;
    this.vertices.get(bHeight - 1).get(bWidth - 1).color = Color.MAGENTA;
  }
  
  // animates the search algorithms on every Tick
  public void onTick() {   
    if (vertexWorklist != null && !vertexWorklist.isEmpty()) {
      Vertex next = this.vertexWorklist.remove();
      
      if (next.equals(vertices.get(bHeight - 1).get(bWidth - 1))) {
        this.ancestor = vertices.get(bHeight - 1).get(bWidth - 1);
        this.vertexWorklist = null;
        return;
      }
      
      else if (visited.contains(next)) {
        return;
      }
      
      else {
        for (Vertex toBeFilled : next.getVertexNeighbors()) {
          if (!this.visited.contains(toBeFilled)) {
            this.back.put(toBeFilled, next);
            this.vertexWorklist.add(toBeFilled);
          }
        }
        this.visited.addLast(next);
        next.color = Color.magenta; 
      }
    }
    else if (this.ancestor != null) {
      this.ancestor.color = Color.blue;
      this.ancestor = this.back.get(this.ancestor);
    }
  }
  
  // lets the user choose BFS or DFS
  public void onKeyEvent(String key) {
    
    if (key.equals("b")) { // queue
      this.vertexWorklist = new Queue<Vertex>();
      this.vertexWorklist.add(vertices.get(0).get(0));
      reset();
      this.visited = new ArrayDeque<Vertex>();
    }
    
    if (key.equals("d")) { // stack
      this.vertexWorklist = new Stack<Vertex>();
      this.vertexWorklist.add(vertices.get(0).get(0));
      reset();
      this.visited = new ArrayDeque<Vertex>();
    }
  }
}

class ExamplesMaze {
  
  Vertex v1; //center
  Vertex v2; //top
  Vertex v3; //bottom
  Vertex v4; // left
  Vertex v5; // right
  
  Edge e1;
  Edge e2;
  Edge e3;
  Edge e4;
  Edge e5;
  Edge e6;
  Edge e7;
  
  Queue<Integer> someInts;
  Queue<Integer> oneInt;
  Queue<Integer> emptyInts;
  
  Stack<Vertex> someVerts;
  Stack<Vertex> oneVert;
  Stack<Vertex> emptyVerts;
  
  ArrayList<Edge> someEdges;
  ArrayList<Edge> oneEdge;
  ArrayList<Edge> emptyEdges;
  
  UnionFind uf1;
  UnionFind uf2;
  
  Vertex noneNull;
  Vertex someNull;
  Vertex allNull;
  
  MazeGame m1;
  MazeGame m2;
  MazeGame m3;
  MazeGame m4;
  
  void initConditions() {
    this.v1 = new Vertex(14, 15);
    this.v2 = new Vertex(14, 14);
    this.v3 = new Vertex(14, 16);
    this.v4 = new Vertex(13, 15);
    this.v5 = new Vertex(15, 15);
    
    this.e1 = new Edge(v2, v1);
    this.e2 = new Edge(v4, v1);
    this.e3 = new Edge(v5, v1);
    this.e4 = new Edge(v3, v1);
    this.e5 = new Edge(v2, v3, 24);
    this.e6 = new Edge(v4, v5, 27);
    this.e7 = new Edge(v5, v4, 67);
    
    this.uf1 = new UnionFind(new HashMap<Vertex, Vertex>(Map.of(v1, v1,
        v2, v2, 
        v3, v3)));
    this.uf2 = new UnionFind(new HashMap<Vertex, Vertex>(Map.of(v3, v3, 
        v4, v4, 
        v5, v5)));
    
    this.someEdges = new ArrayList<Edge>();
    someEdges.add(e4);
    someEdges.add(e5);
    this.oneEdge = new ArrayList<Edge>();
    oneEdge.add(e7);
    this.emptyEdges = new ArrayList<Edge>();
    
    this.someInts = new Queue<Integer>();
    this.someInts.add(2);
    this.someInts.add(4);
    this.someInts.add(6);
    this.oneInt = new Queue<Integer>();
    this.oneInt.add(0);
    this.emptyInts = new Queue<Integer>();
    
    this.someVerts = new Stack<Vertex>();
    this.someVerts.add(v1);
    this.someVerts.add(v2);
    this.oneVert = new Stack<Vertex>();
    this.oneVert.add(v1);
    this.emptyVerts = new Stack<Vertex>();
    
    
    this.noneNull = new Vertex(14, 15, v5, v3, v2, v4);
    this.someNull = new Vertex(14, 15, v5, null, v2 , v4);
    this.allNull = new Vertex(14, 15, null, null, null, null);
    
    this.m1 = new MazeGame(new Random(27));
    this.m2 = new MazeGame(100, 60, new Random(40));
    this.m3 = new MazeGame(1, 1, new Random(2));
    this.m4 = new MazeGame(2, 1, new Random(1));
    
  }
  
  
  boolean testKruskals(Tester t) {
    this.initConditions();
    return t.checkExpect(m4.kruskals(someEdges, uf1), new ArrayList<Edge>(Arrays.asList(e5)));
  }
  
 
  void testOnKeyEvent(Tester t) {
    this.initConditions();
    t.checkExpect(m3, m3);
    m2.onKeyEvent("b");
    t.checkExpect(m3.vertexWorklist, null);
    
    t.checkExpect(m3, m3);
    m2.onKeyEvent("d");
    t.checkExpect(m3.vertexWorklist, null);
    
    t.checkExpect(m4, m4);
    m4.onKeyEvent("b");
    Queue<Vertex> queue1 = new Queue<Vertex>();
    queue1.add(new Vertex(0, 0, new ArrayList<Edge>(), Color.green, null, null, null, null));
    t.checkExpect(m4.vertexWorklist, queue1);
    
    t.checkExpect(m4, m4);
    m4.onKeyEvent("d");
    Stack<Vertex> stack1 = new Stack<Vertex>();
    stack1.add(new Vertex(0, 0, new ArrayList<Edge>(), Color.green, null, null, null, null));
    t.checkExpect(m4.vertexWorklist, stack1);
  }
  
  void testOnTick(Tester t) {
    
    this.initConditions();
    t.checkExpect(m4, m4);
    m4.onTick();
    t.checkExpect(m4.vertexWorklist, null);
    t.checkExpect(m4.ancestor, null);
    ArrayList<ArrayList<Vertex>> v1 = new ArrayList<ArrayList<Vertex>>();
    ArrayList<Vertex> v2 = new ArrayList<Vertex>();
    v2.add(new Vertex(0, 0, new ArrayList<Edge>(), Color.GREEN, null, null, null, null));
    v2.add(new Vertex(1, 0, new ArrayList<Edge>(), Color.MAGENTA, null, null, null, null));
    v1.add(v2);
    t.checkExpect(m4.vertices, v1);
    t.checkExpect(m4.visited, new ArrayDeque<Vertex>());
    t.checkExpect(m4.back, new HashMap<Vertex, Vertex>());
    
    t.checkExpect(m3, m3);
    m3.onTick();
    t.checkExpect(m3.vertexWorklist, null);
    t.checkExpect(m3.ancestor, null);
    ArrayList<ArrayList<Vertex>> v3 = new ArrayList<ArrayList<Vertex>>();
    ArrayList<Vertex> v4 = new ArrayList<Vertex>();
    v4.add(new Vertex(0, 0, new ArrayList<Edge>(), Color.MAGENTA, null, null, null, null));
    v3.add(v4);
    t.checkExpect(m3.vertices, v3);
    
    
  }
  
  void testAdd(Tester t) {
    this.initConditions();
    t.checkExpect(emptyVerts, emptyVerts);
    emptyVerts.add(v1);    
    t.checkExpect(emptyVerts, oneVert);
    
    t.checkExpect(someVerts, someVerts);
    someVerts.add(v3);
    Stack<Vertex> someVertsMod = new Stack<Vertex>();
    someVertsMod.add(v1);
    someVertsMod.add(v2);
    someVertsMod.add(v3);
    t.checkExpect(someVerts, someVertsMod);
    
    t.checkExpect(oneInt, oneInt);
    oneInt.add(4);
    Queue<Integer> oneIntMod = new Queue<Integer>();
    oneIntMod.add(0);
    oneIntMod.add(4);
    t.checkExpect(oneInt, oneIntMod);
  }
  
  boolean testRemove(Tester t) {
    this.initConditions();
    return t.checkExpect(someVerts.remove(), v2)
        && t.checkExpect(oneVert.remove(), v1)
        && t.checkExpect(someInts.remove(), 2)
        && t.checkExpect(oneInt.remove(), 0);
  }
  
  boolean testisEmpty(Tester t) {
    this.initConditions();
    return t.checkExpect(someInts.isEmpty(), false)
        && t.checkExpect(oneInt.isEmpty(), false)
        && t.checkExpect(emptyInts.isEmpty(), true)
        && t.checkExpect(someVerts.isEmpty(), false)
        && t.checkExpect(oneVert.isEmpty(), false)
        && t.checkExpect(emptyVerts.isEmpty(), true);   
  }
  
  boolean testSize(Tester t) {
    this.initConditions();
    return t.checkExpect(someInts.size(), 3)
        && t.checkExpect(oneInt.size(), 1)
        && t.checkExpect(emptyInts.size(), 0)
        && t.checkExpect(someVerts.size(), 2)
        && t.checkExpect(oneVert.size(), 1)
        && t.checkExpect(emptyVerts.size(), 0);
  }
  
  boolean testgetVertexNeighbors(Tester t) {
    this.initConditions();
    ArrayList<Vertex> noNeighbors = new ArrayList<Vertex>();
    ArrayList<Vertex> someNeighbors = new ArrayList<Vertex>();
    someNeighbors.add(v4);
    someNeighbors.add(v5);
    someNeighbors.add(v2);
    ArrayList<Vertex> allNeighbors = new ArrayList<Vertex>();
    allNeighbors.add(v4);
    allNeighbors.add(v5);
    allNeighbors.add(v2);
    allNeighbors.add(v3);
    return t.checkExpect(v1.getVertexNeighbors(), noNeighbors)
        && t.checkExpect(someNull.getVertexNeighbors(), someNeighbors)
        && t.checkExpect(allNull.getVertexNeighbors(), noNeighbors)
        && t.checkExpect(noneNull.getVertexNeighbors(), allNeighbors);
  }
  
  void testjoinEdgeCell(Tester t) {
    initConditions();
    e1.joinEdgeCell();
    t.checkExpect(v2.bottom, v1);
    t.checkExpect(v1.top, v2);

    e2.joinEdgeCell();
    t.checkExpect(v4.right, v1);
    t.checkExpect(v1.left, v4);

    e3.joinEdgeCell();
    t.checkExpect(v5.left, v1);
    t.checkExpect(v1.right, v5);

    e4.joinEdgeCell();
    t.checkExpect(v3.top, v1);
    t.checkExpect(v1.bottom, v3);
  }
  
  boolean testFind(Tester t) {
    this.initConditions();
    MazeGame newWorld = new MazeGame(10, 10, new Random(50));
    
    
    Vertex v1 = newWorld.vertices.get(0).get(0);
    Vertex v2 = newWorld.vertices.get(0).get(1);
    Vertex v3 = newWorld.vertices.get(1).get(2);
    Vertex v4 = newWorld.vertices.get(2).get(2);
    
    newWorld.reps.put(v3, v4);
   
    return t.checkExpect(newWorld.uf.find(v1), v1) 
        && t .checkExpect(newWorld.uf.find(v2), v2)
        && t.checkExpect(newWorld.uf.find(v3), v4);
  }

  void testUnion(Tester t) {
    this.initConditions();
    MazeGame newGame = new MazeGame(10, 10, new Random(50));
    
    Vertex v1 = newGame.vertices.get(0).get(0);
    Vertex v2 = newGame.vertices.get(0).get(1);
    Vertex v3 = newGame.vertices.get(1).get(2);
    Vertex v4 = newGame.vertices.get(2).get(2);
    
    newGame.uf.union(v1, v2);
    newGame.reps.put(v3, v4);
    newGame.uf.union(v2, v3);
    
    t.checkExpect(newGame.reps.get(v1), v2);
    t.checkExpect(newGame.reps.get(v2), v4);
    
  }
  
  void testInitVertices(Tester t) {
    this.initConditions();
    
    t.checkExpect(this.m3.vertices.size(), 1);
    t.checkExpect(this.m3.vertices.get(0).size(), 1);
  }
  
  
  boolean testCompare(Tester t) {
    this.initConditions();
    return t.checkExpect(new EdgeCompare().compare(e7, e5), 43)
        && t.checkExpect(new EdgeCompare().compare(e5, e6), -3);  
  }

  // tests the draw method
  void testDraw(Tester t) {
    this.initConditions();
    m2.bigBang(m2.bWidth * MazeGame.VERTEX_SIZE, m2.bHeight * MazeGame.VERTEX_SIZE, 0.001);
  }  
}





