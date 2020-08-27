package blog.main.dao;

import java.util.List;

import blog.main.entity.Contact;

public interface ContactDAO {

	
	public List<Contact> getContactList();
	
	public Contact getContactById(int id);
	
	public void saveContact(Contact contact);
	
	public void deleteContact(int id);
	
}
