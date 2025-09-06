# This script is a complete rebuild of all images and containers
# Clean up any leftover images and containers from last run
docker rm patient-service
docker rm patient-service-db
docker rm image patient-service
docker rm image postgres

# Create internal network if it doesn't exist
docker network create internal

# Run database container
docker run -d --name patient-service-db --network internal -p 5000:5432 -e POSTGRES_USER=admin_user -e POSTGRES_PASSWORD=password -e POSTGRES_DB=db -v patient-service-db-data:/var/lib/postgresql/data postgres

# Build patient-service image based on the docker file
docker build -t patient-service ./patient-service

# Run patient-service container
docker run --name patient-service --network internal -p 4000:4000 -e SPRING_DATASOURCE_URL=jdbc:postgresql://patient-service-db:5432/db -e SPRING_DATASOURCE_USERNAME=admin_user -e SPRING_DATASOURCE_PASSWORD=password -e SPRING_JPA_HIBERNATE_DDL_AUTO=update -e SPRING_SQL_INIT_MODE=always patient-service
