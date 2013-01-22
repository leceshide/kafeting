CREATE DATABASE TalkDB
USE TalkDB
/*用户统计 */
CREATE TABLE users(
id INT AUTO_INCREMENT PRIMARY KEY,
session_id VARCHAR(200),
ip VARCHAR(100),
address VARCHAR(400),
login_in_date DATE,
login_in_time TIME,
login_out_datetime DATETIME,
remark VARCHAR(200)
)
/*用户会话表*/
CREATE TABLE chatgroup(
id INT AUTO_INCREMENT PRIMARY KEY,
sponsor_session_id VARCHAR(200),
acceptance_session_id VARCHAR(200),
begin_date DATE,
begin_time TIME,
end_datetime DATETIME,
state INT,
remark VARCHAR(200)
)

/*双方会话信息*/
CREATE TABLE messages(
id INT AUTO_INCREMENT PRIMARY KEY,
sponsor_session_id VARCHAR(200),
acceptance_session_id VARCHAR(200),
content TEXT,
create_date DATE,
create_time TIME,
state INT ,
remark VARCHAR(200)
)
/*用户状态*/
CREATE TABLE userstate(
id INT AUTO_INCREMENT PRIMARY KEY,
session_id VARCHAR(200),
state INT,
sponsor_datetime TIME
)