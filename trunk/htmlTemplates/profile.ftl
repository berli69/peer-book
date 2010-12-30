<html>

<head>
<link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
<title>${profile.getPublicProfile().getPersonalData().getName()}'s Profile</title>
</head>

<body>

<#include "topBarWithMenu.ftl">

<div id="bodyDiv">

<h1>${profile.getPublicProfile().getPersonalData().getName()}'s Personal Information
<#if ourProfile>
 - <a href="updateProfile">Edit</a>
</#if>
</h1>
<p>
<#if profile.getPublicProfile().getPersonalData().isSetCurrentStatus()>
	<h3>${profile.getPublicProfile().getPersonalData().getName()} ${profile.getPublicProfile().getPersonalData().getCurrentStatus().getStatus()}</h3>
	
	<#if ourProfile>
		<form method="POST" action="deleteStatus">
		<input type="submit" value="Delete Status" />
		</form>
	</#if>
</#if>

<#if profile.getPublicProfile().getPersonalData().isSetDateOfBirth() && profile.getPublicProfile().getPersonalData().getDateOfBirth()??>
	<br />
	Date of Birth: ${profile.getPublicProfile().getPersonalData().getDateOfBirth().getTime()?date}
</#if>

<#assign x = 1>
<#list profile.getPublicProfile().getPersonalData().getEmailAddressArray() as emailAddress>
	<#if emailAddress?has_content>
		<br />
		Email Address ${x}: ${emailAddress}
	</#if>
	<#assign x = x + 1>
</#list>

<#if profile.getPublicProfile().getPersonalData().isSetPhone() && profile.getPublicProfile().getPersonalData().getPhone()?has_content>
	<br />
	Phone number: ${profile.getPublicProfile().getPersonalData().getPhone()}
</#if>

<#if profile.getPublicProfile().getPersonalData().isSetAddress() && profile.getPublicProfile().getPersonalData().getAddress()?has_content>
	<br />
	Address: ${profile.getPublicProfile().getPersonalData().getAddress()}
</#if>

<#if profile.getPublicProfile().getPersonalData().isSetCourse() && profile.getPublicProfile().getPersonalData().getCourse()?has_content>
	<br />
	Course: ${profile.getPublicProfile().getPersonalData().getCourse()}
</#if>

<#if profile.getPublicProfile().getPersonalData().isSetHomeTown() && profile.getPublicProfile().getPersonalData().getHomeTown()?has_content>
	<br />
	Home town: ${profile.getPublicProfile().getPersonalData().getHomeTown()}
</#if>

<#if profile.getPublicProfile().getPersonalData().isSetInterests() && profile.getPublicProfile().getPersonalData().getInterests()?has_content>
	<br />
	Interests: ${profile.getPublicProfile().getPersonalData().getInterests()}
</#if>

<#if !ourProfile>
	<#assign friends = profile.getPublicProfile().getFriendList().getFriendArray()>
	<p><i>User has ${friends?size} friends.</i></p>
	
	<#if (friends?size > 0)>
		<#list friends as friend>
			<#assign friendName = friend.getFriendName()>
			<#if (ourFriends?seq_contains(friendName))>
				<a href="viewProfile?${FRIENDNAME}=${friendName}">${friendName}</a>
			<#else>
				${friendName}
			</#if>
			<br />
		</#list>
	</#if>
</#if>
</p>

<h1>Wall Posts</h1>

<form action="addWallPost" method="POST">
<input name="${FRIENDNAME}" value="${profile.getPublicProfile().getPersonalData().getName()}" type="hidden" />
<textarea name="${MESSAGE}"></textarea><br />
<input name="" value="Post" type="submit" />
</form>

<#assign wallPosts = profile.getPublicProfile().getWallPosts().getWallPostArray()>
<#assign wallPostsSize = wallPosts?size>
<#if (wallPostsSize > 0)>
	<#list (wallPostsSize - 1)..0 as i>
		<p>
		<#assign wallPost = profile.getPublicProfile().getWallPosts().getWallPostArray(i)>
		${wallPost.getPostDate().getTime()?datetime!}, ${wallPost.getUsername()} wrote: <br />
		${wallPost.getPost()}
		
		<#if ourProfile>
			<form method="POST" action="deleteWallPost">
			<input type="hidden" name="${WALLPOST_NUMBER}" value="${wallPost.getNumber()}" />
			<input type="submit" value="Delete" />
			</form>
		</#if>
	</#list>
<#else>
	<p><i>User has 0 wall posts.</i></p>
</#if>

</div>

</body>

</html>