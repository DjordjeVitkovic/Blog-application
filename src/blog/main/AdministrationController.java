package blog.main;

import java.awt.Button;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import blog.main.dao.BlogDAO;
import blog.main.dao.CategoryDAO;
import blog.main.dao.CommentDAO;
import blog.main.dao.ContactDAO;
import blog.main.dao.SlideDAO;
import blog.main.dao.TagDAO;
import blog.main.dao.UserDAO;
import blog.main.entity.Blog;
import blog.main.entity.Category;
import blog.main.entity.ChangePassword;
import blog.main.entity.Comment;
import blog.main.entity.Contact;
import blog.main.entity.Slide;
import blog.main.entity.Tag;
import blog.main.entity.User;

@Controller
@RequestMapping("/administration")
public class AdministrationController {

	@Autowired
	private SlideDAO slideDAO;
	@Autowired
	private ContactDAO contactDAO;
	@Autowired
	private CommentDAO commentDAO;
	@Autowired
	private CategoryDAO categoryDAO;
	@Autowired
	private TagDAO tagDAO;
	@Autowired
	private BlogDAO blogDAO;
	@Autowired
	private UserDAO userDAO;

	@RequestMapping("/admin")
	public String getAdminPage() {

		return "admin-page";

	}

	// ========================================================================
	// =========== BLOG-POST ============ BLOG-POST ===========================
	// ========================================================================

	@RequestMapping("/blog-form")
	public String getBlogForm(Model model) {

		// Saljem prazan objekat koji ce se popiniti, listu tagova i kategorija da se
		// izaberu

		model.addAttribute("blog", new Blog());
		model.addAttribute("tagList", tagDAO.getTagList());
		model.addAttribute("categoryList", categoryDAO.getCategoryList());

		return "blog-form";
	}

	@RequestMapping("/blog-save-new")
	public String saveBlog(@ModelAttribute Blog blog, @RequestParam(value = "file") MultipartFile file, Principal principal)
			throws IOException {

		// setovanje trenutnog datuma blogu
		blog.setDate();

		// cuvanje slike na desktopu, dao sam path gde da ga cuva i ime fajla, preko
		// metode write upisujem
		byte[] bytes = file.getBytes();
		Path path = Paths.get("/Users/djordjevitkovic/eclipse-workspace/BlogWebApp/WebContent/resources/images/"
				+ file.getOriginalFilename());
		Files.write(path, bytes);
		///Users/djordjevitkovic/Desktop/images/
		///Users/djordjevitkovic/eclipse-workspace/BlogWebApp/WebContent/resources/images
		
		// setujem blogu ime slike
		blog.setImage(file.getOriginalFilename());

		// cekiram da li blog pripada nekoj kategoriji, ako ne onda je ucategory
		if (blog.getCategory().getId() != 0) {
			Category category = categoryDAO.getCategory(blog.getCategory().getId());
			blog.setCategory(category);
		} else {
			blog.setCategory(null);

		}
	
	
		
		User user = userDAO.getUserByUsername(principal.getName());
		blog.setUser(user);		

		// primam listu id-jeva od tagova i parsiram ih u integer
		List<Integer> ids = new ArrayList<Integer>();

		for (Tag tag : blog.getTags()) {
			ids.add(Integer.parseInt(tag.getName()));
		}
		List<Tag> tags = tagDAO.getTagListByIds(ids);

		// setujem tagove
		blog.setTags(tags);

		// cuvan blog
		blogDAO.saveBlog(blog);

		return "redirect:/administration/blog-list";
	}

	@RequestMapping("/blog-list")
	public String getBlogList(Principal principal,Model model) throws IOException {

		// lista svih blogova
		List<Blog> list = blogDAO.getBlogList();
		model.addAttribute("blogList", list);
		model.addAttribute("user", userDAO.getUserByUsername(principal.getName()));

		return "blog-list";
	}

	// Metoda za vracanje liste komentara po blogu, gde ce se moci izlistati i
	// brisati
	@RequestMapping("/blog-comments-list")
	public String getCommentsForBlog(@RequestParam int id, Model model) {

		model.addAttribute("commentList", commentDAO.getCommentListByBlog(blogDAO.getBlog(id)));

		return "comment-list";
	}

	@RequestMapping("/blog-form-update")
	public String updateBlog(@RequestParam int id, Model model) {

		// saljem blog koji zelim da update
		model.addAttribute("blog", blogDAO.getBlog(id));
		model.addAttribute("tagList", tagDAO.getTagList());
		model.addAttribute("categoryList", categoryDAO.getCategoryList());

		return "blog-form";
	}

	@RequestMapping("blog-delete")
	public String deleteBlog(@RequestParam int id) {

		blogDAO.deleteBlog(id);
		return "redirect: blog-list";

	}
	
	@RequestMapping("/blog-important-change")
	public String changeImportant(@RequestParam int id) {
		
		Blog blog = blogDAO.getBlog(id);
		
		blog.setImportant(!blog.getImportant());
		
		blogDAO.saveBlog(blog);
		
		return "redirect: blog-list";
		
	}
	
