DELIMITER $$

CREATE PROCEDURE `synchronizeStatistics`()
BEGIN
	DECLARE done INT DEFAULT FALSE;
	DECLARE s_id INT;
	DECLARE cur1 CURSOR FOR SELECT SURVEY_ID FROM SURVEYS;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

	OPEN cur1;

	read_loop: LOOP

		FETCH cur1 INTO s_id;
		IF done THEN
		  LEAVE read_loop;
		END IF;
		
		call clearStatistics(s_id);
		call updateStatistics(s_id);
		
		
	END LOOP;

	CLOSE cur1;
END