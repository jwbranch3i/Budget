CREATE TABLE budget.catogery (
	id INT auto_increment NOT NULL,
	`type` INT NOT NULL,
	parent varchar(100) NULL,
	category varchar(100) NOT NULL,
	CONSTRAINT catogery_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;



CREATE TABLE budget.actual(
	id INT auto_increment NOT NULL PRIMARY KEY,
	category INT NOT NULL,
	date DATE NOT NULL,
	amount DECIMAL(10,2) NOT NULL,
	FOREIGN KEY (category)
		REFERENCES budget.category(id)
		ON DELETE CASCADE		
)