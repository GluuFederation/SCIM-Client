SCIM-Client
===========

**IMPORTANT:**
This repo is split into two modules:

* [scim-client](scim-client), which follows the client side API being used since version 3.1.0, and
* [scim-client2](scim-client2) that uses a newer API

**scim-client** is considered deprecated and will be removed for next release (**4.0.0**). Once 4.0.0 version is released, only **scim-client2** will remain.

With the more recent version of the client, developers have access to new features found in version 3.1.3 of Gluu's SCIM service implementation. You can find a summary of service features and enhancements [here](https://gluu.org/docs/ce/3.1.3/user-management/scim-new-features/).

You can still interact with SCIM service version 3.1.3 using the older client without needing to alter your code, however there are a few corner cases that deserve some attention. For more information check this [page](https://gluu.org/docs/ce/3.1.3/user-management/scim-new-features/#are-there-any-special-cases-to-account-if-still-using-older-client).

There is also a small [migration guide](https://gluu.org/docs/ce/3.1.3/user-management/scim-new-features/#how-to-migrate-my-current-code-to-use-the-newer-scim-client-api) if you are interested in porting your app to newer client API.
