package ca.sheridancollege.darosaeric.database;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import ca.sheridancollege.darosaeric.beans.Book;
import ca.sheridancollege.darosaeric.beans.Order;
import ca.sheridancollege.darosaeric.beans.User;

@Repository
public class DatabaseAccess {

	@Autowired
	protected NamedParameterJdbcTemplate jdbc;

	@Autowired
	@Lazy
	private BCryptPasswordEncoder passwordEncoder;

	public List<Book> getBookList() { // Returns bookList
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM book";
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Book>(Book.class));
	}

	public List<Book> getBookListById(Long id) { // Returns bookList by ID
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM book WHERE id = :id";
		namedParameters.addValue("id", id);
		return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Book>(Book.class));
	}

	//Method to add books to the database
	public void addBook(Book book) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "INSERT INTO book (title, author, isbn, price, description) VALUES (:title, :author, :isbn, :price, :description)";

		namedParameters.addValue("title", book.getTitle());
		namedParameters.addValue("author", book.getAuthor());
		namedParameters.addValue("isbn", book.getIsbn());
		namedParameters.addValue("price", book.getPrice());
		namedParameters.addValue("description", book.getDescription());

		int rowsAffected = jdbc.update(query, namedParameters);

		if (rowsAffected > 0) {
			System.out.println("Added to Book Database");
		}
	}
	
	//Method to delete books from the database based on ID
	public void deleteBookById(Long id) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "DELETE FROM book WHERE id = :id";

		namedParameters.addValue("id", id);

		if (jdbc.update(query, namedParameters) > 0) {
			System.out.println("Deleted Book " + id + " from the database");
		}
	}

	//Method to edit a books details based on ID
	public void editBookById(Book book) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "UPDATE book SET title = :title, author = :author, isbn = :isbn, price = :price, description = :description WHERE id = :id";

		namedParameters.addValue("title", book.getTitle());
		namedParameters.addValue("author", book.getAuthor());
		namedParameters.addValue("isbn", book.getIsbn());
		namedParameters.addValue("price", book.getPrice());
		namedParameters.addValue("description", book.getDescription());
		namedParameters.addValue("id", book.getId());

		int rowsAffected = jdbc.update(query, namedParameters);

		if (rowsAffected > 0) {
			System.out.println("Book " + book.getId() + " Updated");
		}
	}

	//Method to process checkout and add the order to the database
	public void processCheckout(Order order) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "INSERT INTO orders (username, items, price, quantity) VALUES (:username, :items, :price, :quantity)";

		namedParameters.addValue("username", order.getUser());
		namedParameters.addValue("items", order.getItems());
		namedParameters.addValue("price", order.getPrice());
		namedParameters.addValue("quantity", order.getQuantity());

		int rowsAffected = jdbc.update(query, namedParameters);

		if (rowsAffected > 0) {
			System.out.println("Order Complete For User: " + order.getUser());
		}
	}

	// Finds User Account from Email
	public User findUserAccount(String email) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT * FROM sec_user where email = :email";
		namedParameters.addValue("email", email);

		try {
			return jdbc.queryForObject(query, namedParameters, new BeanPropertyRowMapper<>(User.class));

		} catch (EmptyResultDataAccessException erdae) { //If user is not found, return null
			return null;
		}
	}

	// Method to get User Roles for a specific User id
	public List<String> getRolesById(Long userId) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT sec_role.roleName " + "FROM user_role, sec_role "
				+ "WHERE user_role.roleId = sec_role.roleId " + "AND userId = :userId";

		namedParameters.addValue("userId", userId);
		return jdbc.queryForList(query, namedParameters, String.class);
	}

	//Method to add a user to the user database
	public void addUser(String email, String password) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO sec_user (email, encryptedPassword, enabled) VALUES (:email, :encryptedPassword, 1)";

		namedParameters.addValue("email", email);
		namedParameters.addValue("encryptedPassword", passwordEncoder.encode(password)); //Encrypting password before entry

		jdbc.update(query, namedParameters);
	}

	//Method to add specific roles to a user
	public void addRole(Long userId, Long roleId) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO user_role (userId, roleId) VALUES (:userId, :roleId)";

		namedParameters.addValue("userId", userId);
		namedParameters.addValue("roleId", roleId);

		jdbc.update(query, namedParameters);
	}

}
