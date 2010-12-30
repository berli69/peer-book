<html>

<head>
<link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
<title>Private Messages</title>
</head>

<body>

<#include "topBarWithMenu.ftl">

<div id="bodyDiv">

<h1>Friend Requests</h1>
<#list friendRequests as friendRequest>
	<p>${friendRequest.getUsername()} has sent you a friend request.</p>
	
	<form method="POST" action="acceptFriendRequest">
	<input type="hidden" name=${FRIENDNAME}" value="${friendRequest.getUsername()}" />
	<input type="hidden" name="${FRIEND_REQUEST_NUMBER}" value="${friendRequest.getNumber()}" />
	<input type="submit" value="Accept" />
	</form>
	
	<form method="POST" action="rejectFriendRequest">
	<input type="hidden" name="${FRIEND_REQUEST_NUMBER}" value="${friendRequest.getNumber()}" />
	<input type="submit" value="Reject" />
	</form>
</#list>

<h1>Private Messages</h1>

<#list privateMessages as privateMessage>
	<p>On ${privateMessage.getSentDate().getTime()?datetime}, ${privateMessage.getFromName()} wrote: <br />
	<i>${privateMessage.getMessage()}</i>
	<br /><br />
	<a href="sendMessage?${RECIPIENT}=${privateMessage.getFromName()}">Reply to this message</a>
	</p>

	<form action="deleteMessage" method="POST">
	<input name="${MESSAGE_NUMBER}" value="${privateMessage.getNumber()}" type="hidden" />
	<input value="Delete" type="submit" />
	</form>
</#list>

</div>

</body>

</html>