
sudo rm -rf /path/on/host/*
mkdir -p /path/on/host/tmp
mkdir -p /path/on/host/files
mkdir -p /path/on/host/surveys
mkdir -p /path/on/host/users
mkdir -p /path/on/host/archive
sudo chmod -R 777 /path/on/host
docker-compose down -v   

python redis/delete.py


