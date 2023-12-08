package ca.sheridancollege.darosaeric.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.darosaeric.beans.Book;
import ca.sheridancollege.darosaeric.beans.Order;
import ca.sheridancollege.darosaeric.database.DatabaseAccess;
import jakarta.servlet.http.HttpSession;

@Controller
public class BookstoreController {

	@Autowired
	@Lazy
	private DatabaseAccess da;

	List<Book> bookList = new CopyOnWriteArrayList<>();
	List<Book> cartList = new ArrayList<>();
	double totalPrice = 0;
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/login")
	public String login(HttpSession session) {
		session.invalidate(); // Creates new session on login
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@PostMapping("/register")
	public String postRegister(@RequestParam String username, @RequestParam String password, Model model) {
		
		if (da.findUserAccount(username) == null) { // If account does not exist, add it
			da.addUser(username, password);
			
			Long userId = da.findUserAccount(username).getUserId();
			da.addRole(userId, Long.valueOf(1));
			
			return "redirect:/";
		} 
		else { // If account exists, don't create it and display an error message
			model.addAttribute("error", "Account already Exists");
			return "register";
		}
		
	}
	
	@GetMapping("/error")
	public String error() {
		return "permission-denied";
	}

	@GetMapping("/secure")
	public String secure(Model model, HttpSession session) {

		if (session.isNew()) {
			cartList = new ArrayList<>();
		}
		else {
			session.setAttribute("cartList", cartList);
		}
		
		String formattedPrice = String.format("%.2f", totalPrice);

		model.addAttribute("bookList", da.getBookList());
		model.addAttribute("totalPrice", "$" + formattedPrice);
		model.addAttribute("book", new Book());
		
		return "secure/index";
	}

	@PostMapping("/secure/addBook")
	public String addBook(Model model, @ModelAttribute Book book) {

		List<Book> existingBooks = da.getBookListById(book.getId());

		if (existingBooks.isEmpty()) {
			da.addBook(book);
		} else {
			da.editBookById(book);
		}

		String formattedPrice = String.format("%.2f", totalPrice);
		model.addAttribute("totalPrice", "$" + formattedPrice);

		model.addAttribute("bookList", da.getBookList());
		model.addAttribute("book", new Book());

		return "/secure/index";
	}

	@GetMapping("/secure/deleteBookById/{id}")
	public String deleteBookById(Model model, @PathVariable Long id) {

		Book book = da.getBookListById(id).get(0);

		while (cartList.contains(book)) { // If book is in cart, remove and update accordingly
			cartList.remove(book);
			totalPrice -= book.getPrice();
			String formattedPrice = String.format("%.2f", totalPrice);
			model.addAttribute("totalPrice", "$" + formattedPrice);
		}

		da.deleteBookById(id);
		model.addAttribute("bookList", da.getBookList());
		model.addAttribute("book", new Book());

		return "/secure/index";
	}

	@GetMapping("/secure/editBookById/{id}")
	public String editBookById(Model model, @PathVariable Long id) {

		Book book = da.getBookListById(id).get(0);
		da.editBookById(book);

		while (cartList.contains(book)) { // If book is edited while in cart remove it
			cartList.remove(book);
			totalPrice -= book.getPrice();
		}
		
		String formattedPrice = String.format("%.2f", totalPrice);
		model.addAttribute("totalPrice", "$" + formattedPrice);

		model.addAttribute("bookList", da.getBookList());
		model.addAttribute("book", book);

		return "/secure/index";
	}

	@GetMapping("/secure/bookDetails/{id}")
	public String bookDetails(Model model, @PathVariable Long id) {

		Book book = da.getBookListById(id).get(0);

		model.addAttribute("book", book);

		return "/secure/bookDetails";
	}

	@GetMapping("/secure/addToCart/{id}")
	public String addToCart(Model model, @PathVariable Long id, HttpSession session) {

		Book book = da.getBookListById(id).get(0);
		cartList.add(book);

		totalPrice += book.getPrice(); // Add up total of each book in cart
		String formattedPrice = String.format("%.2f", totalPrice); // Format price to 2 decimals

		session.setAttribute("cartList", cartList);
		model.addAttribute("bookList", da.getBookList());
		model.addAttribute("totalPrice", "$" + formattedPrice); // Updates total price in cart
		model.addAttribute("book", new Book());

		return "/secure/index";
	}

	@GetMapping("/secure/removeFromCart/{id}")
	public String removeFromCart(Model model, @PathVariable Long id, HttpSession session) {

		Book book = da.getBookListById(id).get(0);
		cartList.remove(book);

		totalPrice -= book.getPrice(); // Subtract book cost from total cost
		String formattedPrice = String.format("%.2f", totalPrice);

		session.setAttribute("cartList", cartList);
		model.addAttribute("bookList", da.getBookList());
		model.addAttribute("totalPrice", "$" + formattedPrice); // Updates total price in cart
		model.addAttribute("book", new Book());

		return "/secure/index";
	}

	@PostMapping("/secure/checkOut")
	public String checkOut(Model model) {
		
		/// Retrieve user name of logged in user
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
		
		Order order = new Order();
		order.setUser(username);
		order.setItems(order.prepareQuery(cartList)); // Allows us to add only the name of each book to the SQL query
		order.setPrice(totalPrice);
		order.setQuantity(cartList.size());	
		
		da.processCheckout(order);
		
		model.addAttribute("user", order.getUser());
		model.addAttribute("orderDetails", order.getItems());
		
		String formattedPrice = String.format("%.2f", totalPrice);
		model.addAttribute("totalPrice", "$" + formattedPrice);
		
		cartList.clear(); // Clears cartList upon successful checkout
		totalPrice = 0; // Reset totalPrice upon successful checkout
		
		return "/secure/checkout";
	}

	@GetMapping("/secure/reTest")
	public String reTest(Model model, HttpSession session) { // Used for testing purposes
		
		session.invalidate();
		cartList = new CopyOnWriteArrayList<>();
		totalPrice = 0;
		model.addAttribute("bookList", da.getBookList());
		model.addAttribute("book", new Book());

		return "secure/index";
	}

}


