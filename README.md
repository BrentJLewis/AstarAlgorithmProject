A* Pathfinding Algorithm
Overview

This project was created as a final project for an Algorithms course and implements the A* (A-Star) pathfinding algorithm in Java. The program compares different heuristic functions and movement strategies across multiple grid environments to evaluate pathfinding performance, runtime efficiency, and node expansion behavior.

The implementation tests the algorithm on:

Empty grids
Fixed obstacle grids
Randomly generated obstacle grids of varying densities
Large-scale grid environments

The project measures:

Path cost
Path length
Nodes expanded
Runtime performance
Technologies Used
Java
Object-Oriented Programming
Priority Queues
Heuristic Search Algorithms
Data Structures and Algorithms
Features
A* Pathfinding Implementation
Uses the A* search algorithm for shortest path discovery
Supports multiple heuristic functions
Supports different movement types
Tracks performance statistics during execution
Grid Generation

The program generates several types of environments:

Empty grids
Fixed obstacle grids
Random obstacle grids with configurable densities
Heuristic Functions

The project compares three heuristics:

Manhattan Distance
Chebyshev Distance
Octile Distance
Movement Types

The algorithm supports:

4-directional movement
8-directional movement
Diagonal movement with weighted costs
Performance Metrics

For each test case, the program records:

Path length
Total path cost
Number of nodes expanded
Runtime in milliseconds

The program also calculates average results for each heuristic across all test environments.

Program Structure
Class	Purpose
Grid	Creates and manages grid environments
Cell	Represents nodes used by the A* algorithm
AStar	Implements the A* search algorithm
AverageResult	Stores and calculates average statistics
Grid Configurations

The project tests the algorithm on:

1000x1000 grids
2000x2000 grids
Random obstacle densities from 15% to 35%

These larger environments were used to compare scalability and heuristic efficiency under different conditions.

Algorithm Details
A* Search

The algorithm uses:

g(n) for movement cost from the start node
h(n) as the heuristic estimate to the goal
f(n) = g(n) + h(n) for node prioritization

A priority queue is used to efficiently select the next node with the lowest estimated total cost.

Heuristic Comparisons
Manhattan Distance

Best suited for:

4-directional movement
Grid-based movement without diagonals
Chebyshev Distance

Best suited for:

8-directional movement
Uniform movement cost
Octile Distance

Best suited for:

Diagonal movement with weighted diagonal costs
Running the Program
Compile
javac AStarDemo.java
Run
java AStarDemo
Educational Purpose

This project was designed to demonstrate:

Heuristic search algorithms
Pathfinding optimization
Time and space complexity analysis
Priority queue usage
Performance benchmarking
Grid-based traversal algorithms
Output

The program outputs:

Path statistics for each heuristic
Runtime measurements
Node expansion counts
Average performance results across all test grids
Disclaimer

This project was created for academic and educational purposes as part of an Algorithms course final project.