package blog.main.dao;

import java.util.List;

import blog.main.entity.Slide;

public interface SlideDAO {

	public void saveSlide(Slide slide);
	
	public void deleteSlide(int id);
	
	public List<Slide> slideList();
	
	public Slide getSlideById(int id);
	
	public List<Slide> getSlideListForMainPage();
}
