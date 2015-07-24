package com.nightwind.omo.manager;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.nightwind.omo.exception.DishNoFoundException;
import com.nightwind.omo.exception.DishStatusPurchaseUnavailable;
import com.nightwind.omo.exception.InsufficientAccountBalanceException;
import com.nightwind.omo.exception.OrderStatusChangeException;
import com.nightwind.omo.exception.OrderUnrecognizedException;
import com.nightwind.omo.exception.RequestLengthException;
import com.nightwind.omo.model.Dish;
import com.nightwind.omo.model.Order;
import com.nightwind.omo.model.User;
import com.nightwind.omo.utils.HibernateUtils;

public class OrderManager {
	
	private String username;
	
	public OrderManager() {
		
	}
	
	public OrderManager(String username) {
		this.username = username;
	}

	/**
	 * 获取用户／所有人的order
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Order> getOrders() {
		Session session = HibernateUtils.getSessionFactory().openSession();
		String hql = "from Order ";
		Query query;
		// 如果username !＝ null，则返回用户的order，否则返回所有
		if (username != null) {
			hql += " where username = :username order by time desc";
			query = session.createQuery(hql);
			query.setParameter("username", username);
		} else {
			query = session.createQuery(hql);
		}
		return query.list();
	}
	
	public Order getOrder(String orderId) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		return (Order) session.get(Order.class, Integer.valueOf(orderId));
	}

	public Order updateOrderStatus(String orderId, String status) throws OrderUnrecognizedException, OrderStatusChangeException {
		Order order = getOrder(orderId);
		// 目前只提供取消订单，已完成的修改
		int type = 0;
		if (status != null) {
			String oldStatus = order.getStatus();
			if (status.equals("订单取消")) {
				// 只有“待确认”的订单才能取消
				if (!oldStatus.equals("待确认")) {
					throw new OrderStatusChangeException(oldStatus);
				}
				type = 0;
			} else if (status.equals("已完成")) {
				// 只有“商家已确认”的订单才能确认
				if (!oldStatus.equals("商家已确认")) {
					throw new OrderStatusChangeException(oldStatus);
				}
				type = 1;
			} else {
				throw new OrderUnrecognizedException(status);
			}
		} else {
			throw new OrderUnrecognizedException(status);
		}
		Session session = HibernateUtils.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			Query query;
			if (type == 0) {
				query = session.createQuery("update Order set status = :status where id = :orderId and username = :username");
			} else {
				query = session.createQuery("update Order set status = :status, confirmTime = :datetime where id = :orderId and username = :username");
				query.setParameter("datetime", new Date());
			}
			query.setParameter("username", username);
			query.setParameter("orderId", Integer.valueOf(orderId));
			query.setParameter("status", status);
			query.executeUpdate();
			
			//订单取消，返回金额
			if (type == 0) {
				User user = (User) session.get(User.class, username);
				//返还资金
//				System.out.println("user old balance = " + user.getBalance() + " order cost = " + order.getCost());
				user.setBalance(user.getBalance() + order.getCost());
				session.update(user);
			} else {
				//订单完成，增加商家余额
				
				//查询商家用户名
				query = session.createQuery("select noshery.username from Order as order, Dish as dish, Noshery as noshery where order.id = :orderId AND order.dishId = dish.id AND noshery.id = dish.nosheryId");
				query.setParameter("orderId", Integer.valueOf(orderId));
				String username = (String) query.list().get(0);
				//商家资金增加
				query = session.createQuery("update User set balance = balance + :money where username = :username");
				query.setParameter("username", username);
				query.setParameter("money", order.getCost());
				query.executeUpdate();
			}
			
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
		return order;
		
	}
	
//	public static void main(String[] args) {
//		String orderId = "1";
//		Session session = HibernateUtils.getSessionFactory().openSession();
//		session.beginTransaction();
//		Query query = session.createQuery("select noshery.username from Order as order, Dish as dish, Noshery as noshery where order.id = :orderId AND order.dishId = dish.id AND noshery.id = dish.nosheryId");
////		Query query = session.createQuery("select user.balance from Order as order, Dish as dish, Noshery as noshery, User as user where order.id = :orderId AND order.dishId = dish.id AND noshery.id = dish.nosheryId AND user.username = noshery.username");
////		Query query = session.createQuery("update User set user.balance = user.balance + :money from Order as order, Dish as dish, Noshery as noshery, User as user where order.id = :orderId AND order.dishId = dish.id AND noshery.id = dish.nosheryId AND user.username = noshery.username");
//		query.setParameter("orderId", Integer.valueOf(orderId));
//		List list = query.list();
//		String result = (String) (list.get(0));
////		double result = (double) list.get(0);
//		System.out.println("result = " + result);
//		query = session.createQuery("update User set balance = balance + :money where username = :username");
//		query.setParameter("username", result);
//		query.setParameter("money", 10.0);
//		query.executeUpdate();
//		session.getTransaction().commit();
//	}
	
	public Order putOrder(Order order) throws InsufficientAccountBalanceException, DishNoFoundException, RequestLengthException, DishStatusPurchaseUnavailable {
		//预处理
		Dish dish = DishManager.getDish(String.valueOf(order.getDishId()));
		if (dish == null) {
			throw new DishNoFoundException();
		}
		if (order.getDishCount() <= 0) {
			throw new RequestLengthException("菜品数量不能小于1");
		}
		if (!dish.getStatus().equals("正常")) {
			String msg = "不可购买。该商品的状态为：" + dish.getStatus();
			throw new DishStatusPurchaseUnavailable(msg);
		}
		order.setDishCost(dish.getCost());
		order.setCost(order.getDishCost() * order.getDishCount());
		order.setTime(new Date());
		order.setStatus("待确认");
		
		Session session = HibernateUtils.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			User user = (User) session.get(User.class, username);
			if (user.getBalance() - order.getCost() >= 0) {
				user.setBalance(user.getBalance() - order.getCost());
				session.update(user);
				session.save(order);
			} else {
				throw new InsufficientAccountBalanceException();
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
		return order;
	}

	public boolean verifyOwner(String orderId) {
		boolean ok = false;
		try {
			ok = getOrder(orderId).getUsername().equals(username);
		} catch (Exception e) {
		}
		return ok;
	}
}
