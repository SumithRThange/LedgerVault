# Oracle Cloud Always Free Deployment (LedgerVault Backend)

This deploys backend + MySQL using Docker Compose on one Oracle VM.

## 1) Create OCI Always Free VM
- Shape: Ampere A1 (or any Always Free shape)
- OS: Ubuntu 22.04
- Allow inbound ports in Security List and NSG:
  - 22 (SSH)
  - 8080 (Backend API)

## 2) SSH into VM
```bash
ssh ubuntu@<PUBLIC_IP>
```

## 3) Install Docker + Compose plugin
```bash
sudo apt update
sudo apt install -y docker.io docker-compose-v2 git
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER
newgrp docker
```

## 4) Clone project and deploy
```bash
git clone https://github.com/SumithRThange/LedgerVault.git
cd LedgerVault/deploy/oci
docker compose up -d --build
```

## 5) Verify
```bash
docker ps
curl http://localhost:8080/users
```

From your laptop/mobile use:
```text
http://<PUBLIC_IP>:8080/
```

## 6) Android base URL
In Retrofit set:
```java
private static final String BASE_URL = "http://<PUBLIC_IP>:8080/";
```

## Notes
- Current MySQL root password in compose is `root` (change for production).
- Data persists in Docker volume `mysql_data`.
