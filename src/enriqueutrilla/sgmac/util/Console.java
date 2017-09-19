package enriqueutrilla.sgmac.util;

/**
 * Copyright 2017 Enrique Utrilla Molina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Console.java
 * Simple utility for logging through the standard output.
 */

public class Console {
	
	private static boolean enabled = true;
	private static boolean addTimestamp = true;
	
	public static boolean isEnabled(){
		return enabled;
	}
	
	public static void setEnabled(boolean enabled){
		Console.enabled = enabled;
	}

	public static boolean isAddTimestamp() {
		return addTimestamp;
	}

	public static void setAddTimestamp(boolean addTimestamp) {
		Console.addTimestamp = addTimestamp;
	}

	public static void log(String msg){
		if (enabled){
			if (addTimestamp){
				msg = System.currentTimeMillis() + ": " + msg;
			}
			System.out.println(msg);
		}
	}

}
