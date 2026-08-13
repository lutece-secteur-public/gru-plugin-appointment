-- liquibase formatted sql
-- changeset appointment:update_db_generic_attributes_appointment_3.0.8-3.0.9.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_field (id_entry, code, value, default_value, pos, no_display_title)
SELECT e.id_entry, 'file_config', null, 0, 0, 0
FROM genatt_entry e
        INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN ( 'appointment.entryTypeImage', 'appointment.entryTypeFile')
    and not exists (select 1 from genatt_field where id_entry=e.id_entry and code='file_config' );