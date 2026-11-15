-- fans_setup.sql
-- Home/dev setup only - NOT run by FanInfo.java itself.
--
-- The assignment states that when FanInfo is graded, "databasedb", user
-- "student1", and the populated "fans" table will already exist. This
-- script is just what the assignment allows you to run by hand against
-- your own local MySQL server so you have something to test FanInfo
-- against. Run it once, as an admin user, in the MySQL client/Workbench
-- connected to your local server:
--
--   mysql -u root -p < fans_setup.sql

CREATE DATABASE IF NOT EXISTS databasedb;

CREATE USER IF NOT EXISTS 'student1'@'localhost' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON databasedb.* TO 'student1'@'localhost';
FLUSH PRIVILEGES;

USE databasedb;

CREATE TABLE IF NOT EXISTS fans (
    id INT PRIMARY KEY,
    firstname VARCHAR(25),
    lastname VARCHAR(25),
    favoriteteam VARCHAR(25)
);

INSERT INTO fans (id, firstname, lastname, favoriteteam) VALUES
    (1, 'John', 'Smith', 'Cubs'),
    (2, 'Jane', 'Doe', 'Yankees'),
    (3, 'Mike', 'Brown', 'Packers');
