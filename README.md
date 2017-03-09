# scala-cliente
SCALA Cliente Demo Code / SCALA Cliente Connettività dimostrazione

# java version
All code must use Java 8 minimum

# java dependencies
Libs dependencies are found in the pom.xml

# client certificate import
This is how to import the provided certificate to the Java KeyStore

jdk/bin/keytool -importkeystore -srckeystore mypfxfile.pfx -srcstoretype pkcs12 -destkeystore clientcert.jks -deststoretype JKS

# HMAC timestamp and transaction id
Each request must have a different transaction id and a continuously increasing timestamp
The timestamp is just an integer that for each request is greater than the previous one

