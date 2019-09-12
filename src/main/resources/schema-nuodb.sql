-- Spring Boot automatically runs the SQL in this file
--
-- We could get Hibernate to do this for us, but often you want more control.
-- Your choice.

DROP TABLE demo.Accounts IF EXISTS;
CREATE TABLE demo.Accounts (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name STRING);
ALTER TABLE demo.Accounts ADD COLUMN balance INT;
