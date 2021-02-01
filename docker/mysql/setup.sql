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

CREATE TABLE IF NOT EXISTS mv_surveys_numberpublishedanswers (
  SURVEYUID varchar(255) NOT NULL,
  PUBLISHEDANSWERS bigint(21) NOT NULL DEFAULT 0,
  LASTANSWER datetime DEFAULT NULL,
  MW_TIMESTAMP datetime DEFAULT NULL,
  KEY MV_SURVEYS_IND (SURVEYUID,PUBLISHEDANSWERS)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
