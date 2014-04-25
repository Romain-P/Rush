package fr.rushland.server.games;

import java.util.List;

public class GameType {
	public String name;
	public String waitMap;
	public String map;
	public List<String> teamNames;
	public List<String> teamPrefixes;
	public List<String> teamColours;
	public List<Integer> teamMaxSizes;
	public List<int[]> waitLocs;
	public List<int[]> locs;

	public GameType(String name, String waitMap, String map, List<String> teamNames, 
			List<String> teamPrefixes, List<String> teamColours, List<int[]> waitLocs, List<int[]> locs) {
		this.name = name;
		this.waitMap = waitMap;
		this.map = map;
		this.teamNames = teamNames;
		this.teamPrefixes = teamPrefixes;
		this.teamColours = teamColours;
		this.waitLocs = waitLocs;
		this.locs = locs;
	}
}