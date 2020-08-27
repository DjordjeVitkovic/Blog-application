package blog.main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blog.main.dao.BlogDAO;
import blog.main.dao.CategoryDAO;
import blog.main.dao.CommentDAO;
import blog.main.dao.ContactDAO;
import blog.main.dao.SlideDAO;
import blog.main.dao.TagDAO;
import blog.main.dao.UserDAO;
import blog.main.entity.Blog;
import blog.main.entity.Category;
import blog.main.entity.Comment;
import blog.main.entity.Contact;
import blog.main.entity.Slide;
import blog.main.entity.User;

@Controller
@RequestMapping("/")
public class FrontController {

	@Autowired
	private ContactDAO contactDAO;
	@Autowired
	private SlideDAO slideDAO;
	@Autowired
	private CommentDAO commentDAO;
	@Autowired
	private BlogDAO blogDAO;
	@Autowired
	private CategoryDAO categoryDAO;
	@Autowired
	private TagDAO tagDAO;
	@Autowired
	private UserDAO userDAO;

	@RequestMapping({ "/index-page", "/" })
	public String getIndexPage(Model model) throws ParseException, IOException {
		
		List<Blog> blogList = blogDAO.getThreeImportantBlogs();

		getDateTimeDifference(blogList);	
		
//		for (Blog blog : blogList) {
//		
//		blog.setIm(encodeFileToBase64Binary("/Users/djordjevitkovic/Desktop/images/"+blog.getImage()));
//		
//					
//		}
		
//		System.out.println(blogList.get(0).getIm());
//		System.out.println(blogList.get(1).getIm());
//		System.out.println(blogList.get(2).getIm());
		
		
		// saljem tri liste na index stranu
		model.addAttribute("slideList", slideDAO.getSlideListForMainPage());
		// tri najnovija oznacena sa important
		model.addAttribute("blogList", blogList);
		// 12 za donji slajder
		model.addAttribute("twelveList", blogDAO.getTwelveBlogsForIndexPage());
		
		model.addAttribute("categoryList", categoryDAO.getCategoryList());

		return "front/index-page";
	}

	@RequestMapping("/contact-page")
	public String getContacPage(Model model) {

		model.addAttribute("contact", new Contact());
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());

