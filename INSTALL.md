# Installation
## Prerequisite 

- Docker CE
- Node JS 8+ 
- npm 3.5+ _should be installed with nodejs_
- yarn _(npm install -g yarn)_

Once you've cloned the repository, jump into the project directory.

## Dependencies
## UI

```
cd partners-front
yarn
```

## API
If you run the command “./dev-run.sh”, dependencies will be downloaded automatically)

## Launch the application
## UI

```
cd partners-front
npm run server:dev:hmr
```

(or inside root directory: `./dev-run.sh front`)

## API

`./dev-run.sh back` : it will fetch dependencies, and run the API

## MongoDB
We suggest you to run a MongoDB instance using Docker.

First time: `docker run -d --name mongo -p 27017:27017 mongo`
Then: `docker start mongo`

___________________________________

## Develop with Eclipse Che

We are using Eclipse Che, but you can use any other IDE.

## Che installation
You must have **Docker 17+** in order to run Che.
Simply run: `docker run -ti -v /var/run/docker.sock:/var/run/docker.sock -v ~/che-data:/data eclipse/che start`

NB: `~/che-data` can be any directory where you want to store Che data.

## Configuration
1. Create a workspace with the configuration from `wksp-config.json`
> It contains servers, services, git and projects configurations and Che commands

2. Clone this git repository.
3. Run command **add-git-aliases** if you want them

## Run
1. Run **front-install-deps**, then **back-build**
2. Then, you can run UI with **front-run**.
3. And the back with **back-run**.

## Upgrade
To upgrade **Che**, you can run this command:
` docker run -v /var/run/docker.sock:/var/run/docker.sock -v ~/che-data:/data eclipse/che:6.8.0 upgrade`

Where `6.8.0` is the wanted version.
