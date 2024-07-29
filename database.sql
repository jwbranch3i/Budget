CREATE TABLE budget.categery (
	id INT auto_increment NOT NULL,
	`type` INT NOT NULL,
	parent varchar(100) NULL,
	category varchar(100) NOT NULL,
	CONSTRAINT categery_pk PRIMARY KEY (id)
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

CREATE TABLE budget.budget(
	id INT auto_increment NOT NULL PRIMARY KEY,
	category INT NOT NULL,
	date DATE NOT NULL,
	amount DECIMAL(10,2) NOT NULL,
	FOREIGN KEY (category)
		REFERENCES budget.category(id)
		ON DELETE CASCADE		
)




 SELECT category.id, category.category AS CATEGORY,
       actual.amount AS ACTUAL,
       budget.amount as BUDGET,
       FROM category 
       left JOIN actual ON actual.category = category.id
       left join budget on budget.category = category.id
       where month(actual.date) = 7 and year(actual.date) = 2024;            
            