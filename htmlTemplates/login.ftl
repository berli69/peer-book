<html>

<head>
<title>PeerBook: Log in</title>
<link href="stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<body>

<#include "topBar.ftl">

<div id="bodyDiv">
<h1>Log in</h1>

<form method="POST" action="login">
Username: <br />
<input type="text" name="${USERNAME}" /><br />
Password: <br />
<input type="password" name="${PASSWORD}" /><br />
<input type="submit" value="Log in" />
</form>

<#if failed>
	<p><font color="FF0000">Your login credentials failed.</font></p>
</#if>

<p><a href="createProfile">Create New Profile</a></p>

</div>

</body>
</html>