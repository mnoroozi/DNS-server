#!/usr/bin/env bash
gnome-terminal -e 'java -cp out/artifacts/DNS_server_jar/DNS-server.jar root.RootMain'
gnome-terminal -e 'java -cp out/artifacts/DNS_server_jar/DNS-server.jar tld.TLDMain 1'
gnome-terminal -e 'java -cp out/artifacts/DNS_server_jar/DNS-server.jar tld.TLDMain 2'
gnome-terminal -e 'java -cp out/artifacts/DNS_server_jar/DNS-server.jar authdns.AuthMain 1'
gnome-terminal -e 'java -cp out/artifacts/DNS_server_jar/DNS-server.jar authdns.AuthMain 2'
gnome-terminal -e 'java -cp out/artifacts/DNS_server_jar/DNS-server.jar authdns.AuthMain 3'
gnome-terminal -e 'java -cp out/artifacts/DNS_server_jar/DNS-server.jar authdns.AuthMain 4'
gnome-terminal -e 'java -cp out/artifacts/DNS_server_jar/DNS-server.jar agent.Main'