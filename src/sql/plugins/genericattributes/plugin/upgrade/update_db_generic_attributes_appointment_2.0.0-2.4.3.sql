/*
Update the Appointment Plugin's Generic Attributes (proper icons, order and activation state), and mofidy
their IDs to avoid conflict with the Generic Attributes from the Forms Plugin.
Also update the genatt_entry's 'id_type' values to match with the new ones
*/
UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 123, gentype.id_type = 123, gentype.icon_name = 'dot-circle', gentype.display_order = 1, gentype.inactive = 0
WHERE gentype.id_type = 101 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 124, gentype.id_type = 124, gentype.icon_name = 'check-square', gentype.display_order = 2, gentype.inactive = 0
WHERE gentype.id_type = 102 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 125, gentype.id_type = 125, gentype.icon_name = 'comment', gentype.display_order = 3, gentype.inactive = 0
WHERE gentype.id_type = 103 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 126, gentype.id_type = 126, gentype.icon_name = 'calendar', gentype.display_order = 4, gentype.inactive = 0
WHERE gentype.id_type = 104 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 127, gentype.id_type = 127, gentype.icon_name = 'list-alt', gentype.display_order = 5, gentype.inactive = 0
WHERE gentype.id_type = 105 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 128, gentype.id_type = 128, gentype.icon_name = 'file-alt', gentype.display_order = 6, gentype.inactive = 0
WHERE gentype.id_type = 106 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 129, gentype.id_type = 129, gentype.icon_name = 'sticky-note', gentype.display_order = 7, gentype.inactive = 0
WHERE gentype.id_type = 107 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 130, gentype.id_type = 130, gentype.icon_name = 'hashtag', gentype.display_order = 8, gentype.inactive = 0
WHERE gentype.id_type = 108 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 131, gentype.id_type = 131, gentype.icon_name = 'indent', gentype.display_order = 9, gentype.inactive = 0
WHERE gentype.id_type = 109 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 132, gentype.id_type = 132, gentype.icon_name = 'list-alt', gentype.display_order = 10, gentype.inactive = 0
WHERE gentype.id_type = 110 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 133, gentype.id_type = 133, gentype.icon_name = 'map-marked-alt', gentype.display_order = 11, gentype.inactive = 0
WHERE gentype.id_type = 111 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 134, gentype.id_type = 134, gentype.icon_name = 'user', gentype.display_order = 12, gentype.inactive = 0
WHERE gentype.id_type = 112 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 135, gentype.id_type = 135, gentype.icon_name = 'user', gentype.display_order = 13, gentype.inactive = 0
WHERE gentype.id_type = 113 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 136, gentype.id_type = 136, gentype.icon_name = 'image', gentype.display_order = 14, gentype.inactive = 0
WHERE gentype.id_type = 114 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gentype.id_type = 137, gen.id_type = 137, gentype.icon_name = 'file', gentype.display_order = 15, gentype.inactive = 0
WHERE gentype.id_type = 115 AND gentype.plugin = 'appointment';

UPDATE genatt_entry_type gentype
LEFT JOIN genatt_entry gen
ON gentype.id_type = gen.id_type
SET gen.id_type = 138, gentype.id_type = 138, gentype.icon_name = 'phone-square', gentype.display_order = 16, gentype.inactive = 1
WHERE gentype.id_type = 116 AND gentype.plugin = 'appointment';
