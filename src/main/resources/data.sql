INSERT INTO book (title, author, isbn, price, description)
VALUES
  ('To Kill a Mocking Bird', 'Lee', '12345', '16.99', 'To Kill a Mockingbird by Harper Lee centres on Atticus Finchs attempts to prove the innocence of Tom Robinson, a black man who has been wrongly accused of a heinous crime'),
  ('1984', 'Orwell', '54321', '12.99', '1984 is a dystopian novel that was written by George Orwell and published in 1949. It tells the story of Winston Smith, a citizen of the miserable society of Oceania, who is trying to rebel against the Party and its omnipresent symbol, Big Brother.'),
  ('Wind up Bird Chronicle', 'Murakami', '98765', '14.99', 'In a Tokyo suburb, a young man named Toru Okada searches for his wifes missing cat—and then for his wife as well—in a netherworld beneath the citys placid surface. As these searches intersect, he encounters a bizarre group of allies and antagonists.');
  
INSERT INTO sec_user (email, encryptedPassword, enabled)
VALUES ('fahad.jan@sheridancollege.ca', '$2a$10$1ltibqiyyBJMJQ4hqM7f0OusP6np/IHshkYc4TjedwHnwwNChQZCy', 1);

INSERT INTO sec_user (email, encryptedPassword, enabled)
VALUES ('frank@sheridancollege.ca', '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 1);

INSERT INTO sec_role (roleName)
VALUES ('ROLE_USER');
 
INSERT INTO sec_role (roleName)
VALUES ('ROLE_GUEST');
  
INSERT INTO user_role (userId, roleId)
VALUES (1, 1);
 
INSERT INTO user_role (userId, roleId)
VALUES (2, 1);

