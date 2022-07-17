/*
 * (c) copyright Bernd Wengenroth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bw.jtools.ui;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Fext Input Field with autocompletion and optional character restriction.
 * <br>
 * Based on concepts from Sun's demo "TextAreaDemo".
 */
public class JAutoCompleteTextField extends JTextField
{

	protected static final String ENTER_ACTION = "enter";
	protected static final String TAB_ACTION = "tab";
	protected static final String DOWN_ACTION = "down";
	protected static final String UP_ACTION = "up";

	protected boolean selectionCommitted = true;
	protected int minLength = 2;
	protected AutoCompletionModel autocompletionModel;

	protected List<String> lastMatch_;
	protected int lastMatchIndex_;
	protected int lastMatchTextPosition_;

	/**
	 * Model to handle completions.
	 */
	public static interface AutoCompletionModel
	{
		/**
		 * Returns a list of completions for the given prefix.
		 * @param prefix The prefix to search.
		 * @return The completions or empty list if nothing was found.
		 */
		public List<String> match( String prefix );

		/**
		 * Checks if the character is a word character of the model.
		 * @param c The character to check for.
		 * @return true if a word-character, false if separator.
		 */
		public boolean isWordCharacter( char c );

		/**
		 * Get the text that should be added after a committed auto-completion.<br>
		 * @return The text or null.
		 */
		public String getText2AddAfterCommit();
	}

	/**
	 * Default implementation of AutocompletionModel that use a fixed list of proposals.
	 */
	public static class AutocompletionDefaultModel implements AutoCompletionModel
	{
		protected final List<String> words;
		protected final List<String> values;
		protected final boolean caseSensitive;

		/**
		 * Creates a default model.<br>
		 * <ul>
		 *     <li>All characters that are not white-spaces are considered word-characters.</li>
		 *     <li>Text after commit is a blank.</li>
		 * </ul>
		 * @param proposals The objects to be used as proposals.
		 * @param caseSensitive IF the model should complete case-sensitive or not.
		 */
		public AutocompletionDefaultModel(Collection<? extends Object> proposals, boolean caseSensitive )
		{
			this.caseSensitive = caseSensitive;
			words = new ArrayList<>(proposals.size());
			HashMap<String,String> valueMap;
			if ( caseSensitive )
				valueMap = null;
			else
				valueMap = new HashMap();


			for (Object w : proposals)
			{
				if ( w != null )
				{
					String v = w.toString();
					if ( !caseSensitive )
					{
						final String o = v;
						v = v.toUpperCase();
						valueMap.put( v, o );
					}
					words.add(v);
				}
			}
			words.sort(String::compareTo);
			if ( valueMap == null )
			{
				values = words;
			}
			else
			{
				values = new ArrayList<>(words.size());
				for ( String w : words)
					values.add( valueMap.get(w));
			}
		}

		@Override
		public String getText2AddAfterCommit()
		{
			return " ";
		}

		@Override
		public boolean isWordCharacter( char c )
		{
			return !Character.isWhitespace(c);
		}

	    @Override
		public List<String> match( String prefix )
		{
			List<String> list = new ArrayList();
			if ( !caseSensitive) prefix = prefix.toUpperCase();
			int n = Collections.binarySearch(words, prefix);
			if (n < 0)
			{
				int i = -n-1;
				while ( i < words.size() )
				{
					String match = words.get(i);
					if (match.startsWith(prefix))
						list.add(values.get(i++));
					else
						break;
				}
			}
			return list;
		}
	}


	/**
	 * Creates a default autocompletion field that use the string representations
	 * of the given objects as proposals. Auto-completion will be applies to every word
	 * separately.<br>
	 * Dynamic character-restrictions can be applied by a {@link javax.swing.text.DocumentFilter}
	 * is needed. E.g. for static restrictions see {@link AllowedCharacterDocumentFilter}.<br>
	 * As this c'tor will copy and sort the proposals please use {} for huge amount of proposals.
	 * @param proposals The objects to be used as proposals.
	 */
	public JAutoCompleteTextField(Collection<? extends Object> proposals, boolean caseSensitive)
	{
		this( new AutocompletionDefaultModel(proposals, caseSensitive));
	}

