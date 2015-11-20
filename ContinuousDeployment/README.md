#H1 Continuous Deployment
---

This is the parent pom for creating debian package, every project needs to inherit this parent project if
you want to be able to build debian packages

the only requirement for the child project is that the control directory is defined with the following files:

```
src/main/deb/control
- control
- postinst
- preinst
- prerm
```

and the the Continuous Deployment artifact is defined as parent in the pom.xml

```xml
 <parent>
        <groupId>nl.politie.speeltuin</groupId>
        <artifactId>ci-parent</artifactId>
        <version>1.0.0</version>
    </parent>
```




