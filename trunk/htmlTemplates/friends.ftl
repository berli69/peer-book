<html>

<head>
<link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
<title>Friends</title>
</head>

<body>

<#include "topBarWithMenu.ftl">

<div id="bodyDiv">
<h1>Friends</h1>
<p>

<#list friends as friend>
	<br />
	<a href="viewProfile?${FRIENDNAME}=${friend.getFriendName()}">${friend.getFriendName()}</a>
	<form method="POST" action="removeFriend">
	<input type="hidden" name="${FRIENDNAME}" value="${friend.getFriendName()}" />
	<input type="submit" value="Remove Friend" />
	</form>
</#list>

</p>
</div>

</body>

</html>