		return "front/contact-page";
	}

	@RequestMapping("/contact-save")
	public String saveContact(@ModelAttribute Contact contact) {

		contact.setDate();
		contactDAO.saveContact(contact);

		return "redirect: contact-page";
	}

	@RequestMapping("/comment-save")
	public String commentSave(@ModelAttribute Comment comment) {

		comment.setDate();
		commentDAO.saveComment(comment);

		return "redirect: contact-page";
	}

	@RequestMapping("/blog-list-page")
	public String getBlogListPage(@RequestParam int page, Model model) throws ParseException {

		List<Blog> blogList = blogDAO.blogListForPagination(page);

		getDateTimeDifference(blogList);
		
		model.addAttribute("page", page);
		model.addAttribute("blogList", blogList);
		model.addAttribute("size", blogDAO.getCountBlogs());
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "front/blog-list-page";
	}
	
	@RequestMapping("/blog-list-author")
	public String getBlogListPageByAuthor(@RequestParam String username,@RequestParam int page, Model model) throws ParseException {

		List<Blog> blogList = blogDAO.getBlogListByAuthor(username, page);
		User user = userDAO.getUserByUsername(username);
		
		getDateTimeDifference(blogList);
		
		model.addAttribute("user", user);
		model.addAttribute("page", page);
		model.addAttribute("blogList", blogList);
		model.addAttribute("size", blogDAO.getCountBlogByAuthor(username));
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "front/blog-list-author";
	}

	@RequestMapping("/blog-list-page-change")
	public String changePage(@RequestParam int page, @RequestParam String type, Model model) throws ParseException {

		List<Blog> list;
		

		if (type.equalsIgnoreCase("left")) {

			page--;
			list = blogDAO.blogListForPagination(page);
			getDateTimeDifference(list);
			model.addAttribute("blogList", list);

		} else if (type.equalsIgnoreCase("right")) {

			page++;
			list = blogDAO.blogListForPagination(page);
			getDateTimeDifference(list);
			model.addAttribute("blogList", list);

		}
		model.addAttribute("page", page);
		model.addAttribute("size", blogDAO.getCountBlogs());
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "redirect: blog-list-page";
	}
	
	@RequestMapping("/blog-list-author-change")
	public String changePageAuthor(@RequestParam String username,@RequestParam int page, @RequestParam String type, Model model) throws ParseException {

		List<Blog> list;
		User user = userDAO.getUserByUsername(username);

		if (type.equalsIgnoreCase("left")) {

			page--;
			
			list = blogDAO.getBlogListByAuthor(username, page);
			getDateTimeDifference(list);
			model.addAttribute("blogList", list);

		} else if (type.equalsIgnoreCase("right")) {

			page++;
			list = blogDAO.getBlogListByAuthor(username, page);
			getDateTimeDifference(list);
			model.addAttribute("blogList", list);

		}
		model.addAttribute("user", user);
		model.addAttribute("page", page);
		model.addAttribute("size", blogDAO.getCountBlogs());
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "redirect: blog-list-author";
	}

	@RequestMapping("/blog-list-category")
	public String getBlogPageByCategory(@RequestParam int id, @RequestParam int page, Model model) throws ParseException {

		List<Blog> list = blogDAO.bligListByCategory(id, page);
		getDateTimeDifference(list);
		Category category = categoryDAO.getCategory(id);

		model.addAttribute("page", page);
		model.addAttribute("blogList", list);
		model.addAttribute("category", category);
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("size", blogDAO.getCountBlogsForCategory(id));
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "front/blog-list-category";
	}

	@RequestMapping("/blog-list-category-change")
	public String changePageOnCategory(@RequestParam int id, @RequestParam int page, @RequestParam String type,
			Model model) throws ParseException {

		List<Blog> list;
		Category category = categoryDAO.getCategory(id);

		if (type.equalsIgnoreCase("left")) {

			page--;
			list = blogDAO.bligListByCategory(id, page);
			getDateTimeDifference(list);			
			model.addAttribute("blogList", list);

		} else if (type.equalsIgnoreCase("right")) {

			page++;
			list = blogDAO.bligListByCategory(id, page);
			getDateTimeDifference(list);			
			model.addAttribute("blogList", list);
		}
		
		model.addAttribute("id", id);
		model.addAttribute("page", page);
		model.addAttribute("category", category);
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("size", blogDAO.getCountBlogsForCategory(id));
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "redirect: blog-list-category";
	}

	@RequestMapping("/blog-list-tag")
	public String getBlogPageByTag(@RequestParam int id, @RequestParam int page, Model model) {

		int number = 0;

		List<List<Blog>> list = tagDAO.getTagWithBlogs(id);

		for (List<Blog> list2 : list) {
			number += list2.size();
		}

		model.addAttribute("page", page);
		model.addAttribute("size", list.size());
		model.addAttribute("tag", tagDAO.getTag(id));
		model.addAttribute("numberOfBlogs", number);
		model.addAttribute("blogList", list.get(page));
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "front/blog-list-tag";
	}

	@RequestMapping("/blog-list-tag-change")
	public String changePageOnTag(@RequestParam int id, @RequestParam int page, @RequestParam String type,
			Model model) {

		List<List<Blog>> list = tagDAO.getTagWithBlogs(id);

		if (type.equalsIgnoreCase("left")) {

			page--;
			model.addAttribute("blogList", list.get(page));

		} else if (type.equalsIgnoreCase("right")) {

			page++;
			model.addAttribute("blogList", list.get(page));

		}
		model.addAttribute("page", page);
		model.addAttribute("size", list.size());
		model.addAttribute("tag", tagDAO.getTag(id));
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "front/blog-list-tag";
	}

	@RequestMapping("/blog-list-search")
	public String getBlogPageBySearch(@RequestParam String word, @RequestParam int page, Model model) throws ParseException {

		List<Blog> list = blogDAO.getTwelveBlogsBySearch(word, page);
		getDateTimeDifference(list);

		model.addAttribute("word", word);
		model.addAttribute("page", page);
		model.addAttribute("blogList", list);	
		model.addAttribute("size", list.size());
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("size", blogDAO.getCountBlogsForSearch(word));
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "front/blog-list-search";
	}

	@RequestMapping("/blog-list-search-change")
	public String changePageSearch(@RequestParam String word, @RequestParam int page, @RequestParam String type,
			Model model) throws ParseException {

		List<Blog> list;

		if (type.equalsIgnoreCase("left")) {

			page--;
			list = blogDAO.getTwelveBlogsBySearch(word, page);
			getDateTimeDifference(list);
			model.addAttribute("blogList", list);

		} else if (type.equalsIgnoreCase("right")) {

			page++;
			list = blogDAO.getTwelveBlogsBySearch(word, page);
			getDateTimeDifference(list);
			model.addAttribute("blogList", list);
		}
		
		model.addAttribute("page", page);
		model.addAttribute("word", word);
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("size", blogDAO.getCountBlogsForSearch(word));
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "redirect: blog-list-search";
	}

	@RequestMapping("/blog-post")
	public String getBlogPostPage(@RequestParam int id, Model model) throws ParseException {

		Blog blog = blogDAO.getBlog(id);

		getDateTimeDifferenceForOneBlog(blog);
		blog.setReview(blog.getReview() + 1);
		blogDAO.saveBlog(blog);

		model.addAttribute("blog", blog);
		model.addAttribute("comment", new Comment());
		model.addAttribute("prevAndNext", blogDAO.getPrevAndNext(id));
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "front/blog-post";
	}

	@RequestMapping("/blog-post-change")
	public String getBlogPostPageLeft(@RequestParam int id, @RequestParam String type, Model model) throws ParseException {

		Blog blog;
		
		if (type.equalsIgnoreCase("left")) {			
			blog = blogDAO.getBlogLeft(id);
			getDateTimeDifferenceForOneBlog(blog);
			model.addAttribute("blog", blog);
		} else if (type.equalsIgnoreCase("right")) {
			blog = blogDAO.getBlogRight(id);
			getDateTimeDifferenceForOneBlog(blog);
			model.addAttribute("blog", blog);
		}
		
		model.addAttribute("id", id);
		model.addAttribute("comment", new Comment());
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());

		return "front/blog-post";

	}

	@RequestMapping("/blog-post-save-comment")
	public String saveComment(@ModelAttribute Comment comment, @RequestParam(name = "pageId") int id, Model model) throws ParseException {

		Blog blog = blogDAO.getBlog(id);
		getDateTimeDifferenceForOneBlog(blog);
		comment.setDate();
		comment.setIdBlog(blog);
		blogDAO.saveBlog(blog);
		commentDAO.saveComment(comment);
		
		model.addAttribute("id", id);
		model.addAttribute("blog", blog);
		model.addAttribute("comment", new Comment());
		model.addAttribute("tagList", tagDAO.getTagsForFilter());
		model.addAttribute("mostReviewList", blogDAO.getMostReviewBlogs());
		model.addAttribute("threeBlogList", blogDAO.getThreeImportantBlogs());
		model.addAttribute("categoryList", categoryDAO.getCategoryForFilter());		
		
		return "redirect: blog-post";
	}
	
	public static void getDateTimeDifference(List<Blog> blogList) throws ParseException {
				
		for (Blog blog : blogList) {
						
			Date dateNow = new Date();		 
			SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
			String now = sdf.format(dateNow);
			
			Date dateObj1 = sdf.parse(blog.getDate());
			Date dateObj2 = sdf.parse(now);

			long diff = dateObj2.getTime() - dateObj1.getTime();
			int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			
			blog.setDateDifference(diffDays);
		}		
	}
	public static void getDateTimeDifferenceForOneBlog(Blog blog) throws ParseException {
						
			Date dateNow = new Date();		 
			SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
			String now = sdf.format(dateNow);
			
			Date dateObj1 = sdf.parse(blog.getDate());
			Date dateObj2 = sdf.parse(now);

			long diff = dateObj2.getTime() - dateObj1.getTime();
			int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			
			blog.setDateDifference(diffDays);
				
	}
	
	private static String encodeFileToBase64Binary(String path) throws IOException{
		
//			String base64Encoded = "";
//		  File file = new File(path);
//		  try (FileInputStream imageInFile = new FileInputStream(file)) {
		    // Reading a Image file from file system
		    //byte[] imageData = new byte[(int) file.length()];
		    //imageInFile.read(imageData);
		    //imageInFile.read(imageData);
//		    imageInFile.readAllBytes();
//		    System.out.println("WWWWWWW"   +  imageInFile.readAllBytes());
//		    base64Image = java.util.Base64.getEncoder().encodeToString(imageData);
		    
//		    byte[] encodeBase64 = org.apache.tomcat.util.codec.binary.Base64.encodeBase64(file.);
//			base64Encoded = new String(encodeBase64, "UTF-8");
//		    
//		  } catch (FileNotFoundException e) {
//		    System.out.println("Image not found" + e);
//		  } catch (IOException ioe) {
//		    System.out.println("Exception while reading the Image " + ioe);
//		  }
//		  return base64Encoded;
		
		 File file = new File(path);
		 FileInputStream imageInFile = new FileInputStream(file);
//		byte[] imageBytes = IOUtils.toByteArray(imageInFile);
//		String base64 = java.util.Base64.getEncoder().encodeToString(imageBytes);
		
		byte[] encodeBase64 = org.apache.tomcat.util.codec.binary.Base64.encodeBase64(imageInFile.readAllBytes());
		String base64Encoded = new String(encodeBase64, "UTF-8");
		
		return base64Encoded;
    }
	
}
