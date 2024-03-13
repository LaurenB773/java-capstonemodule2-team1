BEGIN TRANSACTION;

DROP TABLE IF EXISTS users, transfers, accounts;

CREATE TABLE users (
    user_id serial NOT NULL,
    username varchar(50) NOT NULL,
    password_hash varchar(200) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(50),
    role varchar(20),
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uq_username UNIQUE (username)
);

CREATE TABLE transfers (
    transfer_id serial PRIMARY KEY,
    user_from_id int NOT NULL,
    user_to_id int NOT NULL,
    amount_to_transfer numeric(10,2) not null, 
    status varchar(50 ) DEFAULT 'Pending',
	transfer_type varchar(50) NOT NULL,
    CONSTRAINT CHK_amount_gt_0 CHECK (amount_to_transfer > 0)
);

CREATE TABLE accounts (
    account_id serial PRIMARY KEY,
    user_id int NOT NULL,
    balance numeric (10,2),
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users (user_id)
);


COMMIT TRANSACTION;



