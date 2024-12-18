
-- actual definition

CREATE TABLE actual (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	category INTEGER NOT NULL,
	date TEXT NOT NULL,
	actual NUMERIC NOT NULL,
	budget NUMERIC NOT NULL, startBal NUMERIC DEFAULT (0) NOT NULL, endBal NUMERIC DEFAULT (0) NOT NULL,
	CONSTRAINT actual_category_FK FOREIGN KEY (category) REFERENCES category(id) ON DELETE CASCADE
);

-- category definition

CREATE TABLE category (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	type INTEGER NOT NULL,
	parent TEXT,
	main_category INTEGER DEFAULT(1) NOT NULL,
	category TEXT NOT NULL,
	in_total INTEGER DEFAULT(1) NOT NULL,
    hide INTEGER DEFAULT (0) NOT NULL, 
	acct INTEGER DEFAULT (0) NOT NULL, 
	balance REAL DEFAULT (0) NOT NULL);


-- SELECT * 
SELECT * 
FROM category 
WHERE id NOT IN (SELECT category FROM actual WHERE strftime('%m', date) = '10'
);


SELECT id, type, parent, category 
FROM category 
WHERE id NOT IN (SELECT category FROM actual WHERE strftime('%m', date) = '10' AND STRFTIME('%Y', actual.date) = '2024'
);


UPDATE category
SET budget = (
	SELECT budget
	FROM actual
	WHERE category.id = actual.category
	AND strftime('%Y-%m', actual.date) = '2023-10'
)
WHERE EXISTS (
	SELECT 1
	FROM actual
	WHERE category.id = actual.category
	AND strftime('%Y-%m', actual.date) = '2023-10'
);





UPDATE actual
SET budget = COALESCE((
    SELECT budget
    FROM actual AS a
    WHERE a.category = actual.category
	AND strftime('%Y-%m', a.date) = ?
), 0)
WHERE strftime('%Y-%m', actual.date) = ?;

-- update budget for current month
UPDATE actual
SET budget = COALESCE((
	SELECT budget
	FROM actual AS a
	WHERE a.category = actual.category
	AND strftime('%Y-%m', a.date) = '2024-10'
), 0)
WHERE strftime('%Y-%m', actual.date) = '2024-11';


UPDATE actual
SET startBal = COALESCE((
	SELECT endBal - actual
	FROM actual AS a
	WHERE a.category = actual.category
	AND strftime('%Y-%m', a.date) = '2024-10'
), 0)
WHERE strftime('%Y-%m', actual.date) = '2024-11';