-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: military_asset_management
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
-- Table structure for table `assets`
--

DROP TABLE IF EXISTS `assets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `asset_code` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `quantity` int NOT NULL,
  `status` enum('ACTIVE','DAMAGED','INACTIVE','MISSING') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `base_id` bigint NOT NULL,
  `equipment_type_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKh3rqbypxh7aycu4jdf3sisunv` (`asset_code`),
  KEY `idx_asset_base` (`base_id`),
  KEY `idx_asset_equipment` (`equipment_type_id`),
  CONSTRAINT `FKbuwxqhhhc9vfb8uxj18w9hb7o` FOREIGN KEY (`base_id`) REFERENCES `bases` (`id`),
  CONSTRAINT `FKomwycbyk795i1rh24lvms6s8h` FOREIGN KEY (`equipment_type_id`) REFERENCES `equipment_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assets`
--

LOCK TABLES `assets` WRITE;
/*!40000 ALTER TABLE `assets` DISABLE KEYS */;
INSERT INTO `assets` VALUES (1,'AS-1001','2026-09-25 04:57:26.084063',120,'ACTIVE','2026-09-25 07:06:34.568310',1,1),(2,'AS-2001','2026-09-25 04:57:26.093597',80,'ACTIVE','2026-09-25 04:57:26.093597',2,2),(3,'AK-001','2026-09-25 06:28:10.429703',22,'DAMAGED','2026-09-25 06:28:10.429703',1,2),(4,'QA-20260925','2026-09-25 07:04:58.008468',9,'MISSING','2026-09-25 07:05:04.323780',1,2),(5,'TR-QA-TR-20260925','2026-09-25 07:06:30.051125',0,'ACTIVE','2026-09-25 07:06:34.568310',2,1);
/*!40000 ALTER TABLE `assets` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-25 13:03:31
