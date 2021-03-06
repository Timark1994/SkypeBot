/*
 *    Copyright [2016] [Thanasis Argyroudis]

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

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.SkypeException;
import com.skype.User;

import skype.gui.popups.WarningPopup;
import skype.handlers.CommandHandler;
import skype.handlers.NormalChatHandler;
import skype.utils.Consumer;
import skype.utils.Pair;
import skype.utils.Producer;
import skype.utils.timers.HourlyNonEditableMessageCleaner;
import skype.utils.users.UserInformation;


/**
 * 
 * The GroupChatListener class is responsible for receiving messages from the
 * specific {@link GroupChatListener#group} and handling them properly. Messages can
 * be split up in two main categories
 * <p>
 * <ul>
 * <li>{@link skype.commands.Command Commands}
 * <li>Normal messages
 * </ul>
 * <p>
 * This class works with producer-consumer design. The two main methods of this class
 * {@link #chatMessageReceived(ChatMessage)} and
 * {@link #chatMessageSent(ChatMessage)} which also are working like producers. They
 * put the received messages in a {@link #list} and then notify the consumer(s) to
 * wake up and process the messages. The handle process happens inside the consumer
 * and not here.
 * <p>
 * Also this class is responsible for adding edit listener. In order to add an edit
 * listener for a chat that you have already added a <code>GroupChatListener</code>
 * use the
 * 
 * <pre>
 * <code>
 * Skype.addChatMessageListener({@link GroupChatListener#getInstance()})
 * instead of 
 * Skype.addChatMessageListener(new EditListener())
 * </code>
 * </pre>
 * <p>
 * Despite class name this listener will work also for chats with two persons. We
 * just handle them like groups.
 * 
 * @see GroupChatEditListener
 * @see CommandHandler
 * @see NormalChatHandler
 * @see Consumer
 * @author Thanasis Argyroudis
 * @since 1.0
 */
public class GroupChatListener implements ChatMessageListener{

	/** The group to listen to. */
	private final Chat group;
	
	/**
	 * Extra informations for the users of the group. <User's Id, User's
	 * Informations>
	 */
	private ConcurrentHashMap<String, UserInformation> users = null;
	
	/** The handler for message edits. */
	private GroupChatEditListener editListener = null;

	/** The member manager. Listens if someone enters or leaves the chat. */
	@SuppressWarnings("unused")
	private final GroupChatMemberManager memberManager;

	/**
	 * The list for producer-consumer. This list contains one pair of: The message
	 * that came and the current systems time. For more information why we use
	 * current system's time and not skype time is because skype has low time
	 * resolution.
	 * <p>
	 * The list works as an buffer to keep all messages that came until consumers can
	 * process them.
	 */
	private final LinkedList<Pair<ChatMessage, Long>> list;

	/** The consumer thread for processing messages */
	private Consumer consumer;
	/** The producer thread for adding messages */
	private Producer producer;

	/**
	 * Keep last messages here. Mainly used to keep track of editable messages. If a
	 * message stops being editable it will be deleted. It needs to be concurrent
	 * because different threads are adding and removing messages.
	 */
	private final ConcurrentHashMap<ChatMessage, String> messages = new ConcurrentHashMap<ChatMessage, String>(30);

	/** The hourly message clean. */
	private final HourlyNonEditableMessageCleaner hourlyMessageClean = new HourlyNonEditableMessageCleaner(messages);

	/**
	 * Instantiates a new group listener. Registers the handlers and creates the
	 * information classes for the users of chat.
	 *
	 * @param groupChat
	 *            The group chat for which we added the listener
	 */
	public GroupChatListener( Chat groupChat ){
		group = groupChat;

		initiateUserInformations();
		memberManager = new GroupChatMemberManager(users);

		list = new LinkedList<Pair<ChatMessage, Long>>();
		initiateProducerConsumerThreads();

		hourlyMessageClean.startTimer();
	}
	
	/**
	 * @see com.skype.ChatMessageListener#chatMessageReceived(com.skype.ChatMessage)
	 */
	@Override
	public void chatMessageReceived(ChatMessage rec) throws SkypeException {
		if (!rec.getChat().equals(group))
			return;
		producer.addMessageAndNotifyConsumer(rec);
	}

	/**
	 * @see com.skype.ChatMessageListener#chatMessageSent(com.skype.ChatMessage)
	 */
	@Override
	public void chatMessageSent(ChatMessage sent) throws SkypeException {
		if (!sent.getChat().equals(group))
			return;
		producer.addMessageAndNotifyConsumer(sent);
	}

	/**
	 * Gets the group chat edit listener instance for the group which this
	 * GroupChatListener is responsible for.
	 *
	 * @return GroupChatEditListener instance.
	 */
	public synchronized GroupChatEditListener getEditListener() {
		if (editListener == null)
			editListener = new GroupChatEditListener(group, messages);
		return editListener;
	}

	/**
	 * Initiates each user's informations.
	 */
	private void initiateUserInformations() {
		try {

			users = new ConcurrentHashMap<String, UserInformation>(
					group.getAllMembers().length + 1);

			for (User u : group.getAllMembers()) {
				users.put(u.getId(), new UserInformation(u));
			}

		} catch (SkypeException e) {
			new WarningPopup(e.getMessage());
		}
	}

	/**
	 * Initiate and starts consumer thread at max priority.
	 */
	private void initiateProducerConsumerThreads() {
		consumer = new Consumer(list, users, messages);
		consumer.setPriority(Thread.MAX_PRIORITY);
		consumer.start();

		producer = new Producer(list);
		producer.setPriority(Thread.MAX_PRIORITY);
		producer.start();
	}

}
