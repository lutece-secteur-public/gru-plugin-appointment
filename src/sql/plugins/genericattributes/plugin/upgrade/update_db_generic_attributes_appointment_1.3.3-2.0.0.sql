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
ALTER TABLE genatt_entry DROP COLUMN confirm_field_title
