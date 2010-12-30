<html>

<head>
<title>Username Search</title>
<link href="stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<body>

<#include "topBarWithMenu.ftl">

<div id="bodyDiv">
<h1>Username Search</h1>

<#if badPreviousSearch>
	<p class="error">Your search string was too short. It must be at least ${MINIMUM_USERNAME_LENGTH} characters long.</p>
<#else>
	<p>Your search string must be at least ${MINIMUM_USERNAME_LENGTH} characters long.</p>
</#if>

<form method="POST" action="${actionPage}">
Search: <br />
<input type="text" name="${SEARCH}" />
<br /><br />
<input type="submit" value="Search" />
</form>

<#if searchResults??>
	<p>
	<#if (searchResults?size == 0)>
		<p><i>No search results.</i></p>
	</#if>
	
	<#list searchResults as searchResult>
		${searchResult}
		<form method="POST" action="${actionPage}">
		<input type="hidden" name="${CHOSEN_NAME}" value="${searchResult}" />
		<input type="submit" value="Choose" />
		</form>
	</#list>
	</p>
</#if>
</div>

</body>
</html>