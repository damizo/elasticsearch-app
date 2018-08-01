Simple movie booking app with search engine

All docker images are from DockerHub. 
You have to login into your DockerHub account in order to download images.

1. cd /docker-conf
2. docker login (with your credentials)
3. docker-compose pull
4. docker-compose up -d

Creating volumes:  
docker volume create --name=es
docker volume create --name=es_test

AWS-CLI commands:
aws ecr create-repository --repository-name damian-repository/elastic (e.g)

Moving one image from repository to another repository:  
docker tag sourceRepository:tag targetRepository:tag  
docker push targetRepository:tag

Important:
- Spock tests will pass only if your elastir_search Docker images is up

TODO:
- More domains
- More REST endpoints
- This simple app supposed to stand on AWS, right now Docker containers for Elasticsearch and Kibana
are placed in ECS, but there is a problems with EC2 instances visibility.
