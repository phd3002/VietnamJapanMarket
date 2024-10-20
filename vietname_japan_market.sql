-- MySQL dump 10.13  Distrib 8.0.40, for Linux (x86_64)
--
-- Host: localhost    Database: vietnam_japan_market
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `cart_id` int NOT NULL AUTO_INCREMENT,
  `total_price` decimal(38,2) NOT NULL,
  `user_id` int DEFAULT NULL,
  `total_amount` int NOT NULL,
  PRIMARY KEY (`cart_id`) USING BTREE,
  KEY `FKg5uhi8vpsuy0lgloxk2h4w5o6` (`user_id`) USING BTREE,
  CONSTRAINT `FKg5uhi8vpsuy0lgloxk2h4w5o6` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `cart_item_id` int NOT NULL AUTO_INCREMENT,
  `price` decimal(38,2) NOT NULL,
  `quantity` int NOT NULL,
  `cart_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `variation_id` int DEFAULT NULL,
  PRIMARY KEY (`cart_item_id`) USING BTREE,
  KEY `FK99e0am9jpriwxcm6is7xfedy3` (`cart_id`) USING BTREE,
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`) USING BTREE,
  KEY `FKlrockwdl0px4a0929dbghi6xi` (`variation_id`) USING BTREE,
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK99e0am9jpriwxcm6is7xfedy3` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`cart_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKlrockwdl0px4a0929dbghi6xi` FOREIGN KEY (`variation_id`) REFERENCES `product_variation` (`variation_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'/images/product/categories/fashion.png','Fashion'),(2,'/images/product/categories/beauty.png','Beauty'),(3,'/images/product/categories/electronic.png','Electronic'),(4,'/images/product/categories/limited-product.png','Limited Product'),(5,'/images/product/categories/second-hand.png','Second Hand'),(6,'/images/product/categories/toys.png','Toys');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `color`
--

