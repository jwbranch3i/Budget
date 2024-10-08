CREATE TABLE category (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	"type" INTEGER NOT NULL,
	parent TEXT,
	category TEXT NOT NULL
);

CREATE TABLE actual (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	category INTEGER NOT NULL,
	date TEXT NOT NULL,
	actual NUMERIC NOT NULL,
	budget NUMERIC NOT NULL,
	CONSTRAINT actual_category_FK FOREIGN KEY (category) REFERENCES category(id) ON DELETE CASCADE
);

