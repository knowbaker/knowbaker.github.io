package org.singh.npg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author psingh
 */
public class DBGenerator {
	private static byte initCost = 0;
	private long startState;
	private Map<Long, Byte> map;
	private Map<Long, Byte> db;
	private int dimension;
	private int numOfTiles;
	private byte dummy;
	private boolean[] pattern;
	
	public DBGenerator(byte[] startState) {
		init(startState);
	}

	public DBGenerator(byte[] startState, byte dummy) {
		init(startState);
		this.dummy = dummy;
		createPattern(startState);
	}
	
	private void init(byte[] startState) {
		if(startState == null) throw new IllegalArgumentException("Start State may not be null");
		this.startState = Utils.byteArrayToLong(startState);
		this.map = new TreeMap<>();
		this.dimension = (int)Math.sqrt(startState.length);
		this.numOfTiles = startState.length;
	}

	public void generate() {
		bfs();
		filter();
	}

	public void bfs() {
		Queue<State> queue = new ArrayDeque<>(); 
		State start = new State(startState, initCost, numOfTiles - 1);
		start.direction = 'X';
		queue.add(start);
		put(startState, initCost);//startState is discovered
		while(!queue.isEmpty()) {
			State state = queue.poll();
			if(state == null)
				return;
			
			List<State> children = Utils.getChildren(state, pattern, dimension, numOfTiles);
			
			for(State child : children) {
				byte newCost = child.h();
				Byte savedCost = get(child.getState());
				if(savedCost == null || newCost < savedCost) {//if child not discovered or lower cost child not discovered
					long childState = child.getState();
					queue.add(child);
					put(childState, newCost);//label as discovered
				}
			}
		}
	}
	
	private void filter() {
		db = new TreeMap<>();
		Set<Map.Entry<Long, Byte>> entries = map.entrySet();
		for(Map.Entry<Long, Byte> entry : entries) {
			long key = Utils.filter(entry.getKey(), dummy, numOfTiles);
			byte val = entry.getValue();
			Byte nVal = db.get(key);
			if(nVal == null || val < nVal) {
				db.put(key, val);
			}
		}
	}
	
	private Byte get(long child) {
		return map.get(child);
	}

	private void put(long child, byte newPathCost) {
		map.put(child, newPathCost);
	}
	
	private void createPattern(byte[] startState) {
		pattern = new boolean[startState.length];
		for(int i = 0; i < startState.length; i++) {
			if(startState[i] != dummy && startState[i] != 0) {
				pattern[startState[i]] = true;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		DBGenerator dbGen = new DBGenerator(new byte[] {1,2,3,0});
//		DBGenerator dbGen = new DBGenerator(new byte[] {1,2,2,2,2,2,2,2,0}, (byte)2);
//		DBGenerator dbGen = new DBGenerator(new byte[] {1,2,3,3,3,3,3,3,0}, (byte)3);
//		DBGenerator dbGen = new DBGenerator(new byte[] {1,2,3,4,4,4,4,4,0}, (byte)4);
//		DBGenerator dbGen = new DBGenerator(new byte[] {1,2,2,2,5,6,2,2,9,10,2,2,13,2,2,0}, (byte)2);
//		DBGenerator dbGen = new DBGenerator(new byte[] {1,1,1,1,1,1,7,8,1,1,11,12,1,14,15,0}, (byte)1);
//		DBGenerator dbGen = new DBGenerator(new byte[] {1,2,3,4,5,5,5,5,5,5,5,5,5,5,5,0}, (byte)5);
		dbGen.bfs();
		System.out.println("Removing Duplicates...");
		Map<Long, Byte> map = dbGen.map;
		Map<Long, Byte> nMap = new TreeMap<>();
		Set<Map.Entry<Long, Byte>> entries = map.entrySet();
		for(Map.Entry<Long, Byte> entry : entries) {
			long key = Utils.filter(entry.getKey(), dbGen.dummy, dbGen.numOfTiles);
			byte val = entry.getValue();
			
			
			Byte nVal = nMap.get(key);
//			if(key == 148954212050473236L){
//				System.out.println("Key: " + key + " Val: " + val + " NVal: " + nVal);
//			}
			
			if(nVal == null || val < nVal) {
				nMap.put(key, val);
			}
		}
		System.out.println("-----------");
//		Set<Long> s = new TreeSet<Long>(nMap.keySet());
		Set<Map.Entry<Long, Byte>> s = nMap.entrySet();
//		FileWriter f = new FileWriter(new File("C:\\temp\\Test\\js\\ng-gsap-3\\Data-dbgen-new-1Of9.txt"));
//		FileWriter f = new FileWriter(new File("C:\\temp\\Test\\js\\ng-gsap-3\\Data-dbgen-new-2Of9.txt"));
//		FileWriter f = new FileWriter(new File("C:\\temp\\Test\\js\\ng-gsap-3\\Data-dbgen-new-3Of9.txt"));
//		FileWriter f = new FileWriter(new File("C:\\temp\\Test\\js\\ng-gsap-3\\Data-dbgen-new-6Of16.txt"));
//		for(Long e : s) {
		for(Map.Entry<Long, Byte> e : s) {
			System.out.println(Arrays.toString(Utils.longToByteArray(e.getKey(), 4)) + " " + e.getValue());
//			f.write(e + " " + nMap.get(e) + "\n");
//			f.write(e.getKey() + " " + e.getValue() + "\r\n");
//			System.out.println(e + " " + nMap.get(e));
		}
//		if(f != null) {
//        	f.close();
//        }
		System.out.println(dbGen.map.size());
		System.out.println(nMap.size());
	}
}
