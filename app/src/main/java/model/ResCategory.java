package model;

public class ResCategory {
	public String id, title;

	public ResCategory() {		
	}

	public ResCategory(String id, String title) {		
		this.id = id;
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "ResCategory{" +
				"id='" + id + '\'' +
				", title='" + title + '\'' +
				'}';
	}
}
