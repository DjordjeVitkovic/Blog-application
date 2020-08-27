package blog.main.dao;


import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import blog.main.entity.Slide;
@Repository
public class SlideDAOImpl implements SlideDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	@Override
	public void saveSlide(Slide slide) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(slide);
		
	}
	@Transactional
	@Override
	public void deleteSlide(int id) {
		
		Session session = sessionFactory.getCurrentSession();
		
		Slide s = session.get(Slide.class, id);
		
		session.delete(s);
	}
	@Transactional
	@Override
	public List<Slide> slideList() {
		
		Session session = sessionFactory.getCurrentSession();
		Query<Slide> query = session.createQuery("from Slide");
			
		return query.getResultList();
	}
	@Transactional
	@Override
	public Slide getSlideById(int id) {
		
		Session session = sessionFactory.getCurrentSession();
		
		Slide s = session.get(Slide.class, id);

		return s;
	}
	@Transactional
	@Override
	public List<Slide> getSlideListForMainPage() {
		
		Session session = sessionFactory.getCurrentSession();
		Query<Slide> query = session.createQuery("select s from Slide s where s.isVisible=1 order by position asc");
			
		return query.getResultList();
		
	}

}
