[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

feature-management
==================

End points to set and get feature states

To utilise this library add this to your sbt build file
```sbtshell
"dev.cjww.libs" % "feature-management_2.13" % "x.x.x"
```

Then add this to your application.conf 

```hocon
play.modules.enabled += "dev.cjww.modules.FeatureBindings"
``` 

users need to implement FeatureController

and finally, to be able to hit the routes add ```features.routes``` to your routes file

### GET /feature/:feature

Example request:
```
/feature/example-feature
```

Json response body:
```json
//On Ok (200)
{
    "feature" : "example-feature",
    "state" : true //or false
}
```

### GET /features

Example request:
```
/features
```

Json response body:
```json
//On Ok (200)
[
    {
        "feature" : "example-feature",
        "state" : true
    },
    {
        "feature" : "example-feature-2",
        "state" : false
    }
]
```

### POST /feature/:feature/state/:state

Example request:
```
/feature/example-feature/state/true
```

Json response body:
```json
//On Ok (200)
{
    "example-feature" : true
}
```

Shuttering
==========

To be able to hit the shuttering routes add ```shuttering.routes``` to your routes file and users need to implement shuttering controller

### POST /service-shuttering/true

Hitting this route will shutter the service by setting the `shuttered` property to true

### POST /service-shuttering/false

Hitting this route will unshutter the service by setting the `shuttered` property to false

License
=======
This code is open source software licensed under the Apache 2.0 License