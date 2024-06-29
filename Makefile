PID := sample1-springboot

NAME = $(PID).demo
NETWORK = internal.demo

mysql: tmp/.network
	$(MAKE) $(MAKEARGS) -C docker_mysql $(cmd) $(args)

stop:
	-docker stop -t60 $(NAME)
	-$(MAKE) -C docker_mysql stop

purge:
	-docker stop -t60 $(NAME)
	-docker rm $(NAME)
	-rm tmp/.$(PID)-build
	-$(MAKE) -C docker_mysql purge
	-docker network rm $(NETWORK)
	-rm tmp/.network

tmp/.network:
	-@docker network create $(NETWORK)
	@touch tmp/.network


start: build mysql
	docker start $(NAME)
	until [ "`docker inspect -f {{.State.Health.Status}} $(NAME)`" = "healthy" ]; do sleep 3; done

build: tmp/.$(PID)-build

tmp/.$(PID)-build: tmp/.network
	$(MAKE) $(MAKEARGS) -C docker_java_node build
	-docker stop -t60 $(NAME)
	-docker rm $(NAME)
	docker create --network=$(NETWORK) \
		-v $(shell pwd):/home/user/work \
		-p 8080:8000 \
		--name=$(NAME) java-node.demo
	@touch tmp/.$(PID)-build


include Makefile-commands.in

.PHONY: mysql
.PHONY: stop purge
.PHONY: start build
