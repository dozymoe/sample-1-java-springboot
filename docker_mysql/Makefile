ifndef UID
    UID = $(shell id -u)
endif
ifndef GID
    GID = $(shell id -g)
endif

PID := mysql
BACKEND := mysql

NAME = $(PID).demo
NETWORK = internal.demo

ifeq "$(BACKEND)" "mariadb"
	BIN = mariadb
	ADMINBIN = mariadb-admin
else
	BIN = mysql
	ADMINBIN = mysqladmin
endif

start: build
	docker start $(NAME)
	until [ "`docker inspect -f {{.State.Health.Status}} $(NAME)`" = "healthy" ]; do sleep 3; done

build: ../tmp/.$(PID)-build

stop:
	-docker stop -t60 $(NAME)

purge:
	-docker stop -t60 $(NAME)
	-docker rm $(NAME)
	-docker rmi $(NAME)
	-rm -rf ../var/$(PID)
	-rm ../tmp/.$(PID)-build

../tmp/.$(PID)-build: Dockerfile-$(BACKEND) ../var/$(PID)/.keep ./initdb.d/*
	docker build -t $(NAME) --file Dockerfile-$(BACKEND) ./
	-docker stop -t60 $(NAME)
	-docker rm $(NAME)
	docker create --network=$(NETWORK) -u $(UID):$(GID) \
		-v $(shell pwd)/../var/$(PID):/var/lib/mysql \
		--name=$(NAME) $(NAME)
	@touch ../tmp/.$(PID)-build

../var/$(PID)/.keep:
	[ -d ../var/$(PID) ] || mkdir -p ../var/$(PID)
	[ -f ../var/$(PID)/.keep ] || touch ../var/$(PID)/.keep

include Makefile-commands.in

.PHONY: run build start stop purge
