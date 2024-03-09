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
    status varchar(50 ) DEFAULT 'Approved',
	transfer_type varchar(50) NOT NULL,
    CONSTRAINT CHK_amount_gt_0 CHECK (amount_to_transfer > 0)
);

CREATE TABLE accounts (
    account_id serial PRIMARY KEY,
    user_id int NOT NULL,
    balance numeric (10,2),
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE account_transfers(
	account_id int REFERENCES accounts (account_id) NOT NULL,
	transfer_id int REFERENCES transfers (transfer_id) NOT NULL,
	CONSTRAINT pk_account_transfer PRIMARY KEY (account_id, transfer_id)
);

INSERT INTO users (username,password_hash,role) VALUES ('user1','user1','ROLE_USER'); -- 1
INSERT INTO users (username,password_hash,role) VALUES ('user2','user2','ROLE_USER'); -- 2
INSERT INTO users (username,password_hash,role) VALUES ('user3','user3','ROLE_USER'); -- 3

INSERT INTO transfers (transfer_id, user_from_id, user_to_id, amount_to_transfer, transfer_type)
                       VALUES(1, 1, 2, 20, 'Send');
INSERT INTO transfers (transfer_id, user_from_id, user_to_id, amount_to_transfer, transfer_type)
                       VALUES(2, 3, 4, 1000, 'Request');
INSERT INTO transfers (transfer_id, user_from_id, user_to_id, amount_to_transfer, transfer_type)
                       VALUES(3, 4, 5, 500, 'Send');

INSERT INTO accounts (account_id, user_id, balance) VALUES (1, 1, 1000);
INSERT INTO accounts (account_id, user_id, balance) VALUES (2, 2, 500);
INSERT INTO accounts (account_id, user_id, balance) VALUES (3, 3, 1500);


COMMIT TRANSACTION;
