package org.singh.npg;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	private static long goal16 = 1147797409030816545L;
	private static long goal9 = 2271560481L;
	
	static long byteArrayToLong(final byte[] byteArray) {
        long arrayAsLong = 0;
        for (int i = 0; i < byteArray.length; ++i) {
            arrayAsLong |= ((long)byteArray[i] << (i << 2));
        }
        return arrayAsLong;
    }
	
	static byte[] longToByteArray(long state, int numOfTiles) {
		byte[] b = new byte[numOfTiles];
		for(int i = 0; i < b.length; i++) {
			b[i] = (byte)(state >> (i << 2) & 0xF);
		}
		return b;
	}
	
	static List<State> getChildren(State parent, boolean[] tilesInSubset, int dimension, int numOfTiles) {
		List<State> states = new ArrayList<>();
		
		int posOfSpace = parent.getEmptyTilePos();
		if(posOfSpace % dimension != 0)
			states.add(moveLeftNode(parent, tilesInSubset, dimension, posOfSpace));
		if ((posOfSpace + 1) % dimension != 0)
			states.add(moveRightNode(parent, tilesInSubset, dimension, posOfSpace));
		if (posOfSpace >= dimension)
			states.add(moveUpNode(parent, tilesInSubset, dimension, posOfSpace));
		if (posOfSpace < numOfTiles - dimension)
			states.add(moveDownNode(parent, tilesInSubset, dimension, numOfTiles, posOfSpace));
		
		return states;
	}
	
	public static State moveLeftNode(State parent, boolean[] tilesInSubset, int dimension, int posOfSpace) {
        State child = new State(parent.h());
        int posTimes4 = posOfSpace << 2;
        long posMinusOneTimes4 = (posOfSpace - 1) << 2;// Swap tile with empty tile.
        
        long parentState = parent.getState();
        long space = (parentState >> posTimes4) & 0xF;
        long tile = (parentState >> posMinusOneTimes4) & 0xF;
        long zeroBitTile = (long)0xF << posMinusOneTimes4;
        child.setState(parentState & ~zeroBitTile | (tile << posTimes4) | (space << posMinusOneTimes4));

        if (tilesInSubset == null || tilesInSubset[(int)tile])
            child.setH((byte)(parent.h() + 1));

        child.setEmptyTilePos(parent.getEmptyTilePos() - 1);
        return child;
    }

    public static State moveRightNode(State parent, boolean[] tilesInSubset, int dimension, int posOfSpace) {
        int posPlusOne = posOfSpace + 1;
        State child = new State(parent.h());
        int posTimes4 = posOfSpace << 2;
        int posPlusOneTimes4 = posPlusOne << 2;// Swap tile with space.

        long parentState = parent.getState();
        long space = (parentState >> posTimes4) & 0xF;
        long tile = (parentState >> posPlusOneTimes4) & 0xF;
        long zeroBitTile = (long)0xF << posPlusOneTimes4;
        child.setState(parentState & ~zeroBitTile | (tile << posTimes4) | (space << posPlusOneTimes4));

        if (tilesInSubset == null || tilesInSubset[(int)tile])
            child.setH((byte)(parent.h() + 1));

        child.setEmptyTilePos(parent.getEmptyTilePos() + 1);
        return child;
    }

    public static State moveUpNode(State parent, boolean[] tilesInSubset, int dimension, int posOfSpace) {
        State child = new State(parent.h());
        int posTimes4 = posOfSpace << 2;
        int posMinusDimTimes4 = (posOfSpace - dimension) << 2;// Swap tile with space.

        long parentState = parent.getState();
        long space = (parentState >> posTimes4) & 0xF;
        long tile = (parentState >> posMinusDimTimes4) & 0xF;

        final long zeroBitTile = (long)0xF << posMinusDimTimes4;
        child.setState(parentState & ~zeroBitTile | (tile << posTimes4) | (space << posMinusDimTimes4));

        if (tilesInSubset == null || tilesInSubset[(int)tile])
            child.setH((byte)(parent.h() + 1));

        child.setEmptyTilePos(parent.getEmptyTilePos() - dimension);
        return child;
    }

    public static State moveDownNode(State parent, boolean[] tilesInSubset, int dimension, int numOfTiles, int posOfSpace) {
        State child = new State(parent.h());
        int posTimes4 = posOfSpace << 2;
        int posPlusDimTimes4 = (posOfSpace + dimension) << 2;

        long parentState = parent.getState();
        long space = (parentState >> posTimes4) & 0xF;
        long tile = (parentState >> posPlusDimTimes4) & 0xF;//Swap tile with space.

        final long zeroBitTile = (long)0xF << posPlusDimTimes4;
        child.setState(parentState & ~zeroBitTile | (tile << posTimes4) | (space << posPlusDimTimes4));

        if (tilesInSubset == null || tilesInSubset[(int)tile])
            child.setH((byte)(parent.h() + 1));

        child.setEmptyTilePos(parent.getEmptyTilePos() + dimension);
        return child;
    }
    
    public static long filter(long l, byte dummy, int numTiles) {
		long result = 0;
		byte[] b = new byte[numTiles];
		for(int i = 0; i < b.length; i++) {
			byte k = (byte)(l >> (i << 2) & 0xF);
			if(k == 0)
				b[i] = dummy;
			else
				b[i] = k;
			result |= ((long)b[i] << (i << 2));
		}
		return result;
	}
    
    static boolean isGoal16(byte[] b) {
    	return byteArrayToLong(b) == goal16;
    }
    
    static boolean isGoal16(long l) {
    	return l == goal16;
    }
    
    static boolean isGoal9(byte[] b) {
    	return byteArrayToLong(b) == goal9;
    }
}
