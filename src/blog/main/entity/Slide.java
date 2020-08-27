package blog.main.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table
public class Slide {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column
	private String title;
	@Column
	private String linkPath;
	@Column
	private String image;
	@Column
	private String position;
	@Column
	private boolean isVisible;
	@Column
	private String linkName;
	
	
	public Slide() {
		
	}
	public Slide(String title, String liknPath, String image, String position, boolean isVisible, String linkName) {
		super();
		this.title = title;
		this.linkPath = liknPath;
		this.image = image;
		this.position = position;
		this.isVisible = isVisible;
		this.linkName = linkName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLinkPath() {
		return linkPath;
	}

	public void setLinkPath(String liknPath) {
		this.linkPath = liknPath;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	@Override
	public String toString() {
		return "Slide [title=" + title + ", position=" + position + ", isVisible=" + isVisible + "]";
	}
	
	
}
