CREATE DATABASE  IF NOT EXISTS `smart_tourism` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `smart_tourism`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: smart_tourism
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `historicalinfo`
--

DROP TABLE IF EXISTS `historicalinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historicalinfo` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `monument_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `historical_significance` text COLLATE utf8mb4_general_ci,
  `official_classification` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cultural_importance` text COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`),
  KEY `fk_hist_monument` (`monument_id`),
  CONSTRAINT `fk_hist_monument` FOREIGN KEY (`monument_id`) REFERENCES `monument` (`monument_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historicalinfo`
--

LOCK TABLES `historicalinfo` WRITE;
/*!40000 ALTER TABLE `historicalinfo` DISABLE KEYS */;
INSERT INTO `historicalinfo` VALUES (1,'m001','Musée riche en mosaïques.','Collection nationale importante.','Classement national','Patrimoine culturel national'),(2,'m002','Un des plus grands amphithéâtres romains du monde.','Lieu emblématique démontrant la grandeur architecturale romaine.','Patrimoine mondial UNESCO','Symbole historique majeur de la Tunisie'),(3,'m003','Centre historique riche en souks, mosquées et édifices culturels.','Foyer culturel majeur du Maghreb médiéval.','Patrimoine mondial UNESCO','Cœur spirituel et culturel du pays'),(4,'m004','Fort situé sur un promontoire offrant une vue stratégique sur la mer.','Fort militaire clé de la défense maritime ottomane.','Monument national classé','Site touristique majeur du Cap Bon'),(5,'m005','Ancienne cité numide extrêmement bien conservée.','Site majeur illustrant la fusion culturelle numido-romaine.','UNESCO Patrimoine mondial','Importance culturelle et archéologique exceptionnelle');
/*!40000 ALTER TABLE `historicalinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monthly_stats`
--

DROP TABLE IF EXISTS `monthly_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthly_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tourist_stats_id` int NOT NULL,
  `month_name` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `visitors` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_month_stats` (`tourist_stats_id`),
  CONSTRAINT `fk_month_stats` FOREIGN KEY (`tourist_stats_id`) REFERENCES `touriststats` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthly_stats`
--

LOCK TABLES `monthly_stats` WRITE;
/*!40000 ALTER TABLE `monthly_stats` DISABLE KEYS */;
INSERT INTO `monthly_stats` VALUES (1,1,'January',8000),(2,1,'February',9000),(3,1,'March',10000),(4,2,'January',12000),(5,2,'February',13500),(6,2,'March',15000),(7,2,'April',20000),(8,2,'May',25000),(9,3,'June',30000),(10,3,'July',45000),(11,3,'August',52000),(12,4,'April',15000),(13,4,'May',17000),(14,4,'June',21000),(15,4,'July',26000),(16,5,'September',7000),(17,5,'October',8500),(18,5,'November',6000);
/*!40000 ALTER TABLE `monthly_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monument`
--

DROP TABLE IF EXISTS `monument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monument` (
  `monument_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `city` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `year_built` smallint DEFAULT NULL,
  `architectural_style` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `unesco_heritage` tinyint(1) DEFAULT '0',
  `historical_period` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`monument_id`),
  KEY `idx_monument_city` (`city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monument`
--

LOCK TABLES `monument` WRITE;
/*!40000 ALTER TABLE `monument` DISABLE KEYS */;
INSERT INTO `monument` VALUES ('m001','Musée National du Bardo','Tunis',1888,'Mauresque',0,'Ottoman'),('m002','Amphithéâtre d’El Jem','El Jem',238,'Romain',1,'Empire Romain'),('m003','Médina de Tunis','Tunis',698,'Islamique',1,'Période Hafside'),('m004','Fort de Kélibia','Kélibia',1600,'Ottoman',0,'Empire Ottoman'),('m005','Dougga','Beja',-300,'Néopunique / Romain',1,'Période Numide et Romaine'),('m006','jerba','jerba',1800,'musee',0,'amazigh');
/*!40000 ALTER TABLE `monument` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restoration_history`
--

DROP TABLE IF EXISTS `restoration_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restoration_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `monument_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `seq` smallint DEFAULT '0',
  `note` text COLLATE utf8mb4_general_ci,
  `restoration_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_rest_monument` (`monument_id`),
  CONSTRAINT `fk_rest_monument` FOREIGN KEY (`monument_id`) REFERENCES `monument` (`monument_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restoration_history`
--

LOCK TABLES `restoration_history` WRITE;
/*!40000 ALTER TABLE `restoration_history` DISABLE KEYS */;
INSERT INTO `restoration_history` VALUES (1,'m001',1,'Rénovation du toit','2010-06-15'),(2,'m001',2,'Remise en état des salles principales','2018-09-01'),(3,'m002',1,'Consolidation des murs extérieurs','1990-04-12'),(4,'m002',2,'Stabilisation des gradins','2005-11-03'),(5,'m002',3,'Restauration de l’arène centrale','2019-05-20'),(6,'m003',1,'Restauration des portes anciennes','2011-08-10'),(7,'m003',2,'Réhabilitation des souks','2016-02-22'),(8,'m004',1,'Rénovation des remparts','2008-03-12'),(9,'m004',2,'Consolidation de la tour principale','2020-07-18'),(10,'m005',1,'Restauration du théâtre romain','1997-09-30');
/*!40000 ALTER TABLE `restoration_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `touriststats`
--

DROP TABLE IF EXISTS `touriststats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `touriststats` (
  `id` int NOT NULL AUTO_INCREMENT,
  `region` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
  `year` smallint NOT NULL,
  `total_visitors` int DEFAULT '0',
  `international_visitors` int DEFAULT '0',
  `growth_rate` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_region_year` (`region`,`year`),
  KEY `idx_tourist_region` (`region`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `touriststats`
--

LOCK TABLES `touriststats` WRITE;
/*!40000 ALTER TABLE `touriststats` DISABLE KEYS */;
INSERT INTO `touriststats` VALUES (1,'Tunis',2024,120000,30000,0.05),(2,'Mahdia',2023,250000,180000,0.12),(3,'Tunis',2023,400000,220000,0.08),(4,'Nabeul',2024,180000,60000,0.1),(5,'Beja',2022,90000,40000,0.06);
/*!40000 ALTER TABLE `touriststats` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-05 15:08:19
