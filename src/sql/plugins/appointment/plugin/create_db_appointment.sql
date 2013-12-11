--
-- Structure for table appointment_appointment
--
DROP TABLE IF EXISTS appointment_appointment;
CREATE TABLE appointment_appointment (		
	id_appointment int(11) NOT NULL default '0',
	first_name varchar(255) NOT NULL default '',
	last_name varchar(255) NOT NULL default '',
	email varchar(255) NOT NULL default '',
	id_user varchar(255) NOT NULL default '',
	time_appointment varchar(10) NOT NULL default '0',
	date_appointment date NOT NULL,
	PRIMARY KEY (id_appointment)
);

--
-- Structure for table appointment_form
--
DROP TABLE IF EXISTS appointment_form;
CREATE TABLE appointment_form (		
	id_form int(11) NOT NULL default '0',
	title varchar(255) NOT NULL default '',
	time_start varchar(10) NOT NULL default '0',
	time_end varchar(10) NOT NULL default '0',
	duration_appointments int(11) NOT NULL default '0',
	is_open_monday SMALLINT NOT NULL,
	is_open_tuesday SMALLINT NOT NULL,
	is_open_wednesday SMALLINT NOT NULL,
	is_open_thursday SMALLINT NOT NULL,
	is_open_friday SMALLINT NOT NULL,
	is_open_saturday SMALLINT NOT NULL,
	is_open_sunday SMALLINT NOT NULL,
	date_start_validity date NULL,
	date_end_validity date NULL,
	is_active SMALLINT NOT NULL,
	dispolay_title_fo SMALLINT NOT NULL,
	nb_weeks_to_display int(11) NOT NULL default '0',
	people_per_appointment int(11) NOT NULL default '0',
	id_workflow int(11) NOT NULL default '0',
	PRIMARY KEY (id_form)
);
