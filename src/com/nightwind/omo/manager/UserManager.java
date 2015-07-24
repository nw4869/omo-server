package com.nightwind.omo.manager;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.hibernate.Query;
import org.hibernate.Session;

import com.nightwind.omo.exception.RequestLengthException;
import com.nightwind.omo.model.Delivery;
import com.nightwind.omo.model.User;
import com.nightwind.omo.utils.EncryptUtils;
import com.nightwind.omo.utils.HibernateUtils;

public class UserManager {

	private static Map<String, Long> authFailedTime = new HashMap<>();
	private static Map<String, Integer> authFailedTimes = new HashMap<>();
	private static int banMinute = 60;
	private static int maxFailedTimes = 10;

	

	public static void putUser(User u) {
		u.setIdentity("用户");
		u.setPassword(EncryptUtils.str2MD5(u.getPassword()));
		u.setBalance(new Double(0));
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(u);
		session.getTransaction().commit();
	}

	public static User getUser(String username) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		return (User) session.get(User.class, username);
	}
	

	public static void updatePassword(String username, String plainPwd) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		try {
	        session.getTransaction().begin();
			Query query = session.createQuery("update User set password = :plainPwd where username = :username");
			query.setParameter("username", username);
			query.setParameter("plainPwd", plainPwd);
			query.executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}
	
	public static boolean checkPassword(String username, String plainPwd) throws RequestLengthException {
		if (username == null || username.trim().length() == 0 || username.length() > 20 || plainPwd == null || plainPwd.trim().length() == 0 || plainPwd.length() > 20) {
			throw new RequestLengthException("用户名或密码长度错误");
		}
		Session session = HibernateUtils.getSessionFactory().openSession();
//		Query query = session.createQuery("from User where username = :username and password = MD5(CONCAT(salt, :plainPwd))");
		Query query = session.createQuery("from User where username = :username and password = MD5(:plainPwd)");
//		Query query = session.createQuery("from User where username = :username and password = :plainPwd");
		query.setParameter("username", username);
		query.setParameter("plainPwd", plainPwd);
		int size = query.list().size();
		session.close();
		return size > 0;
	}
	
	/**
	 *  只更新姓名
	 * @param user
	 */
	public static void update(User user) {
		if (user.getName() == null) {
			return;
		}
		Session session = HibernateUtils.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			Query query = session.createQuery("update User set name = :name where username = :username");
			query.setParameter("name", user.getName());
			query.setParameter("username", user.getUsername());
			query.executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}
	
	public static String getToken(String username) {
		return getUser(username).getPassword();
	}
	
	public static void main(String[] args) {
		User user = new User();
		user.setUsername("user2");
		user.setPassword("pwd");
		putUser(user);
		getUser("user1");
		updatePassword("user1", "pwd");
		
//		boolean ok = checkPassword(user.getUsername(), user.getPassword());
//		System.out.println("ok = " + ok);
		
		System.out.println("===end===");
	}

	public static boolean checkToken(String username, String token) throws RequestLengthException {
		if (username == null || username.trim().length() == 0 || username.length() > 20 || token == null || token.trim().length() == 0 || token.length() > 32) {
			throw new RequestLengthException("用户名或token长度错误");
		}
		Session session = HibernateUtils.getSessionFactory().openSession();
		Query query = session.createQuery("from User where username = :username and password = :token");
		query.setParameter("username", username);
		query.setParameter("token", token);
		int size = query.list().size();
		session.close();
		return size > 0;
	}


	public static void checkBan(String ip) {
		long now = System.currentTimeMillis();
		Integer times = authFailedTimes.get(ip);
		if (times != null && times >= maxFailedTimes && now < authFailedTime.get(ip) + banMinute * 60 * 1000) {
			Response res = Response.status(403).entity("{\"error\": \"ban\", \"endTime\": \"" + authFailedTime.get(ip) + banMinute * 60 + "\"}").build();
			throw new WebApplicationException(res);
		}
	}
	
	public static void ban(String ip) {
		long now = System.currentTimeMillis();
		Integer times = authFailedTimes.get(ip);
		authFailedTime.put(ip, now);
		if (times == null) {
			times = 0;
		} 
		authFailedTimes.put(ip, ++times);
	}
	
}
