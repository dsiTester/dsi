FROM ubuntu:18.04

RUN \
  apt-get update && \
  apt-get install -y software-properties-common && \
# Install Git
  apt-get install -y git && \
# Install python
  apt-get update && \
  apt-get install -y python python-dev python-pip python-virtualenv && \
  rm -rf /var/lib/apt/lists/* && \
# Install misc
  apt-get update && \
  apt-get install -y sudo && \
  apt-get install -y vim && \
  apt-get install -y emacs && \  
  apt-get install -y wget && \
  apt-get install -y bc && \
  apt-get install -y zip unzip && \
  apt-get install -y openssh-client && \
  apt-get install -y ant && \
  apt-get install -y screen && \
  apt-get install -y htop && \ 
  apt-get install -y parallel && \
  apt-get install -y csvtool && \
# Install OpenJDK 8
  apt-get install -y openjdk-8-jdk && \
  mv /usr/lib/jvm/java-8-openjdk* /usr/lib/jvm/java-8-openjdk

# Set up user (dsiuser)
RUN useradd -ms /bin/bash -c "dsiuser" dsiuser && echo "dsiuser:docker" | chpasswd && adduser dsiuser sudo

USER dsiuser

WORKDIR /home/dsiuser/

# Use OpenJDK 8 when building the docker image
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk

# Install Maven 3.5.4 locally for user
RUN \
  wget http://mirrors.ibiblio.org/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz && \
  tar -xzf apache-maven-3.3.9-bin.tar.gz && mv apache-maven-3.3.9/ apache-maven/ && \
  rm apache-maven-3.3.9-bin.tar.gz && \
# Set up the user's configurations
  tail -n +10 ~/.bashrc > ~/tmp-bashrc && \
  cp ~/tmp-bashrc ~/.bashrc && \
  echo 'JAVAHOME=/usr/lib/jvm/java-8-openjdk' >> ~/.bashrc && \
  echo 'export JAVA_HOME=${JAVAHOME}' >> ~/.bashrc && \
  echo 'export M2_HOME=${HOME}/apache-maven' >> ~/.bashrc && \
  echo 'export MAVEN_HOME=${HOME}/apache-maven' >> ~/.bashrc && \
  echo 'export PATH=${M2_HOME}/bin:${JAVAHOME}/bin:${PATH}' >> ~/.bashrc 

COPY --chown=dsiuser . /home/dsiuser/
