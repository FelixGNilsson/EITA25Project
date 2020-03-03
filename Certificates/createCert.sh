#!/bin/bash

read -p "enter name of client: " clientName
mkdir -p "client/$clientName"

keytool -import -file Certificates/public/CA.pem -alias CA -keystore TrustStore
keytool -keystore KeyStore -genkey -alias $clientName
keytool -certreq -alias $clientName -keystore KeyStore -file $clientName.csr

openssl x509 -CA Certificates/public/CA.pem -CAkey Certificates/private/cakey.pem -CAserial Certificates/public/CA.srl -req -in $clientName.csr -out $clientName.pem -days 100

keytool -import -trustcacerts -alias root -file Certificates/public/CA.pem -keystore KeyStore
keytool -import -trustcacerts -alias $clientName -file $clientName.pem -keystore KeyStore

mv TrustStore KeyStore $clientName.csr $clientName.pem client/$clientName/ 


