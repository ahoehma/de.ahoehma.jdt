/*
 * Copyright (c) 2011 Andreas Höhmann <andreas.hoehmann@gmx.de>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.ahoehma.jdt.quickfix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;

/**
 * @author Andreas Höhmann <andreas.hoehmann@gmx.de>
 * @since 1.0.0
 */
public class QuickFixRemoveStatement implements IQuickFixProcessor {

	public static void getMethodProposals(final IInvocationContext context,
			final IProblemLocation problem,
			final boolean isOnlyParameterMismatch,
			final Collection<IJavaCompletionProposal> proposals)
			throws CoreException {
		final ICompilationUnit cu = context.getCompilationUnit();
		final CompilationUnit astRoot = context.getASTRoot();
		final ASTNode selectedNode = problem.getCoveringNode(astRoot);
		if (!(selectedNode instanceof SimpleName)) {
			return;
		}
		final SimpleName nameNode = (SimpleName) selectedNode;
		final DeleteNodeCorrectionProposal removeProposal = new DeleteNodeCorrectionProposal(
				"delete", cu, nameNode.getStartPosition(),
				nameNode.getLength(), 0);
		proposals.add(removeProposal);
	}

	@Override
	public IJavaCompletionProposal[] getCorrections(
			final IInvocationContext context, final IProblemLocation[] locations)
			throws CoreException {
		final HashSet<Integer> handledProblems = new HashSet<Integer>(
				locations.length);
		final ArrayList<IJavaCompletionProposal> resultingCollections = new ArrayList<IJavaCompletionProposal>();
		System.out.println(locations.length);
		for (int i = 0; i < locations.length; i++) {
			final IProblemLocation curr = locations[i];
			final Integer id = new Integer(curr.getProblemId());
			if (handledProblems.add(id)) {
				System.out.println(i + ":" + id);
				process(context, curr, resultingCollections);
			}
		}
		return resultingCollections
				.toArray(new IJavaCompletionProposal[resultingCollections
						.size()]);
	}

	@Override
	public boolean hasCorrections(final ICompilationUnit unit,
			final int problemId) {
		return IProblem.UndefinedMethod == problemId;
	}

	private void process(final IInvocationContext context,
			final IProblemLocation problem,
			final ArrayList<IJavaCompletionProposal> proposals)
			throws CoreException {
		final int id = problem.getProblemId();
		if (id == 0) { // no proposals for none-problem locations
			return;
		}
		switch (id) {
		case IProblem.UndefinedMethod:
			getMethodProposals(context, problem, false, proposals);
			break;
		}
	}
}