DROP TABLE IF EXISTS `color`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `color` (
  `color_id` int NOT NULL AUTO_INCREMENT,
  `color_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`color_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `color`
--

LOCK TABLES `color` WRITE;
/*!40000 ALTER TABLE `color` DISABLE KEYS */;
INSERT INTO `color` VALUES (1,'#333333'),(2,'#ffffff'),(3,'#000000'),(4,'#abc123'),(5,'#fcd345');
/*!40000 ALTER TABLE `color` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `countries`
--

DROP TABLE IF EXISTS `countries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `countries` (
  `country_id` int NOT NULL AUTO_INCREMENT,
  `country_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`country_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `countries`
--

LOCK TABLES `countries` WRITE;
/*!40000 ALTER TABLE `countries` DISABLE KEYS */;
INSERT INTO `countries` VALUES (1,'United States'),(2,'Vietnam'),(3,'China'),(4,'France'),(5,'Germany'),(6,'Italy'),(7,'Thailand'),(8,'Japan');
/*!40000 ALTER TABLE `countries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `feedback_id` int NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `store_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `variation_id` int DEFAULT NULL,
  `feedback_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`feedback_id`) USING BTREE,
  KEY `FKppurdkst620e0cpc0e7himbiu` (`store_id`) USING BTREE,
  KEY `FKpwwmhguqianghvi1wohmtsm8l` (`user_id`) USING BTREE,
  KEY `FK2g7lgxa3divd1c3bb56otpv7f` (`variation_id`) USING BTREE,
  CONSTRAINT `FK2g7lgxa3divd1c3bb56otpv7f` FOREIGN KEY (`variation_id`) REFERENCES `product_variation` (`variation_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKppurdkst620e0cpc0e7himbiu` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKpwwmhguqianghvi1wohmtsm8l` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (1,'Weve created a full-stack structure for our working workflow processes, were from the funny the century initial all the made, have spare to negatives.','2024-10-13 15:21:36.000000',5,1,1,1,NULL),(2,'Weve created a full-stack structure for our working workflow processes, were from the funny the century initial all the made, have spare to negatives.','2024-10-13 15:22:13.000000',4,1,2,1,NULL),(3,'Weve created a full-stack structure for our working workflow processes, were from the funny the century initial all the made, have spare to negatives.','2024-10-13 15:22:32.000000',5,1,3,1,NULL),(4,'Weve created a full-stack structure for our working workflow processes, were from the funny the century initial all the made, have spare to negatives.','2024-10-13 15:22:40.000000',4,1,4,2,NULL),(5,'Weve created a full-stack structure for our working workflow processes, were from the funny the century initial all the made, have spare to negatives.','2024-10-13 15:22:52.000000',5,1,1,3,NULL),(6,'Weve created a full-stack structure for our working workflow processes, were from the funny the century initial all the made, have spare to negatives.','2024-10-13 15:23:03.000000',5,1,2,3,NULL),(7,'Weve created a full-stack structure for our working workflow processes, were from the funny the century initial all the made, have spare to negatives.','2024-10-13 15:23:12.000000',5,1,3,4,NULL);
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice` (
  `invoice_id` int NOT NULL AUTO_INCREMENT,
  `additional_fee` decimal(38,2) DEFAULT NULL,
  `deposit` decimal(38,2) DEFAULT NULL,
  `remaining_balance` decimal(38,2) DEFAULT NULL,
  `shipping_fee` decimal(38,2) DEFAULT NULL,
  `tax` decimal(38,2) DEFAULT NULL,
  `total_amount` decimal(38,2) NOT NULL,
  `order_id` int DEFAULT NULL,
  PRIMARY KEY (`invoice_id`) USING BTREE,
  KEY `FKthf5w8xuexpjinfl7xheakhqn` (`order_id`) USING BTREE,
  CONSTRAINT `FKthf5w8xuexpjinfl7xheakhqn` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
  `order_detail_id` int NOT NULL AUTO_INCREMENT,
  `price` decimal(38,2) NOT NULL,
  `quantity` int NOT NULL,
  `order_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `variation_id` int DEFAULT NULL,
  `order_order_id` int DEFAULT NULL,
  PRIMARY KEY (`order_detail_id`) USING BTREE,
  KEY `FKjyu2qbqt8gnvno9oe9j2s2ldk` (`order_id`) USING BTREE,
  KEY `FK4q98utpd73imf4yhttm3w0eax` (`product_id`) USING BTREE,
  KEY `FK4gyw6w1v0k1k498q6s7y7ihoh` (`variation_id`) USING BTREE,
  KEY `FKn5s774je1pjt7y8upyvbnq7ww` (`order_order_id`) USING BTREE,
  CONSTRAINT `FK4gyw6w1v0k1k498q6s7y7ihoh` FOREIGN KEY (`variation_id`) REFERENCES `product_variation` (`variation_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK4q98utpd73imf4yhttm3w0eax` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKjyu2qbqt8gnvno9oe9j2s2ldk` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKn5s774je1pjt7y8upyvbnq7ww` FOREIGN KEY (`order_order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `additional_fee` decimal(38,2) DEFAULT NULL,
  `commission_fee` decimal(38,2) DEFAULT NULL,
  `deposit` decimal(38,2) DEFAULT NULL,
  `order_date` datetime(6) NOT NULL,
  `phone_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `remaining_balance` decimal(38,2) DEFAULT NULL,
  `shipping_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `total_price` decimal(38,2) NOT NULL,
  `country_id` int DEFAULT NULL,
  `rate_id` int DEFAULT NULL,
  `unit_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE,
  KEY `FKkq1n38yo89vssmny2cvr4cpu3` (`country_id`) USING BTREE,
  KEY `FK8nsrtknc2vmaj9iydbocos2oj` (`rate_id`) USING BTREE,
  KEY `FKro3xr3oweat82e0uvk3189x3t` (`unit_id`) USING BTREE,
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`) USING BTREE,
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK8nsrtknc2vmaj9iydbocos2oj` FOREIGN KEY (`rate_id`) REFERENCES `shipping_rate` (`rate_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKkq1n38yo89vssmny2cvr4cpu3` FOREIGN KEY (`country_id`) REFERENCES `countries` (`country_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKro3xr3oweat82e0uvk3189x3t` FOREIGN KEY (`unit_id`) REFERENCES `shipping_unit` (`unit_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `payment_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `payment_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `order_id` int DEFAULT NULL,
  PRIMARY KEY (`payment_id`) USING BTREE,
  KEY `FKlouu98csyullos9k25tbpk4va` (`order_id`) USING BTREE,
  CONSTRAINT `FKlouu98csyullos9k25tbpk4va` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_image`
--

DROP TABLE IF EXISTS `product_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_image` (
  `image_id` int NOT NULL AUTO_INCREMENT,
  `image_1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `image_2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `image_3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `image_4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `image_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `thumbnail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  PRIMARY KEY (`image_id`) USING BTREE,
  KEY `FK1n91c4vdhw8pa4frngs4qbbvs` (`product_id`) USING BTREE,
  CONSTRAINT `FK1n91c4vdhw8pa4frngs4qbbvs` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_image`
--

LOCK TABLES `product_image` WRITE;
/*!40000 ALTER TABLE `product_image` DISABLE KEYS */;
INSERT INTO `product_image` VALUES (1,'https://yavuzceliker.github.io/sample-images/image-1.jpg','https://yavuzceliker.github.io/sample-images/image-7.jpg','https://yavuzceliker.github.io/sample-images/image-13.jpg','https://yavuzceliker.github.io/sample-images/image-19.jpg','Image 1','https://yavuzceliker.github.io/sample-images/image-1.jpg',1),(2,'https://yavuzceliker.github.io/sample-images/image-2.jpg','https://yavuzceliker.github.io/sample-images/image-8.jpg','https://yavuzceliker.github.io/sample-images/image-14.jpg','https://yavuzceliker.github.io/sample-images/image-20.jpg','Image 2','https://yavuzceliker.github.io/sample-images/image-2.jpg',2),(3,'https://yavuzceliker.github.io/sample-images/image-3.jpg','https://yavuzceliker.github.io/sample-images/image-9.jpg','https://yavuzceliker.github.io/sample-images/image-15.jpg','https://yavuzceliker.github.io/sample-images/image-21.jpg','Image 3','https://yavuzceliker.github.io/sample-images/image-3.jpg',3),(4,'https://yavuzceliker.github.io/sample-images/image-4.jpg','https://yavuzceliker.github.io/sample-images/image-10.jpg','https://yavuzceliker.github.io/sample-images/image-16.jpg','https://yavuzceliker.github.io/sample-images/image-22.jpg','Image 4','https://yavuzceliker.github.io/sample-images/image-4.jpg',4),(5,'https://yavuzceliker.github.io/sample-images/image-5.jpg','https://yavuzceliker.github.io/sample-images/image-11.jpg','https://yavuzceliker.github.io/sample-images/image-17.jpg','https://yavuzceliker.github.io/sample-images/image-23.jpg','Image 5','https://yavuzceliker.github.io/sample-images/image-5.jpg',5),(6,'https://yavuzceliker.github.io/sample-images/image-6.jpg','https://yavuzceliker.github.io/sample-images/image-12.jpg','https://yavuzceliker.github.io/sample-images/image-18.jpg','https://yavuzceliker.github.io/sample-images/image-24.jpg','Image 6','https://yavuzceliker.github.io/sample-images/image-6.jpg',6);
/*!40000 ALTER TABLE `product_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_variation`
--

DROP TABLE IF EXISTS `product_variation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_variation` (
  `variation_id` int NOT NULL AUTO_INCREMENT,
  `stock` int NOT NULL,
  `color_id` int DEFAULT NULL,
  `image_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `size_id` int DEFAULT NULL,
  PRIMARY KEY (`variation_id`) USING BTREE,
  KEY `FKrudkpqdqm7smtrgqr9cqfgwqk` (`color_id`) USING BTREE,
  KEY `FK19k1hj3r58wp3mu8us0456k1t` (`image_id`) USING BTREE,
  KEY `FKtbptn3lovfbklj7frm8gn8uvy` (`product_id`) USING BTREE,
  KEY `FK8d998inm34570itv3ak1x7x7h` (`size_id`) USING BTREE,
  CONSTRAINT `FK19k1hj3r58wp3mu8us0456k1t` FOREIGN KEY (`image_id`) REFERENCES `product_image` (`image_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK8d998inm34570itv3ak1x7x7h` FOREIGN KEY (`size_id`) REFERENCES `size` (`size_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKrudkpqdqm7smtrgqr9cqfgwqk` FOREIGN KEY (`color_id`) REFERENCES `color` (`color_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKtbptn3lovfbklj7frm8gn8uvy` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_variation`
--

LOCK TABLES `product_variation` WRITE;
/*!40000 ALTER TABLE `product_variation` DISABLE KEYS */;
INSERT INTO `product_variation` VALUES (1,100,1,1,1,1),(2,150,2,1,1,2),(3,40,4,2,2,1),(4,50,1,3,3,1),(5,46,5,4,4,2),(6,50,4,4,4,3),(7,100,3,4,4,4),(8,300,1,5,5,2),(9,300,2,6,6,2),(10,160,3,6,6,3);
/*!40000 ALTER TABLE `product_variation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `product_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `product_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `weight` float DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  `store_id` int DEFAULT NULL,
  PRIMARY KEY (`product_id`) USING BTREE,
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`) USING BTREE,
  KEY `FKgcyffheofvmy2x5l78xam63mc` (`store_id`) USING BTREE,
  CONSTRAINT `FKgcyffheofvmy2x5l78xam63mc` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'2024-10-06 16:35:18.000000',1200.00,'A laptop is a portable personal computer designed for mobility and convenience, combining a screen, keyboard, and trackpad into a single compact device.','Media Remote','',12,1,1),(2,'2024-10-06 16:40:55.000000',9000.00,'A laptop is a portable personal computer designed for mobility and convenience, combining a screen, keyboard, and trackpad into a single compact device.','Creative Craftsmen','',9,2,1),(3,'2024-10-06 16:41:25.000000',5500.00,'A laptop is a portable personal computer designed for mobility and convenience, combining a screen, keyboard, and trackpad into a single compact device.','Invention Shoppe','',5.5,3,1),(4,'2024-10-06 16:41:51.000000',120.00,'A laptop is a portable personal computer designed for mobility and convenience, combining a screen, keyboard, and trackpad into a single compact device.','Gizmo Glamour','',1.2,4,1),(5,'2024-10-06 16:42:53.000000',3200.00,'A laptop is a portable personal computer designed for mobility and convenience, combining a screen, keyboard, and trackpad into a single compact device.','Tech Toys\r\n','',3.2,5,1),(6,'2024-10-06 16:42:59.000000',6000.00,'A laptop is a portable personal computer designed for mobility and convenience, combining a screen, keyboard, and trackpad into a single compact device.','Gadget Gala','',6,6,1);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE KEY `UK716hgxp60ym1lifrdgp67xt5k` (`role_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'admin'),(1,'user');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_rate`
--

DROP TABLE IF EXISTS `shipping_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_rate` (
  `rate_id` int NOT NULL AUTO_INCREMENT,
  `rate` decimal(38,2) NOT NULL,
  `weight` float NOT NULL,
  `country_from` int DEFAULT NULL,
  `country_to` int DEFAULT NULL,
  `unit_id` int DEFAULT NULL,
  PRIMARY KEY (`rate_id`) USING BTREE,
  KEY `FK2npi3rdebrqiwxd23resf10a4` (`country_from`) USING BTREE,
  KEY `FKe0hgd996fvvpy6onatql9gs1j` (`country_to`) USING BTREE,
  KEY `FKh25o7jlyxk6lwng10gj0sfcao` (`unit_id`) USING BTREE,
  CONSTRAINT `FK2npi3rdebrqiwxd23resf10a4` FOREIGN KEY (`country_from`) REFERENCES `countries` (`country_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKe0hgd996fvvpy6onatql9gs1j` FOREIGN KEY (`country_to`) REFERENCES `countries` (`country_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKh25o7jlyxk6lwng10gj0sfcao` FOREIGN KEY (`unit_id`) REFERENCES `shipping_unit` (`unit_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_rate`
--

LOCK TABLES `shipping_rate` WRITE;
/*!40000 ALTER TABLE `shipping_rate` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipping_rate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_status`
--

DROP TABLE IF EXISTS `shipping_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_status` (
  `status_id` int NOT NULL AUTO_INCREMENT,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `tracking_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `order_id` int DEFAULT NULL,
  PRIMARY KEY (`status_id`) USING BTREE,
  KEY `FKndsq9aluyihiqix183o2lka4p` (`order_id`) USING BTREE,
  CONSTRAINT `FKndsq9aluyihiqix183o2lka4p` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_status`
--

LOCK TABLES `shipping_status` WRITE;
/*!40000 ALTER TABLE `shipping_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipping_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_unit`
--

DROP TABLE IF EXISTS `shipping_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_unit` (
  `unit_id` int NOT NULL AUTO_INCREMENT,
  `shipping_revenue` decimal(38,2) NOT NULL,
  `unit_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `unit_contact` int DEFAULT NULL,
  `unit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `unit_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`unit_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_unit`
--

LOCK TABLES `shipping_unit` WRITE;
/*!40000 ALTER TABLE `shipping_unit` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipping_unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `size`
--

DROP TABLE IF EXISTS `size`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `size` (
  `size_id` int NOT NULL AUTO_INCREMENT,
  `size_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`size_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `size`
--

LOCK TABLES `size` WRITE;
/*!40000 ALTER TABLE `size` DISABLE KEYS */;
INSERT INTO `size` VALUES (1,'S'),(2,'M'),(3,'L'),(4,'XL'),(5,'XXL'),(6,'3XL');
/*!40000 ALTER TABLE `size` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stores`
--

DROP TABLE IF EXISTS `stores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stores` (
  `store_id` int NOT NULL AUTO_INCREMENT,
  `store_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `store_revenue` decimal(38,2) NOT NULL,
  `country_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `picture_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`store_id`) USING BTREE,
  UNIQUE KEY `UKasa48v37wxfgmxe61dxlmmnwl` (`country_id`) USING BTREE,
  UNIQUE KEY `UKbf5j4cttpf2njs5cpc1ilq1p4` (`user_id`) USING BTREE,
  CONSTRAINT `FKiw281hibigo41ijsvot42osjj` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKjw9r043v4o1cjdcpkqptqff4f` FOREIGN KEY (`country_id`) REFERENCES `countries` (`country_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stores`
--

LOCK TABLES `stores` WRITE;
/*!40000 ALTER TABLE `stores` DISABLE KEYS */;
INSERT INTO `stores` VALUES (1,'Welcome to Media Store, our goal is to provide a seamless shopping experience with excellent customer service, fast shipping, and hassle-free returns.','5S Fashion',1250.00,1,1,'https://down-cvs-vn.img.susercontent.com/vn-11134216-7r98o-lxawpjpbortn95_tn.webp');
/*!40000 ALTER TABLE `stores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tokens`
--

DROP TABLE IF EXISTS `tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) DEFAULT NULL,
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK2dylsfo39lgjyqml2tbe0b0ss` (`user_id`) USING BTREE,
  CONSTRAINT `FK2dylsfo39lgjyqml2tbe0b0ss` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tokens`
--

LOCK TABLES `tokens` WRITE;
/*!40000 ALTER TABLE `tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_refund` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `transaction_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `from_wallet_id` int DEFAULT NULL,
  `to_wallet_id` int DEFAULT NULL,
  PRIMARY KEY (`transaction_id`) USING BTREE,
  KEY `FKojo174msod6aw3q15yn7xxmhw` (`from_wallet_id`) USING BTREE,
  KEY `FKmlkuokek2wcmfh0crfbefw0kt` (`to_wallet_id`) USING BTREE,
  CONSTRAINT `FKmlkuokek2wcmfh0crfbefw0kt` FOREIGN KEY (`to_wallet_id`) REFERENCES `wallet` (`wallet_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKojo174msod6aw3q15yn7xxmhw` FOREIGN KEY (`from_wallet_id`) REFERENCES `wallet` (`wallet_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_activity`
--

DROP TABLE IF EXISTS `user_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_activity` (
  `activity_id` int NOT NULL AUTO_INCREMENT,
  `viewed_at` datetime(6) DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`activity_id`) USING BTREE,
  KEY `FKfr9nire0phsk0yp8wm5penp9k` (`product_id`) USING BTREE,
  KEY `FKs41is1raa3f0y5q5g0pw2rfd4` (`user_id`) USING BTREE,
  CONSTRAINT `FKfr9nire0phsk0yp8wm5penp9k` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKs41is1raa3f0y5q5g0pw2rfd4` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_activity`
--

LOCK TABLES `user_activity` WRITE;
/*!40000 ALTER TABLE `user_activity` DISABLE KEYS */;
INSERT INTO `user_activity` VALUES (1,'2024-10-13 15:01:45.000000',1,1),(2,'2024-10-13 15:18:13.000000',1,2),(3,'2024-10-13 15:18:19.000000',1,3),(4,'2024-10-13 15:18:30.000000',2,1),(5,'2024-10-13 15:18:40.000000',3,1),(6,'2024-10-13 15:18:50.000000',4,1),(7,'2024-10-13 15:18:58.000000',5,1),(8,'2024-10-13 15:19:07.000000',6,1),(9,'2024-10-13 15:19:17.000000',6,2);
/*!40000 ALTER TABLE `user_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `role_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`role_id`,`user_id`) USING BTREE,
  KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`user_id`) USING BTREE,
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,1),(1,2),(1,3),(1,4);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `first_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `last_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`) USING BTREE,
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`) USING BTREE,
  KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`) USING BTREE,
  CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2024-10-06 16:38:22.000000','user1@gmail.com','Test 2','User 366','$2a$10$4pSTwdBgzQxfcxMhbi2dKurZ50Hmcr2QPFoAGPf5wMQ7sC/QEjmpa','123456789','enabled','user1',1),(2,'2024-10-13 14:58:49.000000','user2@gmail.com','Test','User 2','$2a$10$QrZqJ/AVYIOOdq4PFTzBbeBshqbKXvPvJ.9otqoCep.uJJFQh2f0O','0123456789','enabled','user2',1),(3,'2024-10-13 14:59:44.000000','user3@gmail.com','Test','User 3','$2a$10$QrZqJ/AVYIOOdq4PFTzBbeBshqbKXvPvJ.9otqoCep.uJJFQh2f0O','0123456789','enabled','user3',1),(4,'2024-10-13 15:00:11.000000','user4@gmail.com','Test','User 4','$2a$10$QrZqJ/AVYIOOdq4PFTzBbeBshqbKXvPvJ.9otqoCep.uJJFQh2f0O','0123456789','enabled','user4',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet`
--

DROP TABLE IF EXISTS `wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet` (
  `wallet_id` int NOT NULL AUTO_INCREMENT,
  `balance` decimal(38,2) NOT NULL,
  `last_updated` datetime(6) DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`wallet_id`) USING BTREE,
  KEY `FKgbusavqq0bdaodex4ee6v0811` (`user_id`) USING BTREE,
  CONSTRAINT `FKgbusavqq0bdaodex4ee6v0811` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet`
--

LOCK TABLES `wallet` WRITE;
/*!40000 ALTER TABLE `wallet` DISABLE KEYS */;
/*!40000 ALTER TABLE `wallet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlist`
--

DROP TABLE IF EXISTS `wishlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlist` (
  `wishlist_id` int NOT NULL AUTO_INCREMENT,
  `added_at` datetime(6) DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `variation_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`wishlist_id`),
  KEY `FK6p7qhvy1bfkri13u29x6pu8au` (`product_id`),
  KEY `FKjlww0utwoqmni8y1mytb5gcm4` (`variation_id`),
  KEY `FKtrd6335blsefl2gxpb8lr0gr7` (`user_id`),
  CONSTRAINT `FK6p7qhvy1bfkri13u29x6pu8au` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `FKjlww0utwoqmni8y1mytb5gcm4` FOREIGN KEY (`variation_id`) REFERENCES `product_variation` (`variation_id`),
  CONSTRAINT `FKtrd6335blsefl2gxpb8lr0gr7` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist`
--

LOCK TABLES `wishlist` WRITE;
/*!40000 ALTER TABLE `wishlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `wishlist` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-20  8:33:49
