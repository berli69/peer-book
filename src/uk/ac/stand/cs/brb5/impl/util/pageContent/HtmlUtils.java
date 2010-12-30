/*******************************************************************************
 * PeerBook
 * Copyright (C) 2009 Ben Birt
 * http://blogs.cs.st-andrews.ac.uk/peerbook/
 * 
 * This file is part of PeerBook.
 * 
 * PeerBook is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PeerBook is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PeerBook.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package uk.ac.stand.cs.brb5.impl.util.pageContent;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.FriendRequest;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.privateProfile.PrivateMessage;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.Friend;
import uk.ac.stAndrews.cs.hostBrb5.peerBook.publicProfile.PublicProfileDocument;
import uk.ac.stand.cs.brb5.impl.exceptions.PeerBookException;
import uk.ac.stand.cs.brb5.impl.http.ArgumentType;
import uk.ac.stand.cs.brb5.impl.http.Arguments;
import uk.ac.stand.cs.brb5.impl.kernel.ProfileField;
import uk.ac.stand.cs.brb5.interfaces.IKeySpaceManager;
import uk.ac.stand.cs.brb5.interfaces.IPeerBookKernel;
import uk.ac.standrews.cs.nds.util.Diagnostic;
import uk.ac.standrews.cs.nds.util.DiagnosticLevel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * Provides functionality for constructing and returning HTML Content pages.
 * 
 * @author Ben
 *
 */
public class HtmlUtils {

	private static final String TEMPLATE_FOLDER = "htmlTemplates/";
	private static final Map<String, Object> DEFAULT_TEMPLATE_VARS;
	
	static {
		DEFAULT_TEMPLATE_VARS = new HashMap<String, Object>();
		
		for (ProfileField field : ProfileField.values()) {
			DEFAULT_TEMPLATE_VARS.put(field.toString(), field.toString());
		}
		
		for (ArgumentType type : ArgumentType.values()) {
			DEFAULT_TEMPLATE_VARS.put(type.toString(), type.toString());
		}
		
		DEFAULT_TEMPLATE_VARS.put(Arguments.PROFILE_UPDATED, Arguments.PROFILE_UPDATED);
		
		DEFAULT_TEMPLATE_VARS.put("MINIMUM_PASSWORD_LENGTH", IPeerBookKernel.MINIMUM_PASSWORD_LENGTH);
		DEFAULT_TEMPLATE_VARS.put("MINIMUM_USERNAME_LENGTH", IKeySpaceManager.MINIMUM_USERNAME_LENGTH);
	}

	private IPeerBookKernel kernel;
	private Configuration configuration;

	/**
	 * @param kernel {@link IPeerBookKernel} to be used to do checks while constructing some pages.
	 */
	public HtmlUtils(IPeerBookKernel kernel) {
		this.kernel = kernel;
		this.configuration = new Configuration();
	}

	/**
	 * @return An error 404 page.
	 * @throws PeerBookException
	 */
	public HtmlPage error404() throws PeerBookException {
		return getOutput("error404.ftl", DEFAULT_TEMPLATE_VARS);
	}

	/**
	 * @return A page telling the user they have logged out.
	 * @throws PeerBookException
	 */
	public HtmlPage loggedOut() throws PeerBookException {
		return getOutput("loggedOut.ftl", DEFAULT_TEMPLATE_VARS);
	}

	/**
	 * @param friends An array of {@link Friend}s that are the user's friends.
	 * @return A page listing the user's friends.
	 * @throws PeerBookException
	 */
	public HtmlPage friends(Friend[] friends) throws PeerBookException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.putAll(DEFAULT_TEMPLATE_VARS);
		vars.put("friends", friends);

