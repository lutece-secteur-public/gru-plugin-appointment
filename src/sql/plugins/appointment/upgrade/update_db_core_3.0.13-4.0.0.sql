-- liquibase formatted sql
-- changeset appointment:update_db_core_3.0.13-4.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'core_portlet' AND column_name = 'id_template'
--
-- The plugin upgrade scripts run BEFORE the core upgrade script in the same liquibase run (sql/plugins/* sorts before sql/upgrade/*) :
-- the core structures are created here when they do not exist yet, with the very same statements as the core script, which is then skipped.
--
ALTER TABLE core_portlet ADD COLUMN id_template int default 0 NOT NULL;

-- changeset appointment:update_db_core_3.0.13-4.0.0.sql-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name = 'core_portlet_template'
CREATE TABLE IF NOT EXISTS core_portlet_template (
	id_template int AUTO_INCREMENT NOT NULL,
	id_portlet_type varchar(50) default NULL,
	description varchar(255) default NULL,
	template_path varchar(255) default NULL,
	PRIMARY KEY (id_template)
);

-- changeset appointment:update_db_core_3.0.13-4.0.0.sql-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'APPOINTMENT_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('APPOINTMENT_PORTLET', 'Défaut', 'skin/plugins/appointment/portlet/appointment_portlet.html');

-- changeset appointment:update_db_core_3.0.13-4.0.0.sql-rev3.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'APPOINTMENT_FORM_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('APPOINTMENT_FORM_PORTLET', 'Défaut', 'skin/plugins/appointment/portlet/appointment_form_portlet.html');

-- changeset appointment:update_db_core_3.0.13-4.0.0.sql-rev4.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'APPOINTMENT_FORM_LIST_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('APPOINTMENT_FORM_LIST_PORTLET', 'Défaut', 'skin/plugins/appointment/portlet/appointment_form_list_portlet.html');

-- changeset appointment:update_db_core_3.0.13-4.0.0.sql-rev5.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The appointment portlets had no XSL style of their own : they are all rendered with the default template of their type
UPDATE core_portlet SET id_style = 0 WHERE id_portlet_type IN ('APPOINTMENT_PORTLET', 'APPOINTMENT_FORM_PORTLET', 'APPOINTMENT_FORM_LIST_PORTLET');
