-- liquibase formatted sql
-- lutece runAfter:genericattributes
-- Second changeset formerly sql/plugins/genericattributes/upgrade/update_db_generic_attributes_appointment_2.4.3-2.4.5.sql (LUT-33258), first released in plugin-appointment 3.0.9
-- changeset appointment:update_db_generic_attributes_appointment_3.0.8-3.0.9.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_generic_attributes_appointment_3.0.8-3.0.9.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_field (id_entry, code, value, default_value, pos, no_display_title)
SELECT e.id_entry, 'file_config', null, 0, 0, 0
FROM genatt_entry e
        INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN ( 'appointment.entryTypeImage', 'appointment.entryTypeFile')
    and not exists (select 1 from genatt_field where id_entry=e.id_entry and code='file_config' );
-- changeset appointment:update_db_generic_attributes_appointment_2.4.3-2.4.5.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_generic_attributes_appointment_2.4.3-2.4.5.sql
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
