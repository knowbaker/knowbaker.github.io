package org.singh.npg;

public class State {
	private long state;
	private byte g;
	private byte h;
	private int emptyTilePos;
	public char direction = 'X';
	public State(byte cost) {
		this.h = cost;
	}
	public State(long state, byte cost, int emptyTilePos) {
		this.state = state;
		this.h = cost;
		this.emptyTilePos = emptyTilePos;
	}
	public byte g() {
		return g;
	}
	public void setG(byte g) {
		this.g = g;
	}
	public byte h() {
		return h;
	}
	public void setH(byte h) {
		this.h = h;
	}
	public long getState() {
		return state;
	}
	public void setState(long state) {
		this.state = state;
	}
	public int getEmptyTilePos() {
		return emptyTilePos;
	}
	public void setEmptyTilePos(int emptyTilePos) {
		this.emptyTilePos = emptyTilePos;
	}
	@Override
	public String toString() {
		return state + "";
	}
 }
