-- liquibase formatted sql
-- changeset appointment:update_db_appointment_3.0.8-3.0.13.sql
-- preconditions onFail:MARK_RAN onError:WARN

-- Repair duplicate user on appointment : Create unique id_user for each duplicate user
DELIMITER //
CREATE PROCEDURE repair_appointment_users()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_appointment_id INT;
    DECLARE v_old_user_id INT;
    DECLARE v_new_user_id INT;

    -- Get all duplicate user
    DECLARE cur CURSOR FOR 
        SELECT id_appointment, id_user 
        FROM appointment_appointment 
        WHERE id_appointment NOT IN (
            SELECT max_id FROM (SELECT MAX(id_appointment) as max_id FROM appointment_appointment GROUP BY id_user) as tmp
        );
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_appointment_id, v_old_user_id;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- Create new user (to replace duplicate user)
        INSERT INTO appointment_user (guid, first_name, last_name, email, phone_number)
        SELECT guid, first_name, last_name, email, phone_number 
        FROM appointment_user 
        WHERE id_user = v_old_user_id;
        
        SET v_new_user_id = LAST_INSERT_ID();

        -- Update appointment with the new user
        UPDATE appointment_appointment 
        SET id_user = v_new_user_id 
        WHERE id_appointment = v_appointment_id;
    END LOOP;
    CLOSE cur;
END //
DELIMITER ;

CALL repair_appointment_users();
DROP PROCEDURE repair_appointment_users;

-- Delete appointments already moved to the target state of a DELETE archive task.
DELETE appointment_response, response
FROM appointment_appointment_response appointment_response
INNER JOIN genatt_response response ON response.id_response = appointment_response.id_response
INNER JOIN appointment_appointment app ON app.id_appointment = appointment_response.id_appointment
INNER JOIN workflow_resource_workflow rw
    ON rw.id_resource = app.id_appointment
    AND rw.resource_type = 'appointment'
INNER JOIN workflow_task_archive_cf archive_cf ON archive_cf.next_state = rw.id_state
INNER JOIN workflow_task archive_task ON archive_task.id_task = archive_cf.id_task
INNER JOIN workflow_action archive_action ON archive_action.id_action = archive_task.id_action AND archive_action.id_workflow = rw.id_workflow
WHERE archive_cf.type_archival = 'DELETE';

-- Delete appointment slots already moved to the target state of a DELETE archive task.
DELETE appointment_slot
FROM appointment_appointment_slot appointment_slot
INNER JOIN appointment_appointment app ON app.id_appointment = appointment_slot.id_appointment
INNER JOIN workflow_resource_workflow rw
    ON rw.id_resource = app.id_appointment
    AND rw.resource_type = 'appointment'
INNER JOIN workflow_task_archive_cf archive_cf ON archive_cf.next_state = rw.id_state
INNER JOIN workflow_task archive_task ON archive_task.id_task = archive_cf.id_task
INNER JOIN workflow_action archive_action ON archive_action.id_action = archive_task.id_action AND archive_action.id_workflow = rw.id_workflow
WHERE archive_cf.type_archival = 'DELETE';

-- Delete appointment already moved to the target state of a DELETE archive task.
DELETE app
FROM appointment_appointment app
INNER JOIN workflow_resource_workflow rw ON rw.id_resource = app.id_appointment AND rw.resource_type = 'appointment'
INNER JOIN workflow_task_archive_cf archive_cf ON archive_cf.next_state = rw.id_state
INNER JOIN workflow_task archive_task ON archive_task.id_task = archive_cf.id_task
INNER JOIN workflow_action archive_action ON archive_action.id_action = archive_task.id_action AND archive_action.id_workflow = rw.id_workflow
WHERE archive_cf.type_archival = 'DELETE';


