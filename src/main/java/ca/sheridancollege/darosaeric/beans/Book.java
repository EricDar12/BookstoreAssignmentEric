package ca.sheridancollege.darosaeric.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Book {
	
	private Long id;
	private String title;
	private String author;
	private String isbn;
	private Double price;
	private String description;

}

