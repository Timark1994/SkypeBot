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
package skype.utils.users;

import com.skype.Skype;
import com.skype.SkypeException;

import skype.gui.popups.ErrorPopup;


/**
 * The Class BotUserInfo. This class contains information about the user of the bot.
 * The user of the bot is also the administrator.
 *
 * @author Thanasis Argyroudis
 * @since 1.0
 */
public class BotUserInfo {

	static private String name;

	static private String id;

	static {
		try {
			name = Skype.getProfile().getFullName();
			id = Skype.getProfile().getId();
		} catch (SkypeException e) {
			new ErrorPopup(e.getMessage());
		}
	}

	/**
	 * No need instances. Only static access
	 */
	private BotUserInfo() {

	}

	public static String getBotUserName() {
		return name;
	}

	public static String getBotUserID() {
		return id;
	}

}
