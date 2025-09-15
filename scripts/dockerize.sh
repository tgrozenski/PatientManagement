# IMPORTANT container order is billing-service -> patient-service-db -> patient-service
# This script is a complete rebuild of all images and containers
# Clean up any leftover images and containers from last run
#docker rm patient-service
#docker rm patient-service-db
#docker rm image patient-service
#docker rm image postgres

# Create internal network if it doesn't exist
docker network create internal

# run Kafka detached
docker run -d --name kafka --network internal -p 9092:9092 -p 9094:9094 -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094 -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093 -e KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094 -e KAFKA_CFG_NODE_ID=0 -e KAFKA_CFG_PROCESS_ROLES=controller,broker bitnami/kafka

# Run database container
docker run -d --name patient-service-db --network internal -p 5000:5432 -e POSTGRES_USER=admin_user -e POSTGRES_PASSWORD=password -e POSTGRES_DB=db -v patient-service-db-data:/var/lib/postgresql/data postgres

# Build patient-service image based on the docker file
# docker build -t patient-service ./patient-service

# Run billing service container
docker run -d --name billing-service --network internal -p 4001:4001 -p 9001:9001 billing-service

# Run patient-service container
docker run -d --name patient-service --network internal -p 4000:4000 -e BILLING_SERVICE_ADDRESS=billing-service -e BILLING_SERVICE_GRPC_PORT=9001 -e SPRING_DATASOURCE_URL=jdbc:postgresql://patient-service-db:5432/db -e SPRING_DATASOURCE_USERNAME=admin_user -e SPRING_DATASOURCE_PASSWORD=password -e SPRING_JPA_HIBERNATE_DDL_AUTO=update -e SPRING_SQL_INIT_MODE=always -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 patient-service

# Build billing image based on docker file
# docker build -t billing-service ./billing-service

