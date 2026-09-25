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
-- Table structure for table `purchases`
--

DROP TABLE IF EXISTS `purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchases` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `purchase_date` date NOT NULL,
  `quantity` int NOT NULL,
  `reference_number` varchar(255) NOT NULL,
  `unit_price` decimal(38,2) NOT NULL,
  `base_id` bigint NOT NULL,
  `created_by` bigint NOT NULL,
  `equipment_type_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_purchase_base` (`base_id`),
  KEY `idx_purchase_equipment` (`equipment_type_id`),
  KEY `FKal92jfnl4o3ueqvq8t6ivfdml` (`created_by`),
  CONSTRAINT `FKal92jfnl4o3ueqvq8t6ivfdml` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKftubicnkdkjxj8dxbdfh15exn` FOREIGN KEY (`base_id`) REFERENCES `bases` (`id`),
  CONSTRAINT `FKl8wnapciq105wi29v1war8yst` FOREIGN KEY (`equipment_type_id`) REFERENCES `equipment_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchases`
--

LOCK TABLES `purchases` WRITE;
/*!40000 ALTER TABLE `purchases` DISABLE KEYS */;
INSERT INTO `purchases` VALUES (1,'2026-09-25 04:57:26.101355',NULL,'2026-09-13',50,'PO-1001',850.00,1,1,1),(2,'2026-09-25 07:05:15.992486','','2026-09-25',3,'QA-PO-20260925',100.00,1,1,1);
/*!40000 ALTER TABLE `purchases` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-25 13:03:29
