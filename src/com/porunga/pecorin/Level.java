package com.porunga.pecorin;

public class Level {

	private String currentPoint;
	private String leveledUp;
	private String levelName;
	private String imageUrl;
	private String badgeType;
	
	public Level(String currentPoint, String levelName, String imageUrl, String badgeType, String leveledUp) {
		this.currentPoint = currentPoint;
		this.leveledUp = leveledUp;
		this.levelName = levelName;;
		this.imageUrl = imageUrl;
		this.badgeType = badgeType;
	}
		
	public String getCurrentPoint() {
		return currentPoint;
	}
	
	public String getLeveledUp() {
		return leveledUp;
	}

	public String getLevelName() {
		return levelName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getBadgeType() {
		return badgeType;
	}	

}
