package de.ahoehma.jdt.quickfix;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.correction.proposals.CUCorrectionProposal;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.ISharedImages;

/**
 * @author Andreas Höhmann <andreas.hoehmann@gmx.de>
 * @since 1.0.0
 */
public class DeleteNodeCorrectionProposal extends CUCorrectionProposal {

	private final int fOffset;
	private final int fLength;

	public DeleteNodeCorrectionProposal(final String name,
			final ICompilationUnit cu, final int offset, final int length,
			final int relevance) {
		super(name, cu, relevance, JavaPlugin.getDefault().getWorkbench()
				.getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		fOffset = offset;
		fLength = length;
	}

	@Override
	protected void addEdits(final IDocument doc, final TextEdit root)
			throws CoreException {
		// build a full AST
		final CompilationUnit unit = SharedASTProvider.getAST(
				getCompilationUnit(), SharedASTProvider.WAIT_YES, null);
		final ASTNode name = NodeFinder.perform(unit, fOffset, fLength);
		if (name instanceof SimpleName) {
			final SimpleName[] names = LinkedNodeFinder.findByProblems(unit,
					(SimpleName) name);
			if (names != null) {
				for (int i = 0; i < names.length; i++) {
					final SimpleName curr = names[i];
					final ASTNode parent = curr.getParent();
					root.addChild(new DeleteEdit(parent.getStartPosition(),
							parent.getLength()));
				}
				return;
			}
		}
		root.addChild(new DeleteEdit(fOffset, fLength));
	}
}
