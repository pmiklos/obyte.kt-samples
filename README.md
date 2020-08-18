# Obyte.kt Examples

Obyte.kt is a multi-platform Kotlin library to communicate with hub nodes in the Obyte cryptocurrency network.

The examples in this project will connect to the Obyte testnet.

See [live demo page](https://pmiklos.github.io/obyte.kt-samples/)

## Before you start

You have to build Obyte.kt library first and publish it into your local maven repository:

```bash
git clone https://github.com/pmiklos/obyte.kt.git
cd obyte.kt
git checkout 0.4.1
./gradlew publishToMavenLocal
```
Check if the libraries are successfully installed:
```bash
ls ~/.m2/repository/app/obyte/client/
```
## JVM

To run the JVM examples:

* simple - a very basic example of how to listen on Obyte events and send simple requests
* oracle - a simple oracle that posts random numbers to the DAG

```bash
./gradlew jvm:simple:run
```

```bash
./gradlew --console plain jvm:oracle:run
```

## Javascript (Browser) 

There are multiple Javascript examples you can run:

* simple - a very basic example of how to listen on Obyte events and send simple requests
* wallet - a simplified web wallet capable of listing balances, sending byte and asset payments
* giftcard - a simple gift card creator and single use gift card wallet

```bash
./gradlew js:simple:run
```

```bash
./gradlew js:wallet:run
```

```bash
./gradlew js:giftcard:run
```


This will start webpack and open the default browser with the application. To see what is going on under the hood, open the browser developer console.