	/**
	 * Creates an autocompletion field.
	 * Auto-completion will be applies to every word separately.<br>
	 * @param autocompletionModel The model to use.
	 */
	public JAutoCompleteTextField(AutoCompletionModel autocompletionModel )
	{
		this.autocompletionModel = autocompletionModel;

		addCaretListener( caretEvent ->
		{
			// Remind this event will occure also during update of the completion selection!
			if ( !selectionCommitted )
			{
				if ( caretEvent.getDot() != lastMatchTextPosition_ || caretEvent.getMark() != lastMatch_.get(lastMatchIndex_).length())
				{
					selectionCommitted = true;
				}
			}

		});

		getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent ev)
			{
				if (ev.getLength() == 1)
				{
					final int pos = ev.getOffset();
					String content;
					try
					{
						content = getText(0, pos + 1);
					}
					catch (BadLocationException e)
					{
						return;
					}

					int w;
					for (w = pos; w >= 0; w--)
					{
						if (!autocompletionModel.isWordCharacter(content.charAt(w)))
						{
							break;
						}
					}
					if ((pos - w) >= minLength)
					{
						lastMatch_ = autocompletionModel.match(content.substring(w + 1));
						lastMatchIndex_ = 0;
						lastMatchTextPosition_ = pos + 1;
						if (!lastMatch_.isEmpty())
						{
							final String completion = lastMatch_.get(0).substring(pos - w);
							SwingUtilities.invokeLater( () -> {
								try {
									getDocument().insertString(lastMatchTextPosition_, completion, null);
									setCaretPosition(lastMatchTextPosition_ + completion.length());
								}
								catch (Exception e) {
								}
								moveCaretPosition(lastMatchTextPosition_);
								// Set flag after caret operation to correct caret events.
								selectionCommitted = false;
							});
						}
						else
							selectionCommitted = true;
					}
					else
						selectionCommitted = true;
					setFocusTraversalKeysEnabled(selectionCommitted);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e){}

			@Override
			public void changedUpdate(DocumentEvent e){}
		});

		InputMap im = getInputMap();
		ActionMap am = getActionMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), TAB_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), DOWN_ACTION);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), UP_ACTION);
		am.put(ENTER_ACTION, new CommitAction(true));
		am.put(TAB_ACTION, new CommitAction(false));
		am.put(DOWN_ACTION, new TroggleCompletion(1));
		am.put(UP_ACTION, new TroggleCompletion(-1));
	}

	/**
	 * Sets the minimal length of the words for which autocompletion is tried.
	 * Default is 2.
	 * @param length The length.
	 */
	public void setMinAutocompletionLength( int length )
	{
		minLength = length;
	}

	/**
	 * Gets the minimal length of the words for which autocompletion is tried.
	 */
	public int getMinAutocompletionLength( )
	{
		return  minLength;
	}

	private class TroggleCompletion extends AbstractAction
	{
		final int offset_;

		public TroggleCompletion(int offset)
		{
			offset_ = offset;
		}

		public void actionPerformed(ActionEvent ev)
		{
			if ( !selectionCommitted)
			{
				int nextMatchPos = (lastMatchIndex_ + offset_);
				if (lastMatch_ != null && nextMatchPos >= 0 && nextMatchPos < lastMatch_.size())
				{
					SwingUtilities.invokeLater(() ->
					{
						try
						{

							String prevCompletion = lastMatch_.get(lastMatchIndex_).substring(lastMatchTextPosition_);
							String nextCompletion = lastMatch_.get(nextMatchPos).substring(lastMatchTextPosition_);
							((AbstractDocument)getDocument()).replace(lastMatchTextPosition_, prevCompletion.length(),
									nextCompletion,null);

							lastMatchIndex_ = nextMatchPos;

							setCaretPosition(lastMatchTextPosition_ + nextCompletion.length());
						}
						catch (Exception e)
						{
						}
						moveCaretPosition(lastMatchTextPosition_);
						// Set flag to correct caret events.
						selectionCommitted = false;
					});
				}
			}
		}

	}

	private class CommitAction extends AbstractAction
	{
		boolean redirect_;

		CommitAction( boolean redirectAction )
		{
			redirect_ = redirectAction;
		}

		public void actionPerformed(ActionEvent ev)
		{
			if (selectionCommitted)
			{
				if ( redirect_ )
					postActionEvent();
			}
			else
			{
				try
				{
					int pos = getSelectionEnd();
					String toAdd = autocompletionModel.getText2AddAfterCommit();
					if ( toAdd != null )
					{
						getDocument().insertString(pos, toAdd, null);
						pos += toAdd.length();
					}
					setCaretPosition(pos);
				}
				catch (Exception e)
				{}
				selectionCommitted = true;
				setFocusTraversalKeysEnabled(selectionCommitted);
			}
		}
	}

}