create database eusurveydb character set utf8 COLLATE utf8_general_ci;
create database eusurveyreportdb character set utf8 COLLATE utf8_general_ci;

CREATE USER 'eusurveyuser'@'%' IDENTIFIED BY 'eusurveyuser'; 
GRANT ALL PRIVILEGES ON eusurveydb.* TO 'eusurveyuser'@'%';
GRANT EVENT ON *.* TO 'eusurveyuser'@'%'; 

CREATE USER 'eusurveyruser'@'%' IDENTIFIED BY 'eusurveyruser'; 
GRANT ALL PRIVILEGES ON eusurveyreportdb.* TO 'eusurveyruser'@'%';

SET GLOBAL event_scheduler = ON;
SET GLOBAL log_bin_trust_function_creators = 1;
SET GLOBAL TRANSACTION ISOLATION LEVEL READ COMMITTED;

use eusurveydb;
SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
CREATE TABLE IF NOT EXISTS MV_SURVEYS_NUMBERPUBLISHEDANSWERS AS SELECT 1 AS DUMMY;
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;