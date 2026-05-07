import java.util.*;

public class AStarDemo {

    // -----------------------------
    // Grid
    // -----------------------------
    static class Grid {
        int rows, cols;
        boolean[][] blocked;

        Grid(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
            blocked = new boolean[rows][cols];
        }

        boolean inBounds(int r, int c) {
            return r >= 0 && r < rows && c >= 0 && c < cols;
        }

        boolean isBlocked(int r, int c) {
            return blocked[r][c];
        }

        static Grid emptyGrid(int r, int c) {
            return new Grid(r, c);
        }

        static Grid withFixedObstacles(int r, int c) {
            Grid g = new Grid(r, c);

            int blockSize = r / 4;
            int startRow = r/2 - blockSize/2;
            int startCol = c/2 - blockSize/2;

            for (int i = startRow; i < startRow + blockSize; i++) {
                for (int j = startCol; j < startCol + blockSize; j++) {
                    g.blocked[i][j] = true;
                }
            }
            return g;
        }

        static Grid randomGrid(int r, int c, double chance) {
            Grid g = new Grid(r, c);
            Random rand = new Random();
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    if (rand.nextDouble() < chance)
                        g.blocked[i][j] = true;
                }
            }
            return g;
        }
    }

    // -----------------------------
    // Cell
    // -----------------------------
    static class Cell {
        int x, y;
        double g = Double.POSITIVE_INFINITY;
        double h;
        Cell parent;

        Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        double f() {
            return g + h;
        }
    }

    // -----------------------------
    // Heuristics
    // -----------------------------
    static double manhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    static double chebyshev(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x1-x2), Math.abs(y1-y2));
    }

    static double octile(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1-x2);
        int dy = Math.abs(y1-y2);
        return (dx + dy) + (Math.sqrt(2)-2)*Math.min(dx,dy);
    }

    // -----------------------------
    // A* Algorithm
    // -----------------------------
    static class AStar {
        Grid grid;
        int heuristicType; // 0=Manhattan, 1=Chebyshev, 2=Octile
        int movementType;  // same indexing
        int nodesExpanded = 0;

        AStar(Grid grid, int heuristicType, int movementType) {
            this.grid = grid;
            this.heuristicType = heuristicType;
            this.movementType = movementType;
        }

        double calcHeuristic(int x1, int y1, int x2, int y2) {
            if (heuristicType == 0) return manhattan(x1,y1,x2,y2);
            if (heuristicType == 1) return chebyshev(x1,y1,x2,y2);
            return octile(x1,y1,x2,y2);
        }

        List<Cell> search(Cell startCell, Cell goalCell) {
            int rows = grid.rows;
            int cols = grid.cols;
            Cell[][] nodes = new Cell[rows][cols];

            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    nodes[i][j] = new Cell(i,j);

            Cell start = nodes[startCell.x][startCell.y];
            Cell goal = nodes[goalCell.x][goalCell.y];

            PriorityQueue<Cell> open = new PriorityQueue<>(
                    new Comparator<Cell>() {
                        public int compare(Cell a, Cell b) {
                            return Double.compare(a.f(), b.f());
                        }
                    }
            );

            boolean[][] closed = new boolean[rows][cols];

            start.g = 0;
            start.h = calcHeuristic(start.x, start.y, goal.x, goal.y);
            open.add(start);

            int[][] dirs4 = { {1,0},{-1,0},{0,1},{0,-1} };
            int[][] dirs8 = {
                    {1,0},{-1,0},{0,1},{0,-1},
                    {1,1},{1,-1},{-1,1},{-1,-1}
            };

            int[][] dirs = (movementType == 0 ? dirs4 : dirs8);

            while (!open.isEmpty()) {
                Cell current = open.poll();
                if (closed[current.x][current.y]) continue;

                closed[current.x][current.y] = true;
                nodesExpanded++;

                if (current == goal)
                    return buildPath(goal);

                for (int[] d : dirs) {
                    int nx = current.x + d[0];
                    int ny = current.y + d[1];

                    if (!grid.inBounds(nx, ny)) continue;
                    if (grid.isBlocked(nx, ny)) continue;
                    if (closed[nx][ny]) continue;

                    Cell neighbor = nodes[nx][ny];
                    double newG = current.g + movementCost(current, neighbor);

                    if (newG < neighbor.g) {
                        neighbor.g = newG;
                        neighbor.h = calcHeuristic(nx, ny, goal.x, goal.y);
                        neighbor.parent = current;
                        open.add(neighbor);
                    }
                }
            }

            return Collections.emptyList();
        }

        double movementCost(Cell a, Cell b) {
            int dx = Math.abs(a.x - b.x);
            int dy = Math.abs(a.y - b.y);

            if (movementType == 2) {
                if (dx == 1 && dy == 1)
                    return Math.sqrt(2);
                return 1;
            }

            return 1;
        }

        List<Cell> buildPath(Cell goal) {
            List<Cell> path = new ArrayList<>();
            Cell c = goal;
            while (c != null) {
                path.add(c);
                c = c.parent;
            }
            Collections.reverse(path);
            return path;
        }
    }

    // -----------------------------
    // Averages
    // -----------------------------
    static class AverageResult {
        String name;
        double totalCost = 0;
        int totalExpanded = 0;
        long totalRuntime = 0;
        int count = 0;

        AverageResult(String name) {
            this.name = name;
        }

        void add(double cost, int expanded, long time) {
            totalCost += cost;
            totalExpanded += expanded;
            totalRuntime += time;
            count++;
        }

        double avgCost() { return totalCost / count; }
        double avgExpanded() { return (double) totalExpanded / count; }
        double avgRuntime() { return (double) totalRuntime / count; }
    }

    // -----------------------------
    // Main
    // -----------------------------
    public static void main(String[] args) {

        int size = 1000;
        int size2 = 2000;

        Grid[] grids = {
                Grid.emptyGrid(size, size),
                Grid.withFixedObstacles(size, size),
                Grid.randomGrid(size, size, 0.15),
                Grid.randomGrid(size, size, 0.20),
                Grid.randomGrid(size, size, 0.25),
                Grid.randomGrid(size, size, 0.30),
                Grid.randomGrid(size, size, 0.35),
                Grid.randomGrid(size2, size2, 0.15),
                Grid.randomGrid(size2, size2, 0.20),
                Grid.randomGrid(size2, size2, 0.25),
                Grid.randomGrid(size2, size2, 0.30),
                Grid.randomGrid(size2, size2, 0.35)
        };

        String[] gridNames = {
                "Empty Grid","Fixed Obstacle Grid",
                "Random Grid1","Random Grid2","Random Grid3",
                "Random Grid4","Random Grid5",
                "Random Grid6","Random Grid7","Random Grid8",
                "Random Grid9","Random Grid10"
        };

        String[] heuristicNames = { "Manhattan", "Chebyshev", "Octile" };

        AverageResult[] averages = {
                new AverageResult("Manhattan"),
                new AverageResult("Chebyshev"),
                new AverageResult("Octile")
        };

        Cell start = new Cell(0,0);
        Cell goal1 = new Cell(size-1, size-1);
        Cell goal2 = new Cell(size2-1, size2-1);

        for (int gi = 0; gi < grids.length; gi++) {
            System.out.println("\n===== " + gridNames[gi] + " =====");

            for (int h = 0; h < 3; h++) {
                System.out.println("\n--- " + heuristicNames[h] + " ---");

                AStar astar = new AStar(grids[gi], h, h);

                long t0 = System.nanoTime();
                List<Cell> path = astar.search(start, gi < 7 ? goal1 : goal2);
                long t1 = System.nanoTime();

                double cost = 0;
                for (int i = 1; i < path.size(); i++)
                    cost += astar.movementCost(path.get(i-1), path.get(i));

                averages[h].add(cost, astar.nodesExpanded, (t1 - t0)/1_000_000);

                if (path.isEmpty()) {
                    System.out.println("  No path found.");
                } else {
                    System.out.println("  Path length: " + path.size());
                    System.out.println("  Path cost: " + String.format("%.2f", cost));
                    System.out.println("  Nodes expanded: " + astar.nodesExpanded);
                    System.out.println("  Runtime: " + ((t1 - t0)/1_000_000) + " ms");
                }
            }
        }

        System.out.println("\n===== Average Results per Heuristic =====");
        for (AverageResult avg : averages) {
            System.out.println(avg.name + ":");
            System.out.println("  Avg Path Cost: " + String.format("%.2f", avg.avgCost()));
            System.out.println("  Avg Nodes Expanded: " + String.format("%.2f", avg.avgExpanded()));
            System.out.println("  Avg Runtime: " + String.format("%.2f", avg.avgRuntime()) + " ms");
        }
    }
}