/*
 *    Copyright [2015] [Thanasis Argyroudis]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package skype.listeners;

import java.util.HashSet;

import skype.utils.Config;

import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.Skype;
import com.skype.SkypeException;

/**
 * This is general listener for all skype chats. His only job is to add more specific
 * listeners to chats through client-side skype because finding the chat id through
 * API is really difficult. This chat listener will search only for one command
 * <code>"!addlistener"</code> and then it would register a listener for the chat
 * from which the command came.
 * 
 * @see GroupChatAdderEvent
 */
public class GroupChatAdderListener implements ChatMessageListener {

	/** A Set that includes all the registered chats. */
	private final HashSet<Chat> registeredChats = new HashSet<Chat>(5);

	/**
	 * Instantiates a new group chat adder listener.
	 */
	public GroupChatAdderListener() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skype.ChatMessageListener#chatMessageReceived(com.skype.ChatMessage)
	 */
	@Override
	public void chatMessageReceived(ChatMessage rec) throws SkypeException {
		//Ignore only the owner of the bot can add a listener.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skype.ChatMessageListener#chatMessageSent(com.skype.ChatMessage)
	 */
	@Override
	public void chatMessageSent(ChatMessage sent) throws SkypeException {
		if (!sent.getContent().equalsIgnoreCase("!addlistener"))
			return;

		Chat chat = sent.getChat();
		if (!registeredChats.contains(chat)) {
			sent.setContent("");
			GroupChatListener group = new GroupChatListener(chat);
			System.out.println(chat.getWindowTitle());
			Skype.addChatMessageListener(group);

			if (Config.EnableEdits)
				Skype.addChatMessageEditListener(group.getGroupChatEditListenerInstance());

			registeredChats.add(chat);
		}
	}

}