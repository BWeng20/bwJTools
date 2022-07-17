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
package com.bw.jtools.reports.html;

import com.bw.jtools.reports.ReportRenderer;
import com.bw.jtools.reports.TextOptions;

public class HtmlRenderer extends ReportRenderer
{
	StringBuilder sb = new StringBuilder(10000);
	int reloadTimeS = 0;
	boolean collapsableLists = false;

	/**
	 * Sets the auto-reload option.
	 * A value of 0 disables reload.
	 *
	 * @param seconds Time in seconds, 0 to disable.
	 */
	public void setReloadTime(int seconds)
	{
		reloadTimeS = seconds;
	}

	/**
	 * Enabled support of collapsible lists.
	 *
	 * @param collapsible True: enable: False: disable
	 */
	public void enableCollapsiblesLists(boolean collapsible)
	{
		collapsableLists = collapsible;

	}

	public void addEscaped(String text)
	{
		// Only relevant codes, we don't want to be fully html compliant .
		char[] data = text.toCharArray();
		for (char c : data)
		{
			switch (c)
			{
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\'':
					sb.append("&apos;");
					break;
				case '\n':
					sb.append("<br>");
					break;
				default:
					sb.append(c);
			}
		}
	}

	@Override
	public void startParagraph()
	{
		sb.append("<p>");
	}

	@Override
	public void endParagraph()
	{
		sb.append("</p>");
	}

	@Override
	public void addText(String text)
	{
		addEscaped(text);
	}

	@Override
	public void addText(String text, TextOptions options)
	{
		if (options.bold) sb.append("<b>");
		if (options.italic) sb.append("<i>");

		addText(text);
		if (options.italic) sb.append("</i>");
		if (options.bold) sb.append("</b>");
	}


	@Override
	public void startTable()
	{
		sb.append("<table>");
	}

	@Override
	public void startRow()
	{
		sb.append("<tr>");
	}

	@Override
	public void startCell(int spans)
	{
		if (spans <= 1)
		{
			sb.append("<td>");
		}
		else
		{
			sb.append("<td colspan=\"");
			sb.append(Integer.toString(spans));
			sb.append("\">");
		}
	}

	@Override
	public void endCell()
	{
		sb.append("</td>");
	}

	@Override
	public void endRow()
	{
		sb.append("</tr>");
	}

	@Override
	public void endTable()
	{
		sb.append("</table>");
	}

	@Override
	public void startDocument(String title)
	{
		sb.setLength(0);
		if (title == null) title = "";
		sb.append("<html><head><meta charset=\"utf-8\">");
		if (reloadTimeS > 0)
		{
			sb.append("<meta http-equiv=\"refresh\" content=\"");
			sb.append(reloadTimeS);
			sb.append("\"/>");
		}
		sb.append("<title>");
		addEscaped(title);
		sb.append("</title>\n" +
				"<style>\n" +
				" table{width:100%;border:1px solid black;border-collapse:collapse;}\n" +
				" th,td{border:1px solid black;text-align:left;vertical-align:top;}\n" +
				" tr:hover{background-color:#f5f5f5;}\n");
		if (collapsableLists)
		{
			sb.append(" .clp { cursor: pointer; padding: 10px; }\n" +
					" .active,.clp:hover {background-color: #555;}\n");
		}
		sb.append("</style>\n");

		if (collapsableLists)
		{
			sb.append("<script>\n");
			sb.append(
					"function initClpsl() {\n" +
							"	var coll = document.getElementsByClassName(\"clpsl\");\n" +
							"	var i;\n" +
							"	for (i = 0; i < coll.length; i++) {\n" +
							"		coll[i].addEventListener(\"click\", function() {\n" +
							"		this.classList.toggle(\"active\");\n" +
							"		var content = this.nextElementSibling;\n" +
							"		if (content.style.display === \"block\") {\n" +
							"			this.innerHtml='+';\n" +
							"			content.style.display = \"none\";\n" +
							"		} else {\n" +
							"			this.innerHtml='-';\n" +
							"			content.style.display = \"block\";\n" +
							"		}\n" +
							"	});\n" +
							"} };\n");
			sb.append("</script>\n");

			sb.append("<body onload=\"initClpsl()\">");
		}
		else
		{
			sb.append("<body>");
		}
	}

	@Override
	public void endDocument()
	{
		sb.append("</body></html>");
	}

	@Override
	public String toString()
	{
		return sb.toString();
	}

	@Override
	public void startTableHead()
	{
		sb.append("<thead>");
	}

	@Override
	public void endTableHead()
	{
		sb.append("</thead>");
	}

	@Override
	public void startList()
	{
	}

	@Override
	public void startListHeader()
	{
		if (collapsableLists)
		{
			sb.append("<div class='clpsl'><span width='10px'>+</span>");
		}
		else
		{
			sb.append("<div>");
		}

	}

	@Override
	public void endListHeader()
	{
		sb.append("</div>");
	}

	@Override
	public void startListBody()
	{
		if (collapsableLists)
		{
			sb.append("<ul class='clpslContent'>");
		}
		else
		{
			sb.append("<ul>");
		}
	}

	@Override
	public void endListBody()
	{
		sb.append("</ul>");
	}

	@Override
	public void startListElement()
	{
		sb.append("<li>");
	}

	@Override
	public void endListElement()
	{
		sb.append("</li>");
	}

	@Override
	public void endList()
	{
		sb.append("</ul>");
	}

}
