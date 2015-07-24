package com.nightwind.omo.manager;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.nightwind.omo.exception.SMSIncorrectException;
import com.nightwind.omo.exception.UserDeliveryNotFound;
import com.nightwind.omo.model.Delivery;
import com.nightwind.omo.utils.HibernateUtils;
import com.nightwind.omo.utils.SmsVerifyKit;

public class DeliveryManager {

	@SuppressWarnings("unchecked")
	public static List<Delivery> getDeliveries(String username) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		String hql = "from Delivery ";
		Query query;
		// 如果username !＝ null，则返回用户的delivery，否则返回所有
		if (username != null) {
			hql += " where username = :username ";
			query = session.createQuery(hql);
			query.setParameter("username", username);
		} else {
			query = session.createQuery(hql);
		}
		session.close();
		return query.list();
	}

	public static Delivery getDelivery(String id) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		String hql = "from Delivery where id = :id ";
		Query query = session.createQuery(hql);
		query.setParameter("id", Integer.valueOf(id));
		session.close();
		return (Delivery) query.list().get(0);
	}
	
	public static Delivery getUserDelivery(String username) throws UserDeliveryNotFound {
		Delivery delivery = null;
		Session session = null;
		try {
			session = HibernateUtils.getSessionFactory().openSession();
			String hql = "from Delivery where username = :username ";
			Query query = session.createQuery(hql);
			query.setParameter("username", username);
			List<Delivery> list = query.list();
			if (list.size() > 0) {
				delivery = (Delivery) list.get(0);
			} else {
				throw new UserDeliveryNotFound();
			}
		} finally {
			session.close();
		}
		return delivery;
	}

	public static Delivery putDelivery(Delivery delivery, String smscode) throws SMSIncorrectException {
		// verify SMS code is correct
		try {
			if (verifySmscode(delivery.getTel(), smscode)) {
				delivery.setTelVerifiTime(new Date());
			} else {
				throw new SMSIncorrectException();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SMSIncorrectException();
		}
		// put delivery
		Session session = HibernateUtils.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.save(delivery);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
		return delivery;
	}
	
	public static boolean verifySmscode(String tel, String smscode) throws Exception {
		return new SmsVerifyKit(tel, smscode).verify();
//		return true;
	}

	public static Delivery update(Delivery delivery, String smscode, boolean checkSmsCode) throws SMSIncorrectException {
		if (checkSmsCode) {
			try {
				if (verifySmscode(delivery.getTel(), smscode)) {
					delivery.setTelVerifiTime(new Date());
				} else {
					throw new SMSIncorrectException();
				}
			} catch (SMSIncorrectException e) {
				throw new SMSIncorrectException();
			} catch (Exception e) {
				e.printStackTrace();
				throw new SMSIncorrectException();
			}
		}
		// update delivery
		Session session = null;
		try {
			session = HibernateUtils.getSessionFactory().openSession();
			session.beginTransaction();
			session.update(delivery);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
		return delivery;
	}

	public static void delete(Delivery delivery) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.delete(delivery);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}
	
	public static boolean verifyOwner(String deliveryId, String username) {
		return getDelivery(deliveryId).getUsername().equals(username);
	}
	

	public static void updateOrInstertDelivery(String username, Delivery delivery, String smscode) throws SMSIncorrectException {
		if (delivery == null) {
			return;
		}
		Delivery dbDelivery;
		try {
			dbDelivery = getUserDelivery(username);
			String tel = delivery.getTel();
			if (tel != null) {
				dbDelivery.setTel(tel);
			}
			String address = delivery.getAddress();
			if (address != null) {
				dbDelivery.setAddress(address);
			}
			//不修改号码不需要短信验证
			boolean checkSmsCode = true;
			if (dbDelivery.getTel().equals(delivery.getTel())) {
				checkSmsCode = false;
			}
			update(dbDelivery, smscode, checkSmsCode);
		} catch (UserDeliveryNotFound e) {
			putDelivery(delivery, smscode);
		}
	}
}