		return getOutput("friends.ftl", vars);
	}

	/**
	 * @param privateMessages An array of the user's {@link PrivateMessage}s.
	 * @param friendRequests An array of the user's {@link FriendRequest}s.
	 * @return A page listing the user's friend requests and private messages.
	 * @throws PeerBookException
	 */
	public HtmlPage messages(PrivateMessage[] privateMessages, FriendRequest[] friendRequests) throws PeerBookException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.putAll(DEFAULT_TEMPLATE_VARS);
		vars.put("friendRequests", friendRequests);
		vars.put("privateMessages", privateMessages);

		return getOutput("messages.ftl", vars);
	}

	/**
	 * @param username The recipient's username of the message.
	 * @param message The message sent to the recipient.
	 * @return A page confirming to the user that the message was sent to the recipient.
	 * @throws PeerBookException
	 */
	public HtmlPage sentMessage(String username, String message) throws PeerBookException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("username", username);
		vars.put("message", message);

		return getOutput("sentMessage.ftl", vars);
	}

	/**
	 * @param username The username of the removed friend.
	 * @return A page confirming that the given friend was removed from the user's friends list.
	 * @throws PeerBookException
	 */
	public HtmlPage friendRemoved(String username) throws PeerBookException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("username", username);

		return getOutput("friendRemoved.ftl", vars);
	}

	/**
	 * @param viewer The profile viewer's username.
	 * @param profile The profile being viewed.
	 * @return A page containing the given profile.
	 * @throws PeerBookException
	 */
	public HtmlPage profile(String viewer, PublicProfileDocument profile) throws PeerBookException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.putAll(DEFAULT_TEMPLATE_VARS);
		vars.put("profile", profile);
		vars.put("ourFriends", getOurFriendNames(viewer));
		vars.put("ourProfile", profile.getPublicProfile().getPersonalData().getName().equals(viewer));

		return getOutput("profile.ftl", vars);
	}

	/**
	 * @param failed True if the previous attempt to log in failed.
	 * @return A login page.
	 * @throws PeerBookException
	 */
	public HtmlPage login(boolean failed) throws PeerBookException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.putAll(DEFAULT_TEMPLATE_VARS);
		vars.put("failed", failed);

		return getOutput("login.ftl", vars);
	}

	/**
	 * @param failed True if message failed.
	 * @param recipient Already filled in recipient, if there is one.
	 * @return A page allowing the user to send a private message.
	 * @throws PeerBookException
	 */
	public HtmlPage sendMessage(boolean failed, String recipient) throws PeerBookException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.putAll(DEFAULT_TEMPLATE_VARS);
		vars.put("failed", failed);
		vars.put("recipient", recipient);

		return getOutput("sendMessage.ftl", vars);
	}

	/**
	 * @param badPreviousEntry True if the previous profile creation request failed.
	 * @return A profile creation page.
	 * @throws PeerBookException
	 */
	public HtmlPage createProfile(boolean badPreviousEntry) throws PeerBookException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.putAll(DEFAULT_TEMPLATE_VARS);
		vars.put("badPreviousEntry", badPreviousEntry);

		return getOutput("createProfile.ftl", vars);
	}

	/**
	 * If {@link DiagnosticLevel} < RESULT, displays the exceptions stacktrace. Otherwise just informs the user
	 * of the exception.
	 * 
	 * @param viewer The viewer's username.
	 * @param e The exception to be displayed.
	 * @return A page explaining an exception.
	 */
	public Content exception(String viewer, Exception e) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("loggedIn", kernel.isLoggedIn(viewer));
		vars.put("exception", e);
		vars.put("reportAll", Diagnostic.getLevel().compareTo(DiagnosticLevel.RESULT) < 0);

		try {
			return getOutput("exception.ftl", vars);
		} catch (PeerBookException e1) {
			return new TextContent(e1.toString());
		}
	}

	/**
	 * @param badPreviousSearch True if the user previously entered a too-short search string.
	 * @param searchResults A List of search results for a username search.
	 * @param actionPage The action to take upon the user choosing a username.
	 * @return A page detailing the search results for a given search.
	 * @throws PeerBookException
	 */
	public HtmlPage search(boolean badPreviousSearch, List<String> searchResults, String actionPage) throws PeerBookException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.putAll(DEFAULT_TEMPLATE_VARS);
		vars.put("searchResults", searchResults);
		vars.put("badPreviousSearch", badPreviousSearch);
		vars.put("actionPage", actionPage);

		return getOutput("search.ftl", vars);
	}

	/**
	 * @param failed If the profile update previously failed.
	 * @param publicProfileDoc The {@link PublicProfileDocument} being updated.
	 * @return A page allowing the user to update the fields in their profile.
	 * @throws PeerBookException
	 */
	public HtmlPage updateProfile(boolean failed, PublicProfileDocument publicProfileDoc) throws PeerBookException {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.putAll(DEFAULT_TEMPLATE_VARS);
		vars.put("personalData", publicProfileDoc.getPublicProfile().getPersonalData());
		vars.put("failed", failed);

		return getOutput("updateProfile.ftl", vars);
	}

	private List<String> getOurFriendNames(String viewer) throws PeerBookException {
		Friend[] ourFriends = kernel.getProfile(viewer, viewer).getPublicProfile().getFriendList().getFriendArray();
		List<String> friendNames = new ArrayList<String>();
		friendNames.add(viewer);

		for (Friend friend : ourFriends) {
			friendNames.add(friend.getFriendName());
		}

		return friendNames;
	}

	private HtmlPage getOutput(String templateName, Map<String, Object> vars) throws PeerBookException {
		try {
			Template template = configuration.getTemplate(TEMPLATE_FOLDER + templateName);

			StringWriter writer = new StringWriter();
			template.process(vars, writer);
			return new HtmlPage(writer.toString());
		} catch (IOException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new PeerBookException(e);
		} catch (TemplateException e) {
			Diagnostic.trace(DiagnosticLevel.RESULT, e);
			throw new PeerBookException(e);
		}
	}

}
