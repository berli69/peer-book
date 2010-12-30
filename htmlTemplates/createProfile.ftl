<html>

<head>
<title>Create Profile</title>
<link href="stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<body>

<#include "topBar.ftl">

<div id="bodyDiv">
<h1>Create Profile</h1>

<#if badPreviousEntry>
	<p class="error">Either this username already exists or your username or password was too short. 
	Usernames must be at least ${MINIMUM_USERNAME_LENGTH} characters long. Passwords must be at least 
	${MINIMUM_PASSWORD_LENGTH} characters long.</p>
<#else>
	<p>Your username must be at least ${MINIMUM_USERNAME_LENGTH} characters long, and passwords must be at least
	${MINIMUM_PASSWORD_LENGTH} characters long.</p>
</#if>

<form method="POST" action="createProfile">
Username (your full name): </br>
<input type="text" name="${USERNAME}" /> </br>
Password: <br/>
<input type="password" name="${PASSWORD}" /> </br>
<input type="submit" value="Submit" />
</form>
</div>

</body>
</html>