# Start from GridGain Professional image.
FROM gridgain/gridgain-pro:8.7.9

# Set config uri for node.
ENV CONFIG_URI Cluster-2-server.xml

# Copy optional libs.
ENV OPTION_LIBS ignite-rest-http

# Update packages and install maven.
RUN set -x \
    && apk add --no-cache \
        openjdk8

RUN apk --update add \
    maven \
    && rm -rfv /var/cache/apk/*

# Append project to container.
ADD . Cluster-2

# Build project in container.
RUN mvn -f Cluster-2/pom.xml clean package -DskipTests

# Copy project jars to node classpath.
RUN mkdir $IGNITE_HOME/libs/Cluster-2 && \
   find Cluster-2/target -name "*.jar" -type f -exec cp {} $IGNITE_HOME/libs/Cluster-2 \;