package blog.main.dao;

import java.util.List;

import blog.main.entity.Category;

public interface CategoryDAO {

	public List<Category> getCategoryList();
	
	public void saveCategory(Category category);
	
	public Category getCategory(int id);
	
	public void deleteCategory(int id);
	
	public List<Category> getCategoryForFilter();
	
	
	
}
