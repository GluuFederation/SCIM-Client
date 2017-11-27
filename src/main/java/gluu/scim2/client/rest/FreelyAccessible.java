package gluu.scim2.client.rest;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A method annotated with FreelyAccesible will not have inserted the Authorization header when the request is about to
 * be issued (see AbstractScimClient)
 * Created by jgomer on 2017-11-25.
 */
@Retention(RUNTIME)
@Target({METHOD})
public @interface FreelyAccessible {}
