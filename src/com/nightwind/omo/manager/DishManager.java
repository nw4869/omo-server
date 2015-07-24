package com.nightwind.omo.manager;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.nightwind.omo.model.Dish;
import com.nightwind.omo.utils.HibernateUtils;

public class DishManager {

	@SuppressWarnings("unchecked")
	public static List<Dish> getDishses() {
		Session session = HibernateUtils.getSessionFactory().openSession();
		Query query = session.createQuery("from Dish where status = '正常' OR status = '缺货'");
		List<Dish> dishses = query.list();
		for (Dish dish: dishses) {
			dish.setPicUrl(fixImageUrl(dish.getPicUrl()));
		}
		return dishses;
	}
	
	public static Dish getDish(String dishId) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		Dish dish = (Dish) session.get(Dish.class, Integer.valueOf(dishId));
		dish.setPicUrl(fixImageUrl(dish.getPicUrl()));
		return dish;
	}

	private static String fixImageUrl(String url) {
		String path = url;
		if (path != null && !path.startsWith("http")) {
			if (path.startsWith("./")) {
				path = path.substring(2);
			}
			return ("http://omo.nw4869.cc/" + path);
		} else {
			return url;
		}
	}
}
