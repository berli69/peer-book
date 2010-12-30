<html>

<head>
<title>Send a Message</title>
<link href="stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<body>

<#include "topBarWithMenu.ftl">

<div id="bodyDiv">
<h1>Send a Message</h1>

<form method="POST" action="sendMessage">

<#if failed>
	<p class="error">The recipient you entered does not exist.</p>
</#if>

To: ${recipient}<br />
<input type="hidden" name="${RECIPIENT}" value="${recipient}" /><br />
Your message:<br />
<textarea name="${MESSAGE}"></textarea><br />
<input type="submit" value="Send" />
</form>

<a href="sendMessage">Back</a>

</div>

</body>
</html>