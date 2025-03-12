#!/bin/bash

echo "



 ▗▄▄▖▗▄▄▄▖▗▄▄▖ ▗▖ ▗▖ ▗▄▄▖▗▄▄▄▖▗▄▄▖▗▖ ▗▖▗▄▄▖ ▗▄▄▄▖     ▗▄▖ ▗▄▄▖ ▗▄▄▖     ▗▄▄▖ ▗▖ ▗▖▗▄▄▄▖▗▖   ▗▄▄▄ ▗▄▄▄▖▗▄▄▖ 
▐▌     █  ▐▌ ▐▌▐▌ ▐▌▐▌     █ ▐▌   ▐▌ ▐▌▐▌ ▐▌▐▌       ▐▌ ▐▌▐▌ ▐▌▐▌ ▐▌    ▐▌ ▐▌▐▌ ▐▌  █  ▐▌   ▐▌  █▐▌   ▐▌ ▐▌
 ▝▀▚▖  █  ▐▛▀▚▖▐▌ ▐▌▐▌     █  ▝▀▚▖▐▌ ▐▌▐▛▀▚▖▐▛▀▀▘    ▐▛▀▜▌▐▛▀▘ ▐▛▀▘     ▐▛▀▚▖▐▌ ▐▌  █  ▐▌   ▐▌  █▐▛▀▀▘▐▛▀▚▖
▗▄▄▞▘  █  ▐▌ ▐▌▝▚▄▞▘▝▚▄▄▖  █ ▗▄▄▞▘▝▚▄▞▘▐▌ ▐▌▐▙▄▄▖    ▐▌ ▐▌▐▌   ▐▌       ▐▙▄▞▘▝▚▄▞▘▗▄█▄▖▐▙▄▄▖▐▙▄▄▀▐▙▄▄▖▐▌ ▐▌
                                                                                                           
                                                                                                           
                                                                                                    
Version 1.0


"


handle_error() {
    echo "[ERROR] $1"
    exit 1
}

FRONTEND_DIR="./Frontend"
BACKEND_DIR="./Backend"
DOCKER_IMAGE_NAME="ghcr.io/structsure-lastproject/structsure/structsure:stable"


echo "Vérification si Docker est installé..."

if ! command -v docker &> /dev/null; then
    handle_error "Docker n'est pas installé. Veuillez installer Docker pour continuer."
fi

echo "Vérification si le service Docker fonctionne..."
if ! systemctl is-active --quiet docker; then
    handle_error "Le service Docker n'est pas en cours d'exécution. Veuillez démarrer le service Docker."
fi


# Check if frontend directory exists
if [ ! -d "$FRONTEND_DIR" ]; then
    handle_error "Le répertoire Frontend n'existe pas : $FRONTEND_DIR"
fi

# Aboulute frontend path
FRONTEND_ABS_PATH=$(realpath "$FRONTEND_DIR")
echo "Chemin absolu du frontend : $FRONTEND_ABS_PATH"


# Check if backend directory exists
if [ ! -d "$BACKEND_DIR" ]; then
    handle_error "Le répertoire Backend n'existe pas : $BACKEND_DIR"
fi

# Aboulute backend path
BACKEND_ABS_PATH=$(realpath "$BACKEND_DIR")
echo "Chemin absolu du backend : $BACKEND_ABS_PATH"


# Build the frontend (SolidJS) in a Docker container
echo -e "\e[1;34mBuild du frontend...\e[0m"
docker run --rm --security-opt apparmor=unconfined \
    -v "$FRONTEND_ABS_PATH:/app" \
    -w /app \
    node:lts \
    sh -c "npm install && npm run build" || handle_error "Échec du build frontend avec Docker."



echo "Déplacement des fichiers build du frontend vers le dossier 'static' du backend..."
if [ ! -d "$FRONTEND_ABS_PATH/dist" ]; then
    handle_error "Le dossier du build du frontend n'existe pas : $FRONTEND_ABS_PATH/dist"
fi

sudo rm -rf "$BACKEND_ABS_PATH/src/main/resources/static/assets"
mkdir -p "$BACKEND_ABS_PATH/src/main/resources/static"  # Ensure static folder exists
sudo mv "$FRONTEND_ABS_PATH/dist"/* "$BACKEND_ABS_PATH/src/main/resources/static/" || handle_error "Échec du déplacement des fichiers frontend vers le backend."



# Build the backend with Maven in a Docker container
echo -e "\e[1;32mBuild du backend avec Maven...\e[0m"
docker run --rm --security-opt apparmor=unconfined \
    -v "$BACKEND_ABS_PATH:/app" \
    -w /app \
    maven:3.9\
    mvn clean package || handle_error "Échec du build backend avec Maven."


if [ ! -f "$BACKEND_ABS_PATH/target/structsure-0.0.1-SNAPSHOT.jar" ]; then
    handle_error "Le fichier jar n'existe pas : $BACKEND_ABS_PATH/target/structsure-0.0.1-SNAPSHOT.jar"
fi


# Build the Docker image using the Dockerfile in the backend directory
echo "Construction de l'image Docker..."
docker build -t "$DOCKER_IMAGE_NAME" -f "$BACKEND_ABS_PATH/Dockerfile" . || handle_error "Échec de la construction de l'image Docker."



echo -e "\e[1;32mLe processus de construction a été complété avec succès !\e[0m"
echo -e "\e[1;32mVérifiez l'image docker avec la commande : docker images\e[0m"
