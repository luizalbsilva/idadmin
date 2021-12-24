# ID Administrator

Just a proof of concept application. The application will just administrate the numbers generated through it.

We have a docker-compose file to setup all the infrastructure for you. We're using the postgres on the default 5432 
port, a web based sql client for you to check the data transitions, and the keycloack server, so you can configure the 
users to access our system.

The only thing you must do is to configure the keycloak server so we can use it in our system, but don't worry, it's not 
that much things to do ;D

## Configuring Keycloak

First, create a realm named 'idgenerator'.

### Creating roles

Create these roles, use them on your users.


| ROLE | Composite | Description |
|------|-------|-------------|
| ADMIN | false | Can create Generators |
| USER | false | Can only use a generator create do him|


### Create your users

How you must create your users, give them the roles you fell they need, and finally, create the clients for your project.

You can use another realm if you want to, but you must change the system configurations on the application.properties file.