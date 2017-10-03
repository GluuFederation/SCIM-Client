SCIM-Client
===========

SCIM is a specification designed to reduce the complexity of user management operations by providing a common user schema
and the patterns for exchanging this schema using HTTP in a platform-neutral fashion. The aim of SCIM is achieving
interoperability, security, and scalability in the context of identity management.

You can think of SCIM merely as a REST API with endpoints exposing CRUD functionality (create, update, retrieve and delete).

This project consists of a ready-to-use Java client to interact with those endpoints.

Detailed specifications for SCIM can be found at [RFC 7642](https://tools.ietf.org/html/rfc7642),
[RFC 7643](https://tools.ietf.org/html/rfc7643), and [RFC 7644](https://tools.ietf.org/html/rfc7644).

In Gluu's implementation, we have User, Group and Bulk operations. Below are the links for the latest Gluu implementation
for SCIM client:

* [SCIM 3.0.2 stable client library binary](http://ox.gluu.org/maven/gluu/scim/client/SCIM-Client/3.0.2/)
* [SCIM 3.1.2-SNAPSHOT client library binary](http://ox.gluu.org/maven/gluu/scim/client/SCIM-Client/3.1.2-SNAPSHOT)

Recommended reading before using the client:

* [User Management with SCIM](https://www.gluu.org/docs/ce/admin-guide/user-scim/)
* [SCIM protected by UMA](https://www.gluu.org/docs/ce/admin-guide/scim-uma/)