ALTER TABLE genatt_entry CHANGE COLUMN is_shown_in_completeness used_in_correct_form_response SMALLINT DEFAULT '0';

UPDATE genatt_field f SET f.CODE = 'default_date_value'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'appointment.entryTypeDate'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = f.title
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'appointment.entryTypeGeolocation'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.title = null
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'appointment.entryTypeGeolocation'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.VALUE = f.title
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'appointment.entryTypeNumbering'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'prefix'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'appointment.entryTypeNumbering'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.title = null
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'appointment.entryTypeNumbering'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = f.title
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'appointment.entryTypeImage', 'appointment.entryTypeFile')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'file_config'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'appointment.entryTypeImage', 'appointment.entryTypeFile')
AND f.code is null
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.title = null
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'appointment.entryTypeImage', 'appointment.entryTypeFile')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'answer_choice'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'appointment.entryTypeSelect', 'appointment.entryTypeRadioButton', 'appointment.entryTypeCheckBox')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'text_config'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'appointment.entryTypeText', 'appointment.entryTypePhone')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'text_config'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'appointment.entryTypeTextArea')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'attribute_name'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'appointment.entryTypeSession')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'user_config'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'appointment.entryTypeMyLuteceUser')
AND e.id_entry = f.id_entry);

ALTER TABLE genatt_field MODIFY id_field INT AUTO_INCREMENT;

ALTER TABLE genatt_entry DROP COLUMN used_in_correct_form_response;
