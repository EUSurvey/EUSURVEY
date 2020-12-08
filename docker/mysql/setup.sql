create database eusurveydb character set utf8 COLLATE utf8_general_ci;
create database eusurveyreportdb character set utf8 COLLATE utf8_general_ci;

CREATE USER 'eusurveyuser'@'localhost' IDENTIFIED BY 'eusurveyuser'; 
GRANT ALL PRIVILEGES ON eusurveydb.* TO 'eusurveyuser'@'localhost';
GRANT EVENT ON *.* TO 'eusurveyuser'@'localhost'; 

CREATE USER 'eusurveyruser'@'localhost' IDENTIFIED BY 'eusurveyruser'; 
GRANT ALL PRIVILEGES ON eusurveyreportdb.* TO 'eusurveyruser'@'localhost';

SET GLOBAL event_scheduler = ON;
SET GLOBAL log_bin_trust_function_creators = 1;
SET GLOBAL TRANSACTION ISOLATION LEVEL READ COMMITTED;