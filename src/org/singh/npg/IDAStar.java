package org.singh.npg;

import java.util.List;

public class IDAStar {
	private static final byte INFINITY = Byte.MAX_VALUE;
	private static final byte FOUND = -1;
	private static final byte NOT_FOUND = -2;
	private int numOfTiles;
	private int dimension;

	public IDAStar(int numOfTiles) {
		this.numOfTiles = numOfTiles;
		this.dimension = (int)Math.sqrt(numOfTiles);
	}
	
	public byte work(State start) {
		start.setG((byte)0);
		byte bound = (byte) (start.g() + start.h());//f = g + h, g = 0 for start or goal state.
		byte t = INFINITY;
		while(true) {
			t = dfs(start, bound);
			if(t == FOUND)
				return FOUND;
			if(t == INFINITY)
				return NOT_FOUND;
			bound = t;
		}
	}
	
	private byte dfs(State state, byte bound) {
		byte f = (byte) (state.g() + state.h());//f = g + h
		if(f > bound)
			return f;
		if(Utils.isGoal16(state.getState()))
			return FOUND;
		
		byte min = INFINITY;
		List<State> children = Utils.getChildren(state, null, dimension, numOfTiles);
		for(State child : children) {
			child.setG((byte) (state.g() +  1));
			byte t = dfs(child, bound);
			if(t == FOUND)
				return FOUND;
			if(t < min)
				min = t;
		}
		return min;
	}
	
	private byte h(long state) {
		return 0;
	}
}
