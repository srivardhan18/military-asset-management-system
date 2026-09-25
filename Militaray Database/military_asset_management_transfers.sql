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
-- Table structure for table `transfers`
--

DROP TABLE IF EXISTS `transfers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transfers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `quantity` int NOT NULL,
  `reference_number` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `transfer_date` date NOT NULL,
  `created_by` bigint NOT NULL,
  `equipment_type_id` bigint NOT NULL,
  `from_base_id` bigint NOT NULL,
  `to_base_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_transfer_from` (`from_base_id`),
  KEY `idx_transfer_to` (`to_base_id`),
  KEY `idx_transfer_equipment` (`equipment_type_id`),
  KEY `FK72maf1ivw09l7tb60v664tvqc` (`created_by`),
  CONSTRAINT `FK72maf1ivw09l7tb60v664tvqc` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKbygujo0me1kwidph3r3wi6ppe` FOREIGN KEY (`to_base_id`) REFERENCES `bases` (`id`),
  CONSTRAINT `FKcyr8v4236ksy6j4hu9hxvsm86` FOREIGN KEY (`equipment_type_id`) REFERENCES `equipment_types` (`id`),
  CONSTRAINT `FKey2fnrjf4tthac9vmjv4p37t4` FOREIGN KEY (`from_base_id`) REFERENCES `bases` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfers`
--

LOCK TABLES `transfers` WRITE;
/*!40000 ALTER TABLE `transfers` DISABLE KEYS */;
INSERT INTO `transfers` VALUES (1,'2026-09-25 04:57:26.113151',NULL,12,'TR-2001','COMPLETED','2026-09-20',1,1,1,2),(2,'2026-09-25 07:06:30.054871',NULL,1,'QA-TR-20260925','CANCELLED','2026-09-25',1,1,1,2);
/*!40000 ALTER TABLE `transfers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-25 13:03:30
