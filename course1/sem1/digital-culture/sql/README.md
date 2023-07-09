# Задание 1

```SQL
	SELECT COUNT(*) FROM `User` WHERE `passwordHash` IS NULL;
```

# Задание 2

```SQL
	SELECT DISTINCT userId FROM `Post` ORDER BY `Post`.`userId` ASC;
```

# Задание 3 

```SQL
	SELECT title AS postTitle, (SELECT name FROM User WHERE User.id = Post.userId) AS authorName FROM Post ORDER BY creationTime ASC, id ASC
```

# Задание 4

```SQL
	SELECT a.title FROM Post a WHERE a.id = (SELECT b.id FROM Post b WHERE b.userId = a.userId ORDER BY b.creationTime, b.id OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY)
```

# Задание 5

```SQL
	SELECT DATE(creationTime) as postCreationDate, COUNT(*) as postCount FROM Post GROUP BY DATE(creationTime)
```

# Задание 6

```SQL
	CREATE TABLE Comment(
		id bigint(20) PRIMARY KEY AUTO_INCERTMENT NOT NULL,
		postId bigint(20) NOT NULL,
		userId bigint(20) NOT NULL,
		text longtext,
		creationTime datetime,
		FOREIGN KEY (postId) REFERENCES Post(id),
		FOREIGN KEY (userId) REFERENCES User(id)
		);
	INSERT INTO Comment(postId, userId, text, creationTime) VALUES (...)
```

# Задание 7

```SQL
	SELECT id FROM Post ORDER BY (SELECT COUNT(*) FROM Comment WHERE Comment.postId=Post.id) DESC;
```
