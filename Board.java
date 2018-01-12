package com.algorithms.week4;

import java.util.Arrays;
import java.util.Iterator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Board {

	private int[][] board;
	private Board twin;
	private final int n;
	private boolean isGoal;
	private int hammingCount;
	private int manhattanDist;
	private int zeroX;
	private int zeroY;

	public Board(int[][] blocks) {
		// construct a board from an n-by-n array of blocks
		// (where blocks[i][j] = block in row i, column j)
		n = blocks[0].length;
		board = new int[n][];
		for (int i=0;i<n;i++) {
			board[i] = blocks[i].clone();
		}

		isGoal = true;
		hammingCount = 0;
		manhattanDist = 0;
		for (int i=0;i<n;i++) {
			for (int j=0;j<n;j++) {
				int goal = i*n + j + 1;
				if (i== n-1 && j==n-1) {
					// last tile
					goal=0;
				}

				if (goal != board[i][j]) {
					isGoal = false;

					// do NOT count 0 tile
					if (board[i][j]!=0) {
						int correctX = 0;
						int correctY =  board[i][j]%n -1;
						if (correctY == -1) {
							correctX = board[i][j]/n -1;
							correctY = board[i][j] - correctX*n - 1;
						} else {
							correctX = (board[i][j] - correctY - 1)/n;
						}

						int tempManhattanDist = Math.abs(correctX - i) + Math.abs(correctY- j);
						manhattanDist+= tempManhattanDist;
						hammingCount++;
					} else {
						zeroX = i;
						zeroY = j;
					}
				} else {
					// in case last tile is 0
					if (i== n-1 && j==n-1) {
						zeroX = i;
						zeroY = j;
					}
				}
			}
		}
	}

	public int dimension() {
    		// board dimension n
		return this.n;
    }

	public int hamming() {
		// number of blocks out of place
    		return this.hammingCount;
    }

	public int manhattan() {
		// sum of Manhattan distances between blocks and goal
		return this.manhattanDist;
	}

    	public boolean isGoal() {
    		// is this board the goal board?
    		return this.isGoal;
    }

    	public Board twin() {
    		if (this.twin != null) {
    			return twin;
    		}

    		// a board that is obtained by exchanging any pair of blocks
    		int dim = dimension();
    		int x1 = StdRandom.uniform(dim);
    		int y1 = StdRandom.uniform(dim);

    		// can't be 0 tile
    		while (x1==zeroX && y1==zeroY) {
    			x1 = StdRandom.uniform(dim);
        		y1 = StdRandom.uniform(dim);
    		}

    		int x2 = StdRandom.uniform(dim);
    		int y2 = StdRandom.uniform(dim);

    		// can't be 0 tile, can't be same tile
    		while ((x2==zeroX && y2==zeroY)
    				||(x2==x1 && y2==y1)) {
    			x2 = StdRandom.uniform(dim);
        		y2 = StdRandom.uniform(dim);
    		}

    		int[][] twinBoard = new int[dim][];
		for (int k=0;k<dim; k++) {
			twinBoard[k] = board[k].clone();
		}
    		int temp = twinBoard[x1][y1];
    		twinBoard[x1][y1] = twinBoard[x2][y2];
    		twinBoard[x2][y2] = temp;

    		twin = new Board(twinBoard);
    		return twin;
    }

    	@Override
    	public boolean equals(Object y) {
    		if (y == this) return true;
    		if (y == null) return false;

    		// does this board equal y?
    		if (y.getClass() != this.getClass()) return false;

    		Board that = (Board)y;
    		if (this.n != that.n) {
    			return false;
    		}
    		if (this.hammingCount!=that.hammingCount) {
    			return false;
    		}
    		if (this.manhattanDist != that.manhattanDist) {
    			return false;
    		}
    		if (this.isGoal != that.isGoal) {
    			return false;
    		}
    		if (this.zeroX != that.zeroX) {
    			return false;
    		}
    		if (this.zeroY != that.zeroY) {
    			return false;
    		}

    		boolean isSame =true;
    		int dim = dimension();
    		for (int i=0;i<dim;i++) {
    			for (int j=0;j<dim;j++) {
    				if (this.board[i][j] != that.board[i][j]) {
    					isSame = false;
    					break;
    				}
    			}
    		}

    		return isSame;
    }

    	public Iterable<Board> neighbors() {
		// all neighboring boards
    		return new NeighborIterable() ;
	}

    	private class NeighborIterable implements Iterable<Board> {
		@Override
		public Iterator<Board> iterator() {
			return new NeighborIterator();
		}
    	}

    	private class NeighborIterator implements Iterator<Board> {

    		private final Board[] neighbors;
    		private int idx;

    		public NeighborIterator() {
    			int size = 0;
    			int dim = dimension();
    			Board[] tempNeighbor = new Board[4];
    			for (int i=-1; i<2 ;i+=2 ) {
    				if ((zeroX + i>=0) && (zeroX +i <dim)) {
					// j=0, horizontal move
    					int[][] tempBoard = new int[dim][];
					for (int k=0;k<dim; k++) {
						tempBoard[k] = board[k].clone();
					}

					int temp = tempBoard[zeroX + i][zeroY];
					tempBoard[zeroX + i][zeroY] = tempBoard[zeroX][zeroY];
					tempBoard[zeroX][zeroY] = temp;

					tempNeighbor[size++] = new Board(tempBoard);
				}
    			}

    			for (int j=-1; j<2;j+=2 ) {
    				if ( (zeroY + j>=0) && (zeroY +j <dim) ) {
	    				// i=0, vertical move
    					int[][] tempBoard = new int[dim][];
    					for (int k=0;k<dim; k++) {
    						tempBoard[k] = board[k].clone();
    					}
	    				int temp = tempBoard[zeroX][zeroY + j];
	    				tempBoard[zeroX][zeroY + j] = tempBoard[zeroX][zeroY];
	    				tempBoard[zeroX][zeroY] = temp;

	    				tempNeighbor[size++] = new Board(tempBoard);
    				}
			}

    			neighbors = Arrays.copyOf(tempNeighbor, size);
    		}

		@Override
		public boolean hasNext() {
			return idx!=neighbors.length;
		}

		@Override
		public Board next() {
			if (!hasNext()) {
				throw new java.util.NoSuchElementException();
			}
			return neighbors[idx++];
		}

    	}


    @Override
    public String toString() {
    		// string representation of this board (in the output format specified below)
    		StringBuilder sb = new StringBuilder();
    		sb.append(this.n);
    		sb.append("\n");
    		for (int i=0;i<n;i++) {
    			sb.append(" ");
    			for (int j=0;j<n;j++) {
    				sb.append(String.format("%3d ", this.board[i][j]));
    			}
    			sb.append("\n");
    		}

    		return sb.toString();
    }




    public static void main(String[] args) {
    		// unit tests (not graded)
    		In in = new In(args[0]);
	    int n = in.readInt();
	    int[][] blocks = new int[n][n];
	    for (int i = 0; i < n; i++)
	        for (int j = 0; j < n; j++)
	            blocks[i][j] = in.readInt();
	    Board initial = new Board(blocks);
	    StdOut.println("initial");
	    StdOut.print(initial);

	    //assert initial.hammingCount ==4;
	    //assert initial.manhattanDist ==3;
	    StdOut.println("hamming:");
	    StdOut.println(initial.hamming());
	    StdOut.println("manhattan:");
	    StdOut.println(initial.manhattan());

	    for (Board b : initial.neighbors()) {
	    		StdOut.print(b);
	    }


	    int trial = 10000;
	    int diff =0;
	    int zx = initial.zeroX;
	    int zy = initial.zeroY;
	    for (int i=0;i<trial;i++) {
	    		Board t = initial.twin();
	    		assert t.board[zx][zy]==0;

	    		for (int j=0;j<t.dimension();j++) {
	    			for (int k=0;k<t.dimension();k++) {
	    				if (t.board[j][k] != initial.board[j][k]) {
	    					diff++;
	    				}
	    			}
	    		}
	    }
	    assert diff==2;

    }
}
