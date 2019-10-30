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
