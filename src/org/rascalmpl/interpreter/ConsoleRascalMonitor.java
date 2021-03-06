/*******************************************************************************
 * Copyright (c) 2009-2013 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:

 *   * Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI
 *   * Anya Helene Bagge - anya@ii.uib.no (Univ. Bergen)
 *   * Arnold Lankamp - Arnold.Lankamp@cwi.nl
 *******************************************************************************/
package org.rascalmpl.interpreter;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.imp.pdb.facts.ISourceLocation;

public class ConsoleRascalMonitor implements IRascalMonitor {
	PrintWriter out;

	public ConsoleRascalMonitor() {
		this.out = new PrintWriter(System.err);
	}

	public ConsoleRascalMonitor(PrintStream out) {
		this.out = new PrintWriter(out);
	}

	@Override
	public int endJob(boolean succeeded) {
		return 0;
	}

	@Override
	public void event(String name) {
		//out.println(name);
		//out.flush();
	}

	@Override
	public void event(String name, int inc) {
		//out.println(name);
		//out.flush();
	}

	@Override
	public void event(int inc) {
	}

	@Override
	public void startJob(String name, int totalWork) {
		// System.err.println("Starting: " + name);
		out.println(name);
		out.flush();
	}

	@Override
	public void startJob(String name) {
		out.println(name);
		out.flush();
	}

	@Override
	public void startJob(String name, int workShare, int totalWork) {
		out.println(name);
		out.flush();
	}

	@Override
	public void todo(int work) {
	}

	@Override
	public boolean isCanceled() {
		return false;
	}

	@Override
	public void warning(String message, ISourceLocation src) {
		out.println("Warning: " + message);
		out.flush();
	}
}
