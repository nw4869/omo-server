package com.nightwind.omo.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.nightwind.omo.manager.DishManager;
import com.nightwind.omo.model.Dish;

@Path("/dishes")
public class DishesResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Dish> getDishes() {
		List<Dish> dishses = DishManager.getDishses();
		return dishses;
	}
	

	@GET
	@Path("count")
	@Produces(MediaType.TEXT_HTML)
	public String getCount() {
		int count = DishManager.getDishses().size();
		return String.valueOf(count);
	}
}
