INSERT INTO categories (name) VALUES ('Ficción'), ('Ciencia'), ('Historia');

INSERT INTO authors (name, bio) VALUES
 ('Gabriel García Márquez', 'Novelista colombiano, Nobel de Literatura 1982'),
 ('Carl Sagan', 'Astrónomo y divulgador científico');

INSERT INTO books (title, description, price, cover_url, published_year, author_id, category_id) VALUES
 ('Cien años de soledad', 'La saga de los Buendía en Macondo', 299.00, 'https://example.com/cien.jpg', 1967, 1, 1),
 ('Cosmos', 'Un viaje por el universo', 349.00, 'https://example.com/cosmos.jpg', 1980, 2, 2);