<h1 align="center">Scalable Database Systems (Spring Boot + MongoDB)</h1>

<p align="center">
  A backend project that focuses on improving database performance and handling scale, based on how real-world systems actually work.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Backend-SpringBoot-green" />
  <img src="https://img.shields.io/badge/Database-MongoDB-brightgreen" />
  <img src="https://img.shields.io/badge/Caching-Redis-red" />
  <img src="https://img.shields.io/badge/Messaging-Kafka-orange" />
  <img src="https://img.shields.io/badge/Architecture-Scalable%20Systems-blue" />
</p>

This project demonstrates how modern backend systems handle **high traffic, large data, and performance bottlenecks** using practical database optimization and scaling strategies.

It focuses on how requests flow through the system and how each layer is optimized for performance and scalability.

## Architecture

<img width="700" height="500" alt="image" src="https://github.com/user-attachments/assets/58a4cdb7-b3ef-41b4-8fef-d4bebee0c100" />



### System Flow

- Client request → Query optimization → Cache  
- Cache miss → Primary Database  
- Primary DB → Replication → Read Replicas  
- Scaling via Sharding / Partitioning  
- Async tasks → Message Queue → Workers  
- Monitoring via logs and alerts  

## Tech Stack

<table>
<tr>
<td>

<b>Backend</b><br/>
<img src="https://www.vectorlogo.zone/logos/java/java-icon.svg" width="28"/> Java &nbsp;
<img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" width="28"/> Spring Boot

</td>

<td>

<b>Database</b><br/>
<img src="https://www.vectorlogo.zone/logos/mongodb/mongodb-icon.svg" width="28"/> MongoDB &nbsp;
<img src="https://www.vectorlogo.zone/logos/redis/redis-icon.svg" width="28"/> Redis

</td>

<td>

<b>Messaging</b><br/>
<img src="https://www.vectorlogo.zone/logos/apache_kafka/apache_kafka-icon.svg" width="28"/> Kafka

</td>
</tr>
</table>

## Design Highlights

- Optimized query execution using proper indexing strategies  
- Reduced database load using caching and connection pooling  
- Scalable read performance using replicas  
- Horizontal scaling using sharding techniques  
- Decoupled system using async processing  
- Observability built with logging and monitoring  


## Why This Project

This project is built to understand:

- How databases behave under high load  
- How real systems scale beyond a single database  
- How to design backend systems for performance and reliability  
