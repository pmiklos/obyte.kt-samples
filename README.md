# Obyte.kt Examples

Obyte.kt is a multi-platform Kotlin library to communicate with hub nodes in the Obyte cryptocurrency network.

All examples will connect to the Obyte testnet and start a heartbeat to keep the connection open while displaying the incoming and outgoing messages.

## Before you start

You have to build Obyte.kt library first and publish it into your local maven repository:

```bash
git clone https://github.com/pmiklos/obyte.kt.git
cd obyte.kt
git checkout 0.2.0
./gradlew publishToMavenLocal
```
Check if the libraries are successfully installed:
```bash
ls ~/.m2/repository/app/obyte/client/
```
## JVM

To run the JVM example:

```bash
./gradlew jvm:run
```

## Javascript (Browser) 

```bash
./gradlew js:run
```
This will start webpack and open the default browser with the application. To see what is going on under the hood, open the browser developer console.
