<html>

<head>
<title>Exception Occurred</title>
<link href="stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<body>

<#if loggedIn>
	<#include "topBarWithMenu.ftl">
<#else>
	<#include "topBar.ftl">
</#if>

<div id="bodyDiv">
<p>A ${exception.getClass().getSimpleName()} occurred while attempting to service your request.</p>

<#if reportAll>
<p>
The Exception stacktrace follows:<br />
${exception.getMessage()!""}
<br />
<#list exception.getStackTrace() as stackTraceElement>
	${stackTraceElement} <br />
</#list>
</p>
</#if>

</div>

</body>
</html>