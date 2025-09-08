-- liquibase formatted sql
-- changeset appointment:update_db_generic_attributes_appointment_2.4.3-2.4.5.sql
-- preconditions onFail:MARK_RAN onError:WARN
/* Add a parameter to specify that Entries of type 'Session' should
 * not be updated when an appointment is being modified
 * */
INSERT INTO genatt_field ( id_entry, title, code, value, default_value )
	SELECT e.id_entry, e.title, 'is_updatable', 'false', 0
	FROM genatt_entry e
	INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
	WHERE e.resource_type = 'APPOINTMENT_FORM'
	AND t.class_name = 'appointment.entryTypeSession';