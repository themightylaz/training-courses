# mariadb makefile

# Environment Variables
IMAGE = myawn/mariadb-invdb-image
CONTAINER = inventory-database

.PHONY: up

prep :
	mkdir -p \
		data/backup

clean:
	docker rm -f $(CONTAINER) || true
	docker rmi -f $(IMAGE)|| true

image:
	docker rm -f $(CONTAINER) || true
	docker rmi -f $(IMAGE)|| true
	docker build -t $(IMAGE) .
	docker push $(IMAGE)

dump:
	mysqldump -u hzuser -phzpass inventoryDB > ./data/backup/inv_dump.sql

pull :
	docker-compose pull

up : prep pull
	docker-compose up -d

down :
	docker-compose down

shell :
	docker exec -ti $(CONTAINER) /bin/bash

tail :
	docker logs -f $(CONTAINER)

