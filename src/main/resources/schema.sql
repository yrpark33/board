CREATE TABLE tbl_board (
	board_id BIGINT AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(500) NOT NULL,
	content VARCHAR(2000) NOT NULL,
	writer VARCHAR(50) NOT NULL,
	created_at DATETIME DEFAULT NOW(),
	updated_at DATETIME,
	deleted BOOLEAN DEFAULT FALSE,
	deleted_at DATETIME
);

CREATE TABLE tbl_comment (
	comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
	board_id BIGINT NOT NULL,
	content VARCHAR(500) NOT NULL,
	writer VARCHAR(50) NOT NULL,
	created_at DATETIME DEFAULT NOW(),
	deleted BOOLEAN DEFAULT FALSE,
	deleted_at DATETIME,
	CONSTRAINT fk_comment_board FOREIGN KEY (board_id) REFERENCES tbl_board(board_id)
);


CREATE INDEX idx_comment_board ON tbl_comment(board_id DESC, comment_id ASC);

CREATE TABLE tbl_board_file (
	file_id BIGINT AUTO_INCREMENT PRIMARY KEY,
	board_id BIGINT NOT NULL,
	file_name VARCHAR(300) NOT NULL,
	UUID CHAR(36) NOT NULL,
	sort_order INT DEFAULT 0,
	image BOOLEAN DEFAULT FALSE,
	FOREIGN KEY (board_id) REFERENCES tbl_board(board_id)
);

CREATE INDEX idx_board_file_board_id ON tbl_board_file(board_id, file_id);

CREATE TABLE tbl_account (
	username VARCHAR(50) PRIMARY KEY,
	PASSWORD VARCHAR(100) NOT NULL,
	NAME VARCHAR(100) NOT NULL,
	email VARCHAR(100) UNIQUE,
	enabled BOOLEAN DEFAULT TRUE,
	created_at DATETIME DEFAULT NOW(),
	disabled_at DATETIME,
	profile_img VARCHAR(255)
);

CREATE TABLE tbl_account_roles (
	username VARCHAR(50) NOT NULL,
	rolename VARCHAR(50) NOT NULL,
	PRIMARY KEY (username, rolename),
	FOREIGN KEY (username) REFERENCES tbl_account(username)
);
	

CREATE TABLE persistent_logins (
	username VARCHAR(64) NOT NULL,
	series VARCHAR(64) PRIMARY KEY,
	token VARCHAR(64) NOT NULL,
	last_used TIMESTAMP NOT NULL
);





