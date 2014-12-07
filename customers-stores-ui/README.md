Customer Stores User Interface
==============================

## Prerequisites

- Make sure you have [Spring Boot for Groovy installed] (http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#getting-started-gvm-cli-installation)
- Make sure [Spring Cloud CLI is installed] (https://github.com/spring-cloud/spring-cloud-cli)

## How to Run

Right now the easiest way to run the UI is to use Spring Boot.

    $ spring run app.groovy
    
and visit [http://localhost:9900](http://localhost:9900).

## How to Build

You can also serve the UI using Grunt. As a prerequisite you need to have NPM and Bower installed:

Next, install all dependencies needed (try with `sudo` and `-g` if this doesn't work):

	$ npm install
    $ npm install bower
	$ bower install

Now you can start the UI using:

	$ grunt serve

## Deploy to Cloudfoundry

Cloudfoundry tends to assume this app is a node.js app (because of all
the javascript), so you need to package it carefully:

    $ spring jar --exclude='+bower*,node*,Gruntfile.js,*.json' --include='dist/**' app.jar app.groovy

Then

    $ cf push customersui -p app.jar

