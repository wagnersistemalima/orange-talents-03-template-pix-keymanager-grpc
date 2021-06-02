FROM openjdk:11 
VOLUME /tmp        
EXPOSE 50051       
ADD ./build/libs/KeyManager-0.1-all.jar nome.jar    
ENTRYPOINT ["java","-jar","/nome.jar"]   