	@RequestMapping("/blog-list-sort")
	public String getBlogListOrded(@RequestParam int order,Principal principal, Model model) {
		
		model.addAttribute("blogList", blogDAO.getBlogListOrder(order));
		model.addAttribute("user", userDAO.getUserByUsername(principal.getName()));
		
		return "blog-list";
	}
	
	@RequestMapping("/blog-list-search")
	public String searchBlog(@RequestParam String word,Principal principal, Model model) {
		
		model.addAttribute("blogList", blogDAO.getBlogListSearchBack(word));
		model.addAttribute("user", userDAO.getUserByUsername(principal.getName()));

		
		return "blog-list";
	}
//========================================================================	
//===========     SLIDE     ============   SLIDE    ======================	
//========================================================================	

	@RequestMapping("/slide-form")
	public String getSlideForm(Model model) {

		model.addAttribute("slide", new Slide());

		return "slide-form";
	}

	@RequestMapping("/slide-save")
	public String saveSlide(@ModelAttribute Slide slide) {

		slideDAO.saveSlide(slide);

		return "redirect: slide-list";
	}

	@RequestMapping("/slide-list")
	public String getSlideList(Model model) {

		model.addAttribute("slideList", slideDAO.slideList());

		return "slide-list";
	}

	@RequestMapping("/slide-delete")
	public String deleteSlide(@RequestParam int id) {

		slideDAO.deleteSlide(id);

		return "redirect: slide-list";
	}

	@RequestMapping("/slide-form-update")
	public String updateSlide(@RequestParam int id, Model model) {

		slideDAO.getSlideById(id);

		model.addAttribute("slide", slideDAO.getSlideById(id));

		return "slide-form";
	}

	// menjanje iz visible u unvisible i obrnuto
	@RequestMapping("/slide-visible")
	public String changeVisible(@RequestParam int id) {

		Slide slide = slideDAO.getSlideById(id);

		slide.setIsVisible(!slide.getIsVisible());

		slideDAO.saveSlide(slide);

		return "redirect: slide-list";
	}
	
	@RequestMapping("/slide-add")
	public String addSlide() {
		
		return "slide-add";
	}
	@RequestMapping("/slide-add-blog")
	public String addBlogInSlide(Model model) {
		
		model.addAttribute("blogList", blogDAO.getBlogList());
		
		return "slide-form-blog";
	}
	@RequestMapping("/slide-save-blog")
	public String addBlogInSlide(@RequestParam int id, Model model) {
		
		Blog blog = blogDAO.getBlog(id);
		Slide slide = new Slide();
		
		slide.setTitle(blog.getTitle());
		slide.setImage(blog.getImage());
		slide.setLinkName(blog.getTitle());
		slide.setLinkPath("/blog-post?id=" +blog.getId());
		
		
		slideDAO.saveSlide(slide);
		
		
		return "redirect: slide-list";
	}
	
	
	
	// ========================================================================
	// =========== CONTACT ============ CONTACT ===============================
	// ========================================================================

	@RequestMapping("/contact-list")
	public String getContactList(Model model) {

		model.addAttribute("contactList", contactDAO.getContactList());

		return "contact-list";
	}

	@RequestMapping("/contact-isSeen")
	public String contactIsSeen(@RequestParam int id) {

		Contact contact = contactDAO.getContactById(id);

		contact.setIsSeen(true);

		contactDAO.saveContact(contact);

		return "redirect: contact-list";
	}

	@RequestMapping("/contact-delete")
	public String deleteContact(@RequestParam int id) {

		contactDAO.deleteContact(id);

		return "redirect: contact-list";
	}

	// ========================================================================
	// =========== COMMENT ============ COMMENT ===============================
	// ========================================================================

	@RequestMapping("/comment-list")
	public String getCommentList(Model model) {

		model.addAttribute("commentList", commentDAO.getCommentList());

		return "comment-list";
	}

	// izmena da li je vidljiv komentar ili nije
	@RequestMapping("/comment-showOnPage")
	public String changeShowOnPage(@RequestParam int id) {

		Comment comment = commentDAO.getCommentById(id);

		comment.setShowOnPage(!comment.getShowOnPage());

		commentDAO.saveComment(comment);

		return "redirect: comment-list";
	}

	@RequestMapping("/comment-delete")
	public String deleteComment(@RequestParam int id) {

		commentDAO.deleteComment(id);

		return "redirect: comment-list";
	}

	// ========================================================================
	// =========== CATEGORY ============ CATEGORY =============================
	// ========================================================================

	@RequestMapping("/category-list")
	public String getCategoryList(Model model) {

		System.out.println("category list method");

		List<Category> list = categoryDAO.getCategoryList();

		System.out.println(list.toString());

		model.addAttribute("categoryList", list);

		return "category-list";
	}

	@RequestMapping("/category-form")
	public String getCategoryForm(Model model) {

		Category category = new Category();

		model.addAttribute("category", category);

		return "category-form";
	}

	@RequestMapping("/category-form-update")
	public String getCategoryUpdateForm(@RequestParam int id, Model model) {

		Category category = categoryDAO.getCategory(id);

		model.addAttribute("category", category);

		return "category-form";
	}

