SET character_set_server=utf8;
SET collation_server=utf8_bin;
-- allow us to be lazy
SET foreign_key_checks = 0;

-- -------------------------------------
-- CATEGORIES
-- -------------------------------------
DELETE FROM gimmi.category;

LOAD DATA LOCAL INFILE 'category.csv' REPLACE
INTO TABLE gimmi.category
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(category_id, name_en, name_de, path);

-- -------------------------------------
-- TAG TYPES
-- -------------------------------------
DELETE FROM gimmi.tagType;

LOAD DATA LOCAL INFILE 'tagType.csv' REPLACE
INTO TABLE gimmi.tagType
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(tag_type_id, name_en, name_de);

-- -------------------------------------
-- TAGS
-- -------------------------------------
DELETE FROM gimmi.tag;

LOAD DATA LOCAL INFILE 'tag.csv' REPLACE
INTO TABLE gimmi.tag
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(tag_id, tag_type_id, name_en, name_de);

-- -------------------------------------
-- DOMAINS
-- -------------------------------------
DELETE FROM gimmi.domain;

LOAD DATA LOCAL INFILE 'domain.csv' REPLACE
INTO TABLE gimmi.domain
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(domain_id, url, added);

-- -------------------------------------
-- SITES
-- -------------------------------------
DELETE FROM gimmi.site;

LOAD DATA LOCAL INFILE 'site.csv' REPLACE
INTO TABLE gimmi.site
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(site_id, crawl_time, language_id, domain_id, country_id, storage_path, root_file, title, url_path);

-- -------------------------------------
-- SITE TAGS
-- -------------------------------------
DELETE FROM gimmi.site_has_tag;

LOAD DATA LOCAL INFILE 'site_has_tag.csv' REPLACE
INTO TABLE gimmi.site_has_tag
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(site_id, tag_id);

-- -------------------------------------
-- SITE CATEGORIES
-- -------------------------------------
DELETE FROM gimmi.site_has_category;

LOAD DATA LOCAL INFILE 'site_has_category.csv' REPLACE
INTO TABLE gimmi.site_has_category
CHARACTER SET utf8
FIELDS TERMINATED BY';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(site_has_category_id, category_id, site_id);

-- stop us being lazy
SET foreign_key_checks = 1;