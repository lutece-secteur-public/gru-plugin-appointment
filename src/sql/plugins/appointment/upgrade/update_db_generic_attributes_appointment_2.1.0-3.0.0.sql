-- liquibase formatted sql
-- lutece runAfter:genericattributes
-- Scripts formerly shipped under sql/plugins/genericattributes (LUT-33258), first released in plugin-appointment 3.0.0

-- changeset appointment:update_db_generic_attributes_appointment_1.2.1-1.3.2.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_generic_attributes_appointment_1.2.1-1.3.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- 
-- Add a new column for the iteration number in genatt_response table
-- 
ALTER TABLE genatt_response ADD COLUMN iteration_number int default -1 AFTER id_entry;

-- 
-- Trim the title of all existing entry
-- 
UPDATE genatt_entry SET title = TRIM(title);

-- 
-- Add a new column for the EntryType icon name in genatt_entry_type table
-- 
ALTER TABLE genatt_entry_type ADD COLUMN icon_name varchar(255) AFTER class_name;
--
-- Add a new column for the editable back in genatt_entry table
--
ALTER TABLE genatt_entry ADD COLUMN (is_editable_back smallint DEFAULT '0');

ALTER TABLE genatt_entry ADD COLUMN (is_indexed SMALLINT default 0 NOT NULL);

ALTER TABLE genatt_entry MODIFY COLUMN code varchar(100) default NULL; 
ALTER TABLE genatt_field MODIFY COLUMN code varchar(100) default NULL; 

CREATE INDEX index_genatt_code ON genatt_entry ( code);

ALTER TABLE genatt_entry ADD is_shown_in_completeness smallint DEFAULT '0';

/*
    Note : AUTO_INCREMENT fields work with Mysql. For PostgreSQL, you have to 
    replace AUTO_INCREMENT, depending on your PostgreSQL version. You can
    find an example on buid.properties in target/lutece/sql directory after compilation.
*/
ALTER TABLE genatt_response MODIFY id_response INT AUTO_INCREMENT;


-- changeset appointment:update_db_generic_attributes_appointment_1.3.2-1.3.3.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_generic_attributes_appointment_1.3.2-1.3.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
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


-- changeset appointment:update_db_generic_attributes_appointment_1.3.3-2.0.0.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_generic_attributes_appointment_1.3.3-2.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_entry DROP COLUMN num_row;
ALTER TABLE genatt_entry DROP COLUMN num_column;

INSERT INTO genatt_field (id_entry, code, VALUE, title)
	SELECT e.id_entry, 'confirm_field', 
	case e.confirm_field WHEN 1 THEN 'true' ELSE 'false' END, 
	e.confirm_field_title from genatt_entry e 
	INNER JOIN genatt_entry_type t ON t.id_type = e.id_type 
	WHERE resource_type = 'appointment' 
	AND t.class_name = 'appointment.entryTypeText';
	
ALTER TABLE genatt_entry DROP COLUMN confirm_field;
ALTER TABLE genatt_entry DROP COLUMN confirm_field_title;

INSERT INTO genatt_field ( id_entry, code, value)
	SELECT id_entry, 'width', width from genatt_field WHERE width > 0 AND code not in  ('file_config', 'user_config');

INSERT INTO genatt_field ( id_entry, code, value)
	SELECT id_entry, 'height', height from genatt_field WHERE height > 0;
	
INSERT INTO genatt_field ( id_entry, code, value)
	SELECT id_entry, 'max_size', max_size_enter from genatt_field WHERE max_size_enter is not null AND max_size_enter != 0;
	
DELETE FROM genatt_field where code = 'file_config';
DELETE FROM genatt_field where code = 'user_config';

ALTER TABLE genatt_field DROP COLUMN width;
	
ALTER TABLE genatt_field DROP COLUMN height;
	
ALTER TABLE genatt_field DROP COLUMN max_size_enter;

ALTER TABLE genatt_entry DROP COLUMN map_provider;

ALTER TABLE genatt_field DROP COLUMN image_type;

ALTER TABLE genatt_entry DROP COLUMN is_role_associated;
ALTER TABLE genatt_field DROP COLUMN role_key;

ALTER TABLE genatt_entry modify COLUMN id_entry int AUTO_INCREMENT NOT NULL;

