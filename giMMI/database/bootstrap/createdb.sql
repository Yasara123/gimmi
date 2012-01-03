SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `gimmi` ;
CREATE SCHEMA IF NOT EXISTS `gimmi` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ;
USE `gimmi` ;

-- -----------------------------------------------------
-- Table `gimmi`.`tagType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gimmi`.`tagType` ;

CREATE  TABLE IF NOT EXISTS `gimmi`.`tagType` (
  `tag_type_id` INT NOT NULL AUTO_INCREMENT ,
  `name_de` VARCHAR(100) NOT NULL ,
  `name_en` VARCHAR(100) NULL ,
  PRIMARY KEY (`tag_type_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'Stores types of tags like \"official\", \"user\"';


-- -----------------------------------------------------
-- Table `gimmi`.`tag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gimmi`.`tag` ;

CREATE  TABLE IF NOT EXISTS `gimmi`.`tag` (
  `tag_id` INT NOT NULL AUTO_INCREMENT ,
  `tag_type_id` INT NOT NULL ,
  `name_de` VARCHAR(200) NOT NULL ,
  `name_en` VARCHAR(200) NULL ,
  PRIMARY KEY (`tag_id`) ,
  INDEX `fk_tagType` (`tag_type_id` ASC) ,
  CONSTRAINT `fk_tagType`
    FOREIGN KEY (`tag_type_id` )
    REFERENCES `gimmi`.`tagType` (`tag_type_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'Stores tag definitions';


-- -----------------------------------------------------
-- Table `gimmi`.`language`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gimmi`.`language` ;

CREATE  TABLE IF NOT EXISTS `gimmi`.`language` (
  `language_id` INT NOT NULL AUTO_INCREMENT ,
  `lang_code` VARCHAR(3) NOT NULL ,
  `name_de` VARCHAR(100) NULL ,
  `name_en` VARCHAR(100) NOT NULL ,
  PRIMARY KEY (`language_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'Language code mappings';


-- -----------------------------------------------------
-- Table `gimmi`.`domain`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gimmi`.`domain` ;

CREATE  TABLE IF NOT EXISTS `gimmi`.`domain` (
  `domain_id` INT NOT NULL AUTO_INCREMENT ,
  `url` VARCHAR(300) NOT NULL COMMENT 'Server portion of a source URL' ,
  `added` TIMESTAMP NOT NULL COMMENT 'Unix timestamp when this domain was added' ,
  PRIMARY KEY (`domain_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'Stores basic information about a resource (independant of a ' /* comment truncated */;


-- -----------------------------------------------------
-- Table `gimmi`.`country`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gimmi`.`country` ;

CREATE  TABLE IF NOT EXISTS `gimmi`.`country` (
  `country_id` INT NOT NULL AUTO_INCREMENT ,
  `country_code` VARCHAR(2) NOT NULL ,
  `name_de` VARCHAR(100) NULL ,
  `name_en` VARCHAR(100) NOT NULL ,
  `tld` VARCHAR(3) NULL ,
  PRIMARY KEY (`country_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `gimmi`.`site`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gimmi`.`site` ;

CREATE  TABLE IF NOT EXISTS `gimmi`.`site` (
  `site_id` INT NOT NULL AUTO_INCREMENT ,
  `crawl_time` TIMESTAMP NOT NULL COMMENT 'Unix Timestamp when this resource was crawled' ,
  `language_id` INT NOT NULL ,
  `domain_id` INT NOT NULL ,
  `country_id` INT NOT NULL ,
  `storage_path` VARCHAR(500) NOT NULL COMMENT 'Path to the copied data on disk' ,
  `root_file` VARCHAR(256) NOT NULL COMMENT 'Filename of the root document' ,
  `title` VARCHAR(256) NOT NULL ,
  `url_path` VARCHAR(500) NOT NULL ,
  PRIMARY KEY (`site_id`) ,
  INDEX `fk_language` (`language_id` ASC) ,
  INDEX `fk_source` (`domain_id` ASC) ,
  INDEX `fk_country` (`country_id` ASC) ,
  CONSTRAINT `fk_language`
    FOREIGN KEY (`language_id` )
    REFERENCES `gimmi`.`language` (`language_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_source`
    FOREIGN KEY (`domain_id` )
    REFERENCES `gimmi`.`domain` (`domain_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_country`
    FOREIGN KEY (`country_id` )
    REFERENCES `gimmi`.`country` (`country_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'Stores information about a resource that got crawled';


-- -----------------------------------------------------
-- Table `gimmi`.`site_has_tag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gimmi`.`site_has_tag` ;

CREATE  TABLE IF NOT EXISTS `gimmi`.`site_has_tag` (
  `site_has_tag_id` INT NOT NULL AUTO_INCREMENT ,
  `site_id` INT NOT NULL COMMENT 'Targets the object to be tagged' ,
  `tag_id` INT NOT NULL ,
  PRIMARY KEY (`site_has_tag_id`) ,
  INDEX `fk_tagId` (`tag_id` ASC) ,
  INDEX `fk_tagMap_sourceData1` (`site_id` ASC) ,
  CONSTRAINT `fk_tagId`
    FOREIGN KEY (`tag_id` )
    REFERENCES `gimmi`.`tag` (`tag_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tagMap_sourceData1`
    FOREIGN KEY (`site_id` )
    REFERENCES `gimmi`.`site` (`site_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'Stores the tag<->object relation';


-- -----------------------------------------------------
-- Table `gimmi`.`category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gimmi`.`category` ;

CREATE  TABLE IF NOT EXISTS `gimmi`.`category` (
  `category_id` INT NOT NULL AUTO_INCREMENT ,
  `parent_id` INT NULL ,
  `name_en` VARCHAR(200) NOT NULL ,
  `name_de` VARCHAR(200) NULL ,
  PRIMARY KEY (`category_id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'Stores category definitions';


-- -----------------------------------------------------
-- Table `gimmi`.`site_has_category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gimmi`.`site_has_category` ;

CREATE  TABLE IF NOT EXISTS `gimmi`.`site_has_category` (
  `site_has_category_id` INT NOT NULL AUTO_INCREMENT ,
  `category_id` INT NOT NULL ,
  `site_id` INT NOT NULL ,
  PRIMARY KEY (`site_has_category_id`) ,
  INDEX `fk_categoryId` (`category_id` ASC) ,
  INDEX `fk_resourceId` (`site_id` ASC) ,
  CONSTRAINT `fk_categoryId`
    FOREIGN KEY (`category_id` )
    REFERENCES `gimmi`.`category` (`category_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_resourceId`
    FOREIGN KEY (`site_id` )
    REFERENCES `gimmi`.`site` (`site_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'Stores mapping of categoray<->resourceData';



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
