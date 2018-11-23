# EcoViz
[![Build Status](https://travis-ci.org/lmorel3/ecoviz.svg?branch=master)](https://travis-ci.org/lmorel3/ecoviz)

## Team Set Up

### 1. Developement IDE

- Back : Eclipse (Che maybe ?)
- Front : VSCode

### 2. Git Flow step

#### Overview

Initialy, we have the following branches :
- develop: development of the current sprint.
- master: stable version.
- release: where we merge the finished sprint.

#### New ticket

- New branch from develop named "features/[id du ticket]".
- Dev, test, code review etc. (according to the Definition of Done).
- Merge develop branch in your current branch.
- Merge/Pull request on develop branch.

#### Find bug on release branch

- New branch from release named "hotfix".
- Dev, test, code review etc. (according to the Definition of Done).
- Merge/Pull request on release.

### 3. Definition of Done

- Implemented.
- Tested (unit, integration ?).
- Documented (Javadoc).
- Review and validated.

### 4. Agile board organization

- Backlog
- To Do
- In Progress
- To Review
- Done
- Closed

### 5. Tools

- Travis : https://travis-ci.org/jonathanfievet/ecoviz
- SonarQube : https://sonarcloud.io/organizations/jonathanfievet-github/projects
- Waffle : https://waffle.io/jonathanfievet/ecoviz

## Project overview

The aim of **Ecoviz** is to facilitate the visualization of an _ecosystem_.

### Features:

- Import members from CSV
- Import/export projects and partners from CSV
- Dynamic visualization of projects and partners
- Dynamic map with filtering
- Reverse geocoding via OpenStreetMap's Nominatim
- 'Tags' based data model
- Users management

### Technologies

- [Eclipse Microprofile](https://microprofile.io/)
- [Eclipse JNoSQL](http://www.jnosql.org/)
- [PayaraMicro](https://www.payara.fish/payara_micro)
- [Angular 6](https://angular.io/)
- [D3.js](https://d3js.org/)
- [Leaflet](https://leafletjs.com/)
- [MongoDB](https://www.mongodb.com/)
