ifndef UID
    UID = $(shell id -u)
endif
ifndef GID
    GID = $(shell id -g)
endif

PID := java-node

NAME = $(PID).demo
NETWORK = internal.demo

build: ../tmp/.$(PID)-build

stop:

purge:
	-docker rmi $(NAME)
	-rm ../tmp/.$(PID)-build
	-rm ../tmp/.$(PID)-build-lock

../tmp/.$(PID)-build: Dockerfile
	docker build -t $(NAME) --build-arg uid=$(UID) --build-arg gid=$(GID) \
		--file Dockerfile ./
	@touch ../tmp/.$(PID)-build

.PHONY: build stop purge
