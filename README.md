[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Download](https://api.bintray.com/packages/cjww-development/releases/feature-management/images/download.svg) ](https://bintray.com/cjww-development/releases/feature-management/_latestVersion)

feature-management
==================

End points to set and get feature states

To utilise this library add this to your sbt build file
```sbtshell
"com.cjww-dev.libs" % "feature-management_2.12" % "0.1.0"
```

Then add this to your application.conf 

```hocon
play.modules.enabled += "com.cjwwdev.modules.FeatureBindings"
``` 

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

License
=======
This code is open source software licensed under the Apache 2.0 License