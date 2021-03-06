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
package skype.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import skype.gui.popups.ErrorPopup;
import skype.gui.popups.WarningPopup;

/**
 * The Class FileStreamUtil. A simple utility for fast opening and closing an
 * {@link InputStream inputStream}.
 * 
 * @author Thanasis Argyroudis
 * @see InputStream
 * @see FileInputStream
 * @since 1.0
 */
public class FileStreamUtil {

	/**
	 * No Instantiates. Only static usage.
	 */
	private FileStreamUtil() {


	}

	/**
	 * Creates a <code>FileInputStream</code> from given path.
	 *
	 * @param path
	 *            the path
	 * @return the input stream. Null if an error occurred.
	 */
	public static InputStream fileAsInputStream(String path) {
		FileInputStream inputStream = null;

		try{
			inputStream = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			new ErrorPopup(e.getMessage());
			return null;
		} catch (SecurityException e) {
			new ErrorPopup(e.getMessage());
			return null;
		}
		return inputStream;

	}

	/**
	 * Creates a <code>FileInputStream</code> from given file. If you try to open a
	 *
	 * @param file
	 *            the file
	 * @return the input stream for file. Null if an error occurred.
	 */
	public static InputStream fileAsInputStream(File file) {
		FileInputStream inputStream = null;

		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			new ErrorPopup(e.getMessage());
			return null;
		} catch (SecurityException e) {
			new ErrorPopup(e.getMessage());
			return null;
		}
		return inputStream;

	}

	/**
	 * Closes the given input stream.
	 *
	 * @param stream
	 *            the stream
	 */
	public static void closeInputStream(InputStream stream) {
		try {
			stream.close();
		} catch (IOException e) {
			new WarningPopup(e.getMessage());
		}
	}

}
