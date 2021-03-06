// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyjs.util;

import java.io.File;
import java.io.IOException;
import wyc.Activator;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyjs.commands.JsCompile;

/**
 * This class implements an baseline ant task for compiling whiley files via ant
 * and an appropriate build.xml file. The following illustrates how this task
 * can be used in a build.xml file:
 *
 * <pre>
 * <taskdef name="wyjs" classname="wyjc.util.AntTask" classpath="lib/wyjc.jar"/>
 * <wyc whileydir="stdlib" includes="whiley\/**\/*.whiley" excludes="whiley/io/**"/>
 * </pre>
 *
 * <p>
 * The first line defines the new task, and requires <code>wyjc.jar</code> (which
 * contains this class) to be on the classpath; The second invokes the task to
 * compile all files rooted in the <code>stdlib/</code> directory which are in
 * the <code>whiley/</code> package, excluding those in <code>whiley/io</code>.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class AntTask extends wyc.util.AntTask {

	/**
	 * Construct a new instance of this command.
	 *
	 * @param registry
	 *            The content registry being used to match files to content
	 *            types.
	 * @throws IOException
	 */
	public AntTask() {
		Content.Registry registry = new Activator.Registry();
		this.command = new JsCompile(registry, Logger.NULL);
	}

	// =======================================================================
	// Configuration
	// =======================================================================

	public void setJsdir(File dir) throws IOException {
		((JsCompile)command).setJavascriptdir(dir);
	}
}
