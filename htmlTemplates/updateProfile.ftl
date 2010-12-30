<html>

<head>
<link rel="stylesheet" type="text/css" href="stylesheet.css"></link>
<title>Update Profile</title>
</head>

<body>

<#include "topBarWithMenu.ftl">

<div id="bodyDiv">
<h1>Update Your Profile</h1>

<#if failed>
	<p class="error">There was an error while parsing your personal details. Please enter them as requested on the form.</p>
</#if>

<form action="updateProfile" method="POST">
Date of Birth (dd-mm-yyyy): <input name="${DATE_OF_BIRTH}" value="${(personalData.getDateOfBirth().getTime()?string("dd-MM-yyyy"))!""}" type="text" /><br />
Email Address 1: <input name="${EMAIL_ADDRESS_0}" value="${personalData.getEmailAddressArray(0)!""}" type="text" /><br />
Email Address 2: <input name="${EMAIL_ADDRESS_1}" value="${personalData.getEmailAddressArray(1)!""}" type="text" /><br />
Email Address 3: <input name="${EMAIL_ADDRESS_2}" value="${personalData.getEmailAddressArray(2)!""}" type="text" /><br />
Email Address 4: <input name="${EMAIL_ADDRESS_3}" value="${personalData.getEmailAddressArray(3)!""}" type="text" /><br />
Email Address 5: <input name="${EMAIL_ADDRESS_4}" value="${personalData.getEmailAddressArray(4)!""}" type="text" /><br />
Phone: <input name="${PHONE}" value="${personalData.getPhone()!""}" type="text" /><br />
Address: <input name="${ADDRESS}" value="${personalData.getAddress()!""}" type="text" /><br />
Course: <input name="${COURSE}" value="${personalData.getCourse()!""}" type="text" /><br />
Home Town: <input name="${HOME_TOWN}" value="${personalData.getHomeTown()!""}" type="text" /><br />
Interests: <input name="${INTERESTS}" value="${personalData.getInterests()!""}" type="text" /><br />
<input name="${PROFILE_UPDATED}" type="hidden" />
<input value="Submit" type="submit" />
</form>

</div>

</body>

</html>