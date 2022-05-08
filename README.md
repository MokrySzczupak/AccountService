# account-service-backend

Company service to handle users, payments, security events and more.

**Service accepts only *@acme.com* emails.**

**(Frontend project: https://github.com/NorbertWojtowicz/account-service-frontend)**

Main functionality is descripted below.

# How to test the application by yourself?
Testing version of application is available at:
>http://34.135.102.223:8080/account-service/

For better experience, I reccommend using the **frontend application** available at:
>http://34.116.174.200

 For testing purposes, You can use *test users*:
|EMAIL|PASSWORD|ROLE|
|-------------------|-------------------|-----------------------------|
|**accountantUser@acme.com**|*AccountantPa$$word*|*USER, ACCOUNTANT*|
|**adminUser@acme.com**|*AdminPa$$word*|*USER, ADMINISTRATOR*|
|**auditorUser@acme.com**|*AuditorPa$$word*|*USER, AUDITOR*|

# REST API Endpoints
|       METHOD         |URL                          |DESCRIPTION                       |ROLE|
|----------------|-------------------------------|-----------------------------|-----------------------------|
|POST|`/api/auth/signup`            |Allows the user to register on the service. | EVERYONE|
|POST          |`/api/auth/changepass`            |Changes a user password.  | USER, ACCOUNTANT, ADMINISTRATOR|
|GET         |`/api/empl/payment`|Gives access to the employee's payments.| USER, ACCOUNTANT, AUDITOR, ADMINISTRATOR|
|POST|`/api/acct/payments`|Uploads payments.|ACCOUNTANT|
|PUT|`/api/acct/payments`|Updates payment information.| ACCOUNTANT|
|PUT|`/api/admin/user/role`|Changes user roles.| ADMINISTRATOR|
|DELETE|`/api/admin/user`|Deletes a user.| ADMINISTRATOR|
|GET         |`/api/admin/user`|Displays information about all users.| ADMINISTRATOR|
|PUT|`/api/admin/user/access`|Locks/unlocks users.| ADMINISTRATOR|
|GET|`/api/security/events`|Displays all security events.| AUDITOR|
|GET|`/api/security/events`|Get all payments of all users| ACCOUNTANT|

# Security
## User authentication
Service is protected by basic HTTP authentication. For storing users and passwords I added a JDBC implementation of `UserDetailsService` with an H2 database;

**Available user roles**:
- ACCOUNTANT
- ADMINISTRATOR
- AUDITOR
- USER

Project uses **self signed certificate** generated with keytool, but for easier testing it is disabled in application.properties.
## Security events
Each action (ex. login, upload payment, change user role or access) is registered as security event with informations like user email, action or path. User with auditor role can retrieve all security events.
### Types of events:
|EVENT NAME|DESCRIPTION|
|-------|-------|
|CREATE_USER|A user has been successfully registered|
|CHANGE_PASSWORD|A user has changed the password successfully|
|ACCESS_DENIED|A user is trying to access a resource without access rights|
|LOGIN_FAILED|Failed authentication|
|GRANT_ROLE|A role is granted to a user|
|REMOVE_ROLE|A role has been revoked|
|LOCK_USER|The Administrator has locked the user|
|UNLOCK_USER|The Administrator has unlocked a user|
|DELETE_USER|The Administrator has deleted a user|
|BRUTE_FORCE|A user has been blocked on suspicion of a brute force attack|
## Brute force security
If there are more than 5 consecutive attempts to enter an incorrect password, an entry about this appears in the security events. Also, the user account is blocked.
## Breached passwords security
The passwords submitted during a registration, login, and password change are checked against a  **set of breached passwords**. If the password is breached, the application requires users to set a new non-breached password.
# Database
## Management system
Project uses **H2** as a database management system.
## Relationship diagram
![Source image for better resolution](https://i.imgur.com/ruhFZ8Q.png)