	@RequestMapping("/category-save")
	public String saveCategory(@ModelAttribute Category category) {

		categoryDAO.saveCategory(category);

		return "redirect:/administration/category-list";
	}

	@RequestMapping("/category-delete")
	public String deleteCategory(@RequestParam int id) {

		categoryDAO.deleteCategory(id);

		return "redirect:/administration/category-list";

	}
	@RequestMapping("/go-front")
	public String goFront(Model model) throws ParseException {
		
		List<Blog> blogList = blogDAO.getThreeImportantBlogs();

		getDateTimeDifference(blogList);	

		// saljem tri liste na index stranu
		model.addAttribute("slideList", slideDAO.getSlideListForMainPage());
		// tri najnovija oznacena sa important
		model.addAttribute("blogList", blogList);
		// 12 za donji slajder
		model.addAttribute("twelveList", blogDAO.getTwelveBlogsForIndexPage());
		
		model.addAttribute("categoryList", categoryDAO.getCategoryList());
		
		return "front/index-page";
	}

	// ========================================================================
	// ============== TAGS ================= TAGS =============================
	// ========================================================================

	@RequestMapping("/tag-list")
	public String getTagList(Model model) {

		List<Tag> tagList = tagDAO.getTagList();

		model.addAttribute("tagList", tagList);

		return "tag-list";
	}

	@RequestMapping("/tag-save")
	public String saveTag(@ModelAttribute Tag tag) {

		tagDAO.saveTag(tag);

		return "redirect:/administration/tag-list";
	}

	@RequestMapping("/tag-form")
	public String getTagForm(Model model) {

		Tag tag = new Tag();
		model.addAttribute("tag", tag);
		return "tag-form";
	}

	@RequestMapping("/tag-form-update")
	public String getTagFormUpdate(@RequestParam int id, Model model) {

		Tag tag = tagDAO.getTag(id);

		model.addAttribute("tag", tag);

		return "tag-form";
	}

	@RequestMapping("tag-delete")
	public String deleteTag(@RequestParam int id) {

		tagDAO.deleteTag(id);

		return "redirect:/administration/tag-list";
	}

	// ========================================================================
	// ============== USERS ================= USERS ===========================
	// ========================================================================

	@RequestMapping("/user-list")
	public String getUserList(Principal principal,Model model) {

		// saljem listu svih usera
		model.addAttribute("userList", userDAO.getUserList());
		// uzimam jednog usera pod username i slajem ga na stranicu zbog njegovih
		// podataka
		model.addAttribute("user", userDAO.getUserByUsername(principal.getName()));

		return "user-list";
	}

	@RequestMapping("/user-enable")
	public String enableUser(@RequestParam String username) {

		// izmena da li je user enable ili disable
		userDAO.enableUser(username);

		return "redirect: user-list";
	}

	// pravljenje noih usera
	@RequestMapping("/user-form")
	public String getUserForm(Model model) {

		model.addAttribute("user", new User());
		model.addAttribute("roles", userDAO.getRols());

		return "user-form";
	}

	// cuvanje usera, bcryotujem mu passwrod
	@RequestMapping("/user-save")
	public String saveUser(@ModelAttribute User user, Model model) {

		String passwordEncode = new BCryptPasswordEncoder().encode(user.getPassword());

		user.setEnabled(true);
		user.setPassword("{bcrypt}" + passwordEncode);

		userDAO.saveUser(user);

		return "redirect: user-list";
	}

	@RequestMapping("/user-delete")
	public String saveUser(String username) {

		userDAO.deleteUser(username);

		return "redirect: user-list";
	}

	@RequestMapping("/user-edit-profile")
	public String getUserEditProfile(Principal principal, Model model) {

		model.addAttribute("user", userDAO.getUserByUsername(principal.getName()));

		return "user-edit-profile";
	}

	@RequestMapping("/user-edit")
	public String getUserEdit(@ModelAttribute User user) {

		userDAO.saveUser(user);

		return "redirect: user-list";
	}

	@RequestMapping("/user-change-password")
	public String getUserChangePassword(Principal principal, Model model) {

		// model.addAttribute("user", userDAO.getUserByUsername(principal.getName()));

		model.addAttribute("changePassword", new ChangePassword());

		return "user-change-password";
	}

	@RequestMapping("/user-change-password-action")
	public String getUserChangePasswordAction(@ModelAttribute ChangePassword password, Principal principal,
			Model model) {

		if (password.getNewPassword().equalsIgnoreCase(password.getConfirmPassword())) {

			User user = userDAO.getUserByUsername(principal.getName());

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			if (encoder.matches(password.getOdlPassword(), user.getPassword().replace("{bcrypt}", ""))) {

				user.setPassword("{bcrypt}" + encoder.encode(password.getNewPassword()));

				userDAO.saveUser(user);

			} else {

				// nije dobar stari password
				return "user-change-password";
			}
		} else {
			// ne poklapaju se novi i potvrdjeni
			return "user-change-password";
		}

		return "redirect: user-list";
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

}
