SET character_set_server=utf8;
SET collation_server=utf8_bin;

-- -------------------------------------
-- TAG TYPES
-- -------------------------------------
DELETE FROM gimmi.tagType;

LOAD DATA LOCAL INFILE 'tagType.csv' REPLACE
INTO TABLE gimmi.tagType
CHARACTER SET utf8
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(tag_type_id, name_en, name_de);