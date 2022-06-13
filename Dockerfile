# Stage that builds the application, a prerequisite for the running stage
FROM maven:3-openjdk-17-slim as build
RUN curl -sL https://deb.nodesource.com/setup_16.x | bash -
RUN apt-get update -qq && apt-get install -qq --no-install-recommends nodejs

# Stop running as root at this point
RUN useradd -m starters
WORKDIR /usr/src/app/
RUN chown starters:starters /usr/src/app/
USER starters

# Copy pom.xml and prefetch dependencies so a repeated build can continue from the next step with existing dependencies
COPY --chown=starters pom.xml ./
RUN mvn dependency:go-offline -Pproduction

# Copy all needed project files to a folder
COPY --chown=starters:starters src src
COPY --chown=starters:starters frontend frontend
COPY --chown=starters:starters package.json ./

# Using * after the files that are autogenerated so that so build won't fail if they are not yet created
COPY --chown=starters:starters package-lock.json* pnpm-lock.yaml* webpack.config.js* ./


# Build the production package, assuming that we validated the version before so no need for running tests again
RUN mvn clean package -DskipTests -Pproduction

# Running stage: the part that is used for running the application
FROM openjdk:17-jdk-slim
COPY --from=build /usr/src/app/target/*.jar /usr/app/starters.jar

RUN mkdir -p /data/db
RUN mkdir -p /data/import
RUN chown starters:starters -R /data
WORKDIR /data

RUN useradd -m starters
USER starters
EXPOSE 8080
CMD java -jar /usr/app/starters.jar
