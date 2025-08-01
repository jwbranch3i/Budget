-- category definition

CREATE TABLE category (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	type INTEGER NOT NULL,
	parent TEXT,
	main_category INTEGER DEFAULT(1) NOT NULL,
	category TEXT NOT NULL,
	include_in_total INTEGER DEFAULT(1) NOT NULL,
    hide INTEGER DEFAULT (0) NOT NULL, 
	acct INTEGER DEFAULT (0) NOT NULL, 
	balance REAL DEFAULT (0) NOT NULL, 
	default_maximum_amount REAL DEFAULT (0) NOT NULL);

-- actual definition

CREATE TABLE actual (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	category INTEGER NOT NULL,
	date TEXT NOT NULL,
	actual NUMERIC NOT NULL,
	budget NUMERIC NOT NULL, startBal NUMERIC DEFAULT (0) NOT NULL, endBal NUMERIC DEFAULT (0) NOT NULL,
	CONSTRAINT actual_category_FK FOREIGN KEY (category) REFERENCES category(id) ON DELETE CASCADE
);


-- Accounts definition

CREATE TABLE Accounts (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	acctName TEXT NOT NULL
);

-- category_running_totals definition

CREATE TABLE category_running_totals (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_id INTEGER NOT NULL,
    month_date TEXT NOT NULL, -- Format: YYYY-MM
    previous_balance REAL DEFAULT 0.0,
    current_difference REAL DEFAULT 0.0, -- (budget - actual)
    running_total REAL DEFAULT 0.0,
    maximum_amount REAL DEFAULT NULL, -- Optional maximum
    warning_issued BOOLEAN DEFAULT FALSE,
    created_date TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_date TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    UNIQUE(category_id, month_date)
);

CREATE INDEX idx_category_running_totals_category_date 
ON category_running_totals(category_id, month_date)	
;