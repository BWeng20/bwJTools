package com.bw.jtools.ui.vector.svg;

import java.awt.geom.AffineTransform;

public abstract class ElementBase
{

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		toSVG(sb);
		return sb.toString();
	}

	public abstract void toSVG(StringBuilder sb);

	protected void appendAttribute(StringBuilder sb, String attributeName, String value)
	{
		if (ElementWrapper.isNotEmpty(value))
		{
			// @TODO Escaping
			sb.append(' ')
			  .append(attributeName)
			  .append('=')
			  .append('"')
			  .append(value)
			  .append('"');
		}
	}

	protected void appendAttribute(StringBuilder sb, String attributeName, Number value)
	{
		if (value != null)
		{
			sb.append(' ')
			  .append(attributeName)
			  .append('=')
			  .append('"')
			  .append(value)
			  .append('"');
		}
	}

	protected void appendTransform(StringBuilder sb, String attributeName, AffineTransform aft)
	{
		if (aft != null)
		{
			sb.append(' ')
			  .append(attributeName)
			  .append("=\"matrix(");
			double matrix[] = new double[6];
			aft.getMatrix(matrix);
			sb.append(matrix[0])
			  .append(' ');
			sb.append(matrix[1])
			  .append(' ');
			sb.append(matrix[2])
			  .append(' ');
			sb.append(matrix[3])
			  .append(' ');
			sb.append(matrix[4])
			  .append(' ');
			sb.append(matrix[5])
			  .append(")\"");
		}
	}

}
