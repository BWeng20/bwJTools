package com.bw.jtools.ui;

import javax.swing.text.*;

/**
 * Editor kit to vertically center text inside an JTextPane.<br>
 * Horizontal justification can be done via attributes.<br>
 * Found at different locations in the internet and adapted.
 * Original authorship is unknown.
 */
public class CenterEditorKit extends StyledEditorKit
{
	/**
	 * Returns a view factory to create vertically centered views.
	 *
	 * @return the view factory.
	 */
	public ViewFactory getViewFactory()
	{
		return new CenterStyledViewFactory();
	}

	/**
	 * View to center text sections vertically.
	 */
	static class CenterStyledViewFactory implements ViewFactory
	{
		public View create(Element elem)
		{
			String kind = elem.getName();
			if (kind != null)
			{
				if (kind.equals(AbstractDocument.ContentElementName))
					return new LabelView(elem);
				else if (kind.equals(AbstractDocument.ParagraphElementName))
					return new ParagraphView(elem);
				else if (kind.equals(AbstractDocument.SectionElementName))
					return new CenteredBoxView(elem, View.Y_AXIS);
				else if (kind.equals(StyleConstants.ComponentElementName))
					return new ComponentView(elem);
				else if (kind.equals(StyleConstants.IconElementName))
					return new IconView(elem);
			}
			return new LabelView(elem);
		}

	}

	/**
	 * BoxView that centers vertically.
	 */
	final static class CenteredBoxView extends BoxView
	{
		public CenteredBoxView(Element elem, int axis)
		{
			super(elem, axis);
		}

		protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans)
		{
			super.layoutMajorAxis(targetSpan, axis, offsets, spans);
			int textBlockHeight = 0;
			int offset = 0;

			for (int i = 0; i < spans.length; i++)
			{
				textBlockHeight = spans[i];
			}
			offset = (targetSpan - textBlockHeight) / 2;
			for (int i = 0; i < offsets.length; i++)
			{
				offsets[i] += offset;
			}

		}
	}

}