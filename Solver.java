package com.algorithms.week4;


import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

	private int movesToSolve;
	private final boolean isSolvable;
	private Board[] solution;


    	public Solver(Board initial) {
    		// find a solution to the initial board (using the A* algorithm)

    		if (initial == null) {
    			throw new java.lang.IllegalArgumentException();
    		}

    		Board twin = initial.twin();
    		isSolvable = runSolution(initial, twin);
    }

    	private boolean runSolution(Board initial, Board twin) {
    		MinPQ<SearchNode> minPQ1 = new MinPQ<>(new SearchNodeComparator());
    		MinPQ<SearchNode> minPQ2 = new MinPQ<>(new SearchNodeComparator());
    		SearchNode sn1 = new SearchNode(initial, 0, null);
    		SearchNode sn2 = new SearchNode(twin, 0, null);

    		minPQ1.insert(sn1);
    		minPQ2.insert(sn2);

    		boolean solvable = false;

    		// delMin and get all neighbors and insert them onto the pq
    		while (!minPQ1.isEmpty() && !minPQ2.isEmpty()) {
    			SearchNode rsn1 = minPQ1.delMin();
    			SearchNode rsn2 = minPQ2.delMin();


    			if (rsn1.getBoard().isGoal()) {
				solvable = true;
				movesToSolve = rsn1.getMoves();
				// need to include the initial board
				solution = new Board[movesToSolve+1];
				SearchNode tmp = rsn1;
				for (int i=movesToSolve; i>=0; i--) {
					solution[i] = tmp.getBoard();
					tmp = tmp.getPrev();
				}
				break;
			}

    			if (rsn2.getBoard().isGoal()) {
				solvable = false;
				break;
			}

    			for (Board b : rsn1.getNeighbors()) {
    				if (!rsn1.isSameWithPreviousBoard(b)) {

    					minPQ1.insert(new SearchNode(b, rsn1.getMoves()+1, rsn1));
    				}
    			}

    			for (Board b : rsn2.getNeighbors()) {
    				if (!rsn2.isSameWithPreviousBoard(b)) {
    					minPQ2.insert(new SearchNode(b, rsn2.getMoves()+1, rsn2));
    				}
    			}
    		}

    		return solvable;
	}


	public boolean isSolvable() {
    		// is the initial board solvable?
    		return this.isSolvable;
    }


    	public int moves() {
    		// min number of moves to solve initial board; -1 if unsolvable
    		if (!isSolvable()) {
    			return -1;
    		}
    		return movesToSolve;
    }


    	public Iterable<Board> solution() {
    		// sequence of boards in a shortest solution; null if unsolvable
    		if (!isSolvable()) {
    			return null;
    		}
    		return new SolutionIterable();
    	}

    	private class SolutionIterable implements Iterable<Board> {

		@Override
		public Iterator<Board> iterator() {
			return new SolutionIterator();
		}
    	}

    	private class SolutionIterator implements Iterator<Board> {
    		int idx = 0;
    		Board[] cpSolution;

    		public SolutionIterator() {
    			cpSolution = Arrays.copyOf(solution, solution.length);
    		}

		@Override
		public boolean hasNext() {
			return idx!=cpSolution.length;
		}

		@Override
		public Board next() {
			if (!hasNext()) {
				throw new java.util.NoSuchElementException();
			}
			return cpSolution[idx++];
		}
    	}

    	private class SearchNode {
    		private final Board board;
    		private final int moves;
    		private final SearchNode prev;
    		private final int manhattanPriority;

    		public SearchNode(Board board, int moves, SearchNode prev) {
    			this.board = board;
    			this.moves = moves;
    			this.prev = prev;
    			this.manhattanPriority = board.manhattan() + moves;
    		}

    		public Iterable<Board> getNeighbors() {
    			return board.neighbors();
    		}

    		public int getMoves() {
    			return moves;
    		}

    		public Board getBoard() {
    			return board;
    		}

    		public SearchNode getPrev() {
    			return prev;
    		}

    		public boolean isSameWithPreviousBoard(Board b) {
    			if (prev == null) {
    				return false;
    			}

    			return b.equals(prev.getBoard());
    		}

    		public int getManhattanPriority() {
    			return manhattanPriority;
    		}
    }

	private class SearchNodeComparator implements Comparator<SearchNode> {
		@Override
		public int compare(SearchNode o1, SearchNode o2) {
			return Integer.compare(o1.getManhattanPriority(), o2.getManhattanPriority());
		}
	}


    public static void main(String[] args) {
    		// solve a slider puzzle (given below)

    }
}
