export PROJECT_ID=$(gcloud config list --format 'value(core.project)')
gcloud storage ls gs://$PROJECT_ID
gcloud storage cp -r gs://$PROJECT_ID/* ~/
chmod +x ~/guestbook-frontend/mvnw
chmod +x ~/guestbook-service/mvnw
cd ~/guestbook-service
cd ~/guestbook-frontend
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=cloud"
cd ~/guestbook-frontend
./mvnw spring-boot:run -Dspring.profiles.active=cloud
./mvnw spring-boot:run -Dspring.profiles.active=cloud
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=cloud"
cd ..
ls
git init
git config --global user.email "waddadelmehdi@gmail.com"
git config --global user.name "WADDAD ELMEHDI"
git init
