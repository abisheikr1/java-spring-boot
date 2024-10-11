#instll latest jar
mvn clean install
# Login to GitLab
docker login registry.gitlab.com
# docker login registry.gitlab.com -u abisheik.professional -p *********
# Build your java image
docker build -t "registry.gitlab.com/finance8645134/billing_software_be:latest" .
# Tag your java image
docker tag registry.gitlab.com/finance8645134/billing_software_be registry.gitlab.com/finance8645134/billing_software_be:latest
# Push your image to gitlab
docker push registry.gitlab.com/finance8645134/billing_software_be       