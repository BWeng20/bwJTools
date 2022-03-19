package com.bw.jtools.examples.pathchooser;

import com.bw.jtools.Log;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.filechooserpreview.JFileChooserPreview;
import com.bw.jtools.ui.pathchooser.JPathChooser;
import com.bw.jtools.ui.pathchooser.PathChooserMode;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public class JPathChooserDemo
{
	public static final String[][] DESCRIPTION =
	{
			{ "en", "Demonstrates usage of <b>com.bw.jtools.ui.pathchooser.JPathChooser</b>." },
			{ "de", "Demonstriert die Verwendung von <b>com.bw.jtools.ui.pathchooser.JPathChooser</b>." }
	};


	/**
	 * To run this class from IDE, configure the run configuration to use the module root class path,
	 * otherwise the module imports are not recognized and some "IllegalAccess" Exception may occur.
	 */
	public static void main(String[] args)
	{
		try
		{
			// UIManager.setLookAndFeel(  UIManager.getCrossPlatformLookAndFeelClassName() );
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		Log.setLevel(Log.DEBUG);
		JPathChooser d = new JPathChooser();

		JCheckBox large = new JCheckBox("Large Icons");
		large.setSelected(d.isUseLargeIcons());
		large.addItemListener(e -> d.setUseLargeIcons(large.isSelected()));

		// Adding some additional buttons, usefull for tests.
		d.addButton(large);
		d.addButton(new JLAFComboBox());

		// Try to locate the jar we are running from and add it as file-system.
		FileSystem jarFS = IOTool.getFileSystemForClass(JPathChooser.class);
		if (jarFS != null)
		{
			d.addFileSystem(jarFS, "Application", null);
		}

		// Add jdk file-system. Since java 9 jrt file system should be used here.
		FileSystem swingFS = IOTool.getFileSystemForClass(JComponent.class);
		if (swingFS != null)
		{
			d.addFileSystem(swingFS, "Jdk", null);
		}

		d.setFileSelectionMode(PathChooserMode.FILES_ONLY);
		final JCheckBox files = new JCheckBox("Files");
		final JCheckBox folders = new JCheckBox("Folders");

		files.setSelected( true );
		folders.setSelected( true );

		files.addItemListener(e ->
		{
			boolean filesChecked = files.isSelected();
			boolean foldersChecked = folders.isSelected();
			if ( !(filesChecked || foldersChecked ) )
			{
				SwingUtilities.invokeLater( () -> { folders.setSelected(true); } );
			}
			else
			{
				d.setFileSelectionMode(
						filesChecked
								? foldersChecked ? PathChooserMode.FILES_AND_FOLDERS : PathChooserMode.FILES_ONLY
								: PathChooserMode.FOLDERS );
			}
		});

		folders.addItemListener(e ->
		{
			boolean filesChecked = files.isSelected();
			boolean foldersChecked = folders.isSelected();
			if ( !(filesChecked || foldersChecked ) )
			{
				SwingUtilities.invokeLater( () -> { files.setSelected(true); } );
			}
			else
			{
				d.setFileSelectionMode(
						filesChecked
								? foldersChecked ? PathChooserMode.FILES_AND_FOLDERS : PathChooserMode.FILES_ONLY
								: PathChooserMode.FOLDERS );
			}
		});

		d.addButton(files);
		d.addButton(folders);

		JFileChooserPreview preview = new JFileChooserPreview(300, "Preview", 5, 5, JFileChooserPreview.defaultHandlers());
		preview.install(d);

		d.setDirectory(IOTool.getPath(System.getProperty("user.home")));
		d.setFileSelectionMode(PathChooserMode.FILES_AND_FOLDERS);
		d.showDialog(null, "Select Some Folder or File", "Select");


		Path p = d.getSelectedPath();

		if (p != null)
		{
			System.out.println(p.toUri());
		}
	}
}
