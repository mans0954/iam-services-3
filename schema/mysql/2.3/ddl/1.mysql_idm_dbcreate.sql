DROP DATABASE IF EXISTS openiam;

/* Creates the DB and initial user account and privleges */

CREATE database openiam DEFAULT CHARACTER SET utf8
CHARACTER SET = utf8;
USE openiam;

DELIMITER $$

DROP PROCEDURE IF EXISTS dropIdmUser$$

CREATE PROCEDURE dropIdmUser()
	BEGIN
		
		IF ((SELECT count(*) FROM mysql.user WHERE USER='idmuser') > 1) THEN
			DROP USER 'idmuser'@'localhost';
		END IF;
		
	END$$
DELIMITER ;

call dropIdmUser();

DROP PROCEDURE dropIdmUser;

CREATE USER 'idmuser'@'localhost' IDENTIFIED BY 'idmuser';
GRANT ALL ON *.* TO 'idmuser'@'localhost'; 

/* enable remote access to the database */
GRANT ALL ON openiam.* TO idmuser@'*' IDENTIFIED BY 'idmuser';
