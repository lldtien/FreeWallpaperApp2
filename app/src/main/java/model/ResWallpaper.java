package model;

import java.io.Serializable;

public class ResWallpaper implements Serializable {
	private static final long serialVersionUID = 1L;
	private String url, photoJson;
	private int width, height;

	public ResWallpaper() {
	}

	public ResWallpaper(String photoJson, String url, int width, int height) {		
		this.photoJson = photoJson;
		this.url = url;
		this.width = width;
		this.height = height;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPhotoJson() {
		return photoJson;
	}

	public void setPhotoJson(String photoJson) {
		this.photoJson = photoJson;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
