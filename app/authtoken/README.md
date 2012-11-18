# AuthenticityToken module for Play2!

## What is the AuthenticityToken ?

The authenticity token is a way around one of the most serious internet security threats: [CRSF attacks](http://en.wikipedia.org/wiki/Cross-site_request_forgery). It ensures that the client submitting a form is the one who received the page (and not a hacker who stole your session data).

## Why should I care ?

According to the United States Department Of Homeland Security the most dangerous CSRF vulnerability ranks in at the 909th most dangerous software bug ever found, making this vulnerability more dangerous than most buffer overflows.[6] Other severity metrics have been issued for CSRF vulnerabilities that result in remote code execution with root privileges[7] as well as a vulnerability that can compromise a root certificate, which will completely undermine a public key infrastructure.[8]

## What does the module provide ?

1. a scala tag that generates a hidden authenticityToken
2. a standard play validator to confirm token authenticty

## How does it work ?

In a nutshell:

1. on every form post, we add a hidden parameter containing a uuid
2. the uuid is signed and its signature is stored in the session (which translated into a cookie) 

When the user submits the form, we get: the uuid, the signature and the other form inputs.

1. We sign the incoming uuid again
2. Validation passes it the signatures match  session.sign=uuid.sign

Should an attacker inject a different id, he will never figure how the generate the correct signature.

## Usage

**3 minutes**, success guarantied… follow these steps

IMPORTANT: You need to use the latest play2 **master**

### 1. Install the module dependency

** THIS IS DEPRECATED, just copy the source files in your project. It will work equaly well **

Edit file `project/Build.scala` and adjust the following settings

```
val appDependencies = Seq(
  "crionics" %% "play2-authenticitytoken" % "1.0-SNAPSHOT"
)


val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
  resolvers += "Crionics Github Repository" at "http://orefalo.github.com/m2repo/releases/"
)
```

### 2. Add the Token to FORM elements

Adding the authenticity token is trivial: For every form, add `@authenticityToken()` inside the form elements.

```
@import _root_.views.html.authtoken.authenticityToken

<form action="@routes.Application.process()" method="post">
   @authenticityToken()
   Please input your name
   <input name="name" />
   <input type="submit"/>
</form>
```

### 3. Validate the token

Validating the token is equaly simple: the `@AuthenticityToken` Play validator is available.

```
import authtoken.validator.AuthenticityToken;

public class FormData {
	
	@AuthenticityToken
	public String authtoken;
	
	public String name;
}

public static Result process() {

	Form<FormData> form = form(FormData.class).bindFromRequest();

	if (form.hasErrors()) {
		return badRequest("authenticity validation FAILED");
	} else {
		return ok("authenticity validation PASSED");
	}
}
```


## Sample Application

A sample application is available, to run it:

1. clone the repository
2. cd sample
3. play run
4. open a browser to [localhost:9000](http://localhost:9000)


## Credits

* play2-authenticitytoken module by [Olivier Refalo](https://github.com/orefalo)

## History

version 1.0: First release
