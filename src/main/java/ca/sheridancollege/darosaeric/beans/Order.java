package ca.sheridancollege.darosaeric.beans;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Order {
	
	private String user;
	private String items;
	private double price;
	private int quantity;
	
	public String prepareQuery(List<Book> cart) { // Takes current Cart as input
		
		List<String> itemNames = new ArrayList<>();

		for(Book e : cart) { 
			itemNames.add(e.getTitle()); // Adds just the book name to itemNames List
		} 
	
		return String.join(",", itemNames); // Returns a String containing all of the book names separated by a comma
	}
	
}
