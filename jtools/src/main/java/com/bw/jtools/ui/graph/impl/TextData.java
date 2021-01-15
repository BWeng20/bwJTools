package com.bw.jtools.ui.graph.impl;

import com.bw.jtools.graph.Data;

public class TextData implements Data
{
	private static final String EMPTY = "";

	public String text;

	public TextData() {
		text = EMPTY;
	}

	public TextData(String text) {
		this.text = text;
	}
}
