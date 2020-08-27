package blog.main.dao;

import java.util.List;

import blog.main.entity.Blog;
import blog.main.entity.Comment;

public interface CommentDAO {

	
	public List<Comment> getCommentList();
	
	public Comment getCommentById(int id);
	
	public void saveComment(Comment comment);
	
	public void deleteComment(int id);
	
	public List<Comment> getCommentListByBlog(Blog blog);
}
