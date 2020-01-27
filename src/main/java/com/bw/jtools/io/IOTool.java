/*
 * (c) copyright 2015-2019 Bernd Wengenroth
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
package com.bw.jtools.io;

import com.bw.jtools.Log;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.bw.jtools.persistence.Store;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.IconCache;
import com.bw.jtools.ui.WaitSplash;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Collection of tool methods for IO handling.
 */
public final class IOTool
{

    static private ExecutorService io_executor_;

    /**
     * Executes a job inside a worker thread from the Executer thread-pool.
     * @param r Runnable to invoke.
     */
    public static synchronized void executeIOTask(Runnable r)
    {
        if (io_executor_ == null)
        {
            io_executor_ = Executors.newCachedThreadPool();
        }
        io_executor_.execute(r);
    }

    /**
     * Creates a text-writer for some path/URI.
     * @see #getPath
     * @see #createTextWriter(java.nio.file.Path)
     *
     * @param file The path.
     * @return The Writer.
     * @throws IOException if something goes wrong.
     */
    public static Writer createTextWriter(String file) throws IOException
    {
        return createTextWriter(getPath(file));
    }

    /**
     * Creates a text-writer for a path.
     *
     * @param file The path.
     * @return The Writer.
     * @throws IOException if something goes wrong.
     */
    public static Writer createTextWriter(Path file) throws IOException
    {
        ensureDirectoriesForFile(file);
        return Files.newBufferedWriter(file, StandardCharsets.UTF_8);
    }

    /**
     * Exception-safe version from {@link File#getCanonicalFile()}.
     * @param file The source file.
     * @return The canonical file.
     */
    public static File getCanonicalFile(File file)
    {
        try
        {
            return file.getCanonicalFile();
        } catch (Exception e)
        {
            Log.error("Can't resolve path " + file.getPath(), e);
            return null;
        }
    }

    /**
     * Sets System property "user.dir".
     * @param path The new path.
     */
    public static void setWorkingDir(File path)
    {
        try
        {
            System.setProperty("user.dir", path.getAbsolutePath());
        } catch (Exception e)
        {
        }
    }

    /**
     * Get the absolute File for some relative path.
     * @see #setWorkingDir(java.io.File)
     * @see File#getAbsolutePath()
     * @param relativePath The relative path.
     * @return The absolute path.
     */
    public static File makeAbsolute(String relativePath)
    {
        return new File(relativePath).getAbsoluteFile();
    }

    /**
     * Gets a relative version of the absolute path.
     * @param path The path to make relative.
     * @return  The relative path.
     */
    public static String makeRelative(Path path)
    {
        try
        {
            return makeRelative(path.toFile());
        } catch (Exception e)
        {
        }
        return path.toUri().toString();
    }

    /**
     * Gets a relative version of the absolute path.<br>
     * The path is made relative to the current working directory
     * that is stored in system property "user.dir".
     * @param path The path to make relative.
     * @return The relative path.
     */
    public static String makeRelative(File path)
    {
        if (path.isAbsolute())
        {
            String absPath = path.getAbsolutePath();
            File common_dir = new File(System.getProperty("user.dir"));

            StringBuilder relPath = new StringBuilder();

            while (!absPath.startsWith(common_dir.getAbsolutePath()))
            {
                common_dir = common_dir.getParentFile();
                relPath.append("..");
                relPath.append(File.separator);
                if (common_dir == null)
                {
                    return absPath;
                }
            }

            absPath = absPath.substring(common_dir.getAbsolutePath().length());
            if (absPath.startsWith(File.separator))
            {
                relPath.append(absPath.substring(File.separator.length()));
            } else
            {
                relPath.append(absPath);
            }

            return relPath.toString();
        } else
        {
            return path.getPath();
        }
    }

    /**
     * Ensure existence of all parent-directories for the specified file.
     * @param file The fire for all parent directories shall be checked.
     * @throws java.io.IOException Throws in case of errors.
     */
    public static void ensureDirectoriesForFile(Path file) throws IOException
    {
        if (file != null)
        {
            Path parent = file.getParent();
            if (parent != null)
            {
                if (!Files.exists(parent))
                {
                    Files.createDirectories(parent);
                    Log.info("Created " + parent);
                }
            }
        }
    }

    /**
     * Helper to sets the current directory of a file-chooser.<br>
     * Mainly here to work around exception on some platforms.
     * @param fileChooser The file-chooser to configure.
     * @param dir The new start-directory.
     */
    public static void setFileChooserDirectory(JFileChooser fileChooser, File dir)
    {
        try
        {
            // Try to reset the working directory before setting it
            fileChooser.setCurrentDirectory(null);
            fileChooser.setCurrentDirectory(dir);
        }
        catch (Throwable t)
        {
            // Some platforms may throw "ArrayOutOfBounds".
            try
            {
                fileChooser.setCurrentDirectory(dir);
            }
            catch (Throwable t2)
            {
            }
        }
    }

    private static JFileChooser fileChooser_ = null;
    private static Frame fileChooserFrame_ = null;

    private static FileNameExtensionFilter filterJson;

    /**
     * Predefined file filter for json files with extension json or js.
     * @return The filter.
     */
    public static FileFilter getFileFilterJson()
    {
        if ( filterJson == null )
        {
            filterJson = new FileNameExtensionFilter( I18N.getText( "filefilter.json" ), "json", "js");
        }
        return filterJson;
    }

    private static FileNameExtensionFilter filterLog;

    /**
     * Predefined file filter for log files with extension log or txt.
     * @return The filter.
     */
    public static FileFilter getFileFilterLog()
    {
        if ( filterLog == null )
        {
            filterLog = new FileNameExtensionFilter( I18N.getText( "filefilter.log" ), "log", "txt");
        }
        return filterLog;
    }

    private static FileFilter filterAll;

    private static class AllFileFilter extends FileFilter
    {

        final String description = I18N.getText( "filefilter.all" );

        @Override
        public boolean accept(File f)
        {
            return true;
        }

        @Override
        public String getDescription()
        {
            return description;
        }
    }


    /**
     * Predefined file filter for all files.
     */
    public static FileFilter getFileFilterAll()
    {
        if ( filterAll == null )
        {
            filterAll = new AllFileFilter();
        }
        return filterAll;
    }


    /**
     * Mode for selecting files or directories to write or create.
     */
    public static final int SAVE = 1;

    /**
     * Mode for selecting files or directories to read.
     */
    public static final int OPEN = 2;

    private static Component prepareFileChooser(Component comp)
    {
        if (comp == null)
        {
            if (null == fileChooserFrame_)
            {
                fileChooserFrame_ = new Frame();
                fileChooserFrame_.setIconImage(IconCache.getAppSmallImage());
            }
            comp = fileChooserFrame_;
        }

        if (null == fileChooser_)
        {
            fileChooser_ = new JFileChooser();
        }
        return comp;
    }

    /**
     * Select a directory.
     *
     * @param comp The calling component. Used to give the dialog a parent.
     * @param prefPrefix Preference-prefix to store the last used directory and
     * file. E.g. "MyApp.OpenFile". Can be null.
     * @param dialogTitle Title of the dialog.
     * @param mode Mode of operation. Possible values are {@link OPEN} and
     * {@link SAVE}.
     * @return Null or the selected directory.
     */
    public static File selectDirectory(Component comp, String prefPrefix, String dialogTitle, int mode)
    {
        comp = prepareFileChooser(comp);

        String dirPath = Store.getString(prefPrefix + ".dir", null);
        File dir = (dirPath != null && !dirPath.isEmpty()) ? new File(dirPath) : null;
        fileChooser_.setDialogTitle(dialogTitle);
        fileChooser_.setMultiSelectionEnabled(false);
        fileChooser_.setFileHidingEnabled(true);
        fileChooser_.setFileFilter(null);
        fileChooser_.setAcceptAllFileFilterUsed(true);
        fileChooser_.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        setFileChooserDirectory(fileChooser_, dir);
        int response = (mode == OPEN) ? fileChooser_.showOpenDialog(comp) : fileChooser_.showSaveDialog(comp);
        if (response == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser_.getSelectedFile().getAbsoluteFile();
            Store.setString(prefPrefix + ".dir", file.getParent());
            return file;
        }
        return null;
    }

    /**
     * Select one file.
     *
     * @param comp The calling component. Used to give the dialog a parent.
     * @param prefPrefix Preference-prefix to store the last used directory and
     * file. E.g. "MyApp.OpenFile". Can be null.
     * @param dialogTitle Title of the dialog.
     * @param mode Mode of operation. Possible values are {@link OPEN} and
     * {@link SAVE}.
     * @param filter File filters to use. Null values force to add the "all" filter.
     * @return Null or the selected file.
     */
    public static File selectFile(Component comp, String prefPrefix, String dialogTitle, int mode, FileFilter... filter)
    {
        final File f[] = internal_selectFiles(comp, prefPrefix, dialogTitle, mode, filter, false);
        if (f != null && f.length > 0)
        {
            return f[0];
        } else
        {
            return null;
        }
    }

    /**
     * Select multiple files.
     *
     * @param comp The calling component. Used to give the dialog a parent.
     * @param prefPrefix Preference-prefix to store the last used directory.
     * E.g. "MyApp.OpenFile". Can be null.
     * @param dialogTitle Title of the dialog.
     * @param mode Mode of operation. Possible values are {@link OPEN} and
     * {@link SAVE}.
     * @param filter File filters to use. Null values force to add a "all" filter.
     * @return Null or a none-empty array.
     */
    public static File[] selectFiles(Component comp, String prefPrefix, String dialogTitle, int mode, FileFilter... filter)
    {
        return internal_selectFiles(comp, prefPrefix, dialogTitle, mode, filter, true);
    }

    private static File[] internal_selectFiles(Component comp, String prefPrefix, String dialogTitle, int mode, FileFilter[] filter, boolean multiSelectionAllowed)
    {
        comp = prepareFileChooser(comp);

        if (mode == SAVE)
        {
            multiSelectionAllowed = false;
        }

        fileChooser_.setDialogTitle(dialogTitle);
        fileChooser_.setMultiSelectionEnabled(multiSelectionAllowed);
        fileChooser_.setFileHidingEnabled(true);
        fileChooser_.resetChoosableFileFilters();
        fileChooser_.setAcceptAllFileFilterUsed(false);

        boolean firstFilter = true;
        for ( FileFilter f : filter )
        {
            if ( f == null )
            {
                f = getFileFilterAll();
            }
            if ( firstFilter )
            {
                fileChooser_.setFileFilter(f);
                firstFilter = false;
            }
            else
            {
                fileChooser_.addChoosableFileFilter(f);
            }
        }

        String lastDir;
        String lastFile;
        if (prefPrefix != null)
        {
            lastDir = Store.getString(prefPrefix + ".dir", null);
            lastFile = multiSelectionAllowed ? null : Store.getString(prefPrefix + ".file", null);
        } else
        {
            lastDir = null;
            lastFile = null;
        }

        if (lastDir == null || lastFile == null)
        {
            fileChooser_.setSelectedFile(new File(""));
        } else
        {
            if (!lastDir.endsWith(File.separator))
            {
                lastDir = lastDir + File.separator;
            }
            File f = new File(lastDir + lastFile);
            fileChooser_.setSelectedFile(f);
        }
        fileChooser_.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (lastDir != null)
        {
            setFileChooserDirectory(fileChooser_, new File(lastDir));
        }

        // Ensure Splash is off
        WaitSplash.showWait(false);

        int response = (mode == OPEN) ? fileChooser_.showOpenDialog(comp) : fileChooser_.showSaveDialog(comp);
        if (response == JFileChooser.APPROVE_OPTION)
        {
            File files[] = null;

            if (mode == SAVE)
            {
                files = new File[1];
                files[0] = fileChooser_.getSelectedFile();
                String fileName = files[0].getName();
                int extIndex = fileName.lastIndexOf('.');
                if (extIndex < 0)
                {
                    // Try to construct a extention from a choosen FileFilter.
                    final FileFilter ff = fileChooser_.getFileFilter();
                    if (ff instanceof FileNameExtensionFilter)
                    {
                        FileNameExtensionFilter fnef = (FileNameExtensionFilter) ff;
                        final String ext[] = fnef.getExtensions();
                        if (ext != null && ext.length > 0)
                        {
                            files[0] = new File(files[0].getParent(), fileName + "." + ext[0]);
                        }
                    }
                }
            } else if (multiSelectionAllowed)
            {
                files = fileChooser_.getSelectedFiles();
                if (files != null && files.length == 0)
                {
                    files = null;
                }
            } else
            {
                files = new File[1];
                files[0] = fileChooser_.getSelectedFile();
            }

            if (files != null)
            {
                Store.setString(prefPrefix + ".dir", files[0].getParentFile().getAbsolutePath());
                if (!multiSelectionAllowed)
                {
                    Store.setString(prefPrefix + ".file", files[0].getName());
                }
            }
            return files;
        }
        return null;
    }

    /**
     * Guess a path for some file/uri.<br>
     * Main reason for this method is to enable access to JARs via Path-API.<br>
     * A URI inside a Jar looks as follows:<br>
     * jar:file:/app.jar!/com/mycompany/myapp/import.txt
     *
     * @param file The path or uri.
     * @return Matching path
     */
    public static Path getPath(String file)
    {
        try
        {
            if (file != null && false == file.isEmpty())
            {
                URI uri = URI.create(file);
                final String scheme = uri.getScheme();
                if (scheme != null)
                {
                    if (scheme.equalsIgnoreCase("file"))
                    {
                        return java.nio.file.Paths.get(uri);
                    }

                    if (scheme.equalsIgnoreCase("jar"))
                    {
                        FileSystem fs = null;
                        int si = file.indexOf("!");
                        String arc = file.substring(0, si);
                        try
                        {
                            URI fsuri = new URI(arc);
                            try
                            {
                                fs = FileSystems.getFileSystem(fsuri);
                            } catch (FileSystemNotFoundException fsnf)
                            {
                                fs = FileSystems.newFileSystem(fsuri, new HashMap<String, Object>());
                            }
                            return fs.getPath(file.substring(si + 1));
                        } catch (Exception ex2)
                        {
                            Log.error("Can't decode Jar URI: " + ex2.getMessage(), ex2);
                        }
                    }
                }
            } else
            {
                return null;
            }
        } catch (Throwable e)
        {
            //Log.log_error("URI Error:" + e.getMessage());
        }
        return FileSystems.getDefault().getPath(file);
    }

    /**
     * Creates an image from a swing component.The component have to be a valid
     * layout (valid size).
     *
     * @param comp The component from which we take a snapshot.
     * @return The generated image.
     */
    public static BufferedImage createImageFromComponent(JComponent comp)
    {
        return createImageFromComponent(comp, new Rectangle(comp.getSize()));
    }

    /**
     * Creates an image from a rectangle area of a swing component.The component
     * have to be a valid layout (valid size).
     *
     * @param comp The component from which we take a snapshot.
     * @param region The region for the snapshot.
     * @return The generated image.
     */
    public static BufferedImage createImageFromComponent(JComponent comp, Rectangle region)
    {
        BufferedImage image = new BufferedImage(region.width, region.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        if (false == comp.isOpaque())
        {
            g2d.setColor(comp.getBackground());
            g2d.fillRect(region.x, region.y, region.width, region.height);
        }

        g2d.translate(-region.x, -region.y);
        comp.print(g2d);
        g2d.dispose();
        return image;
    }

    /**
     * Creates an image from buffer. Returns null if data contains no image or
     * format is not supported by runtime.
     *
     * @param data The raw data.
     * @return The generated image.
     */
    public static BufferedImage createImageFromBuffer(byte[] data)
    {
        try
        {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            return image;
        } catch (Exception e)
        {
            Log.error("Failed to decode image: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets a length-restricted stack-trace.
     * @param t The Throwable.
     * @param prefix A Prefix to use for each line.
     * @param max_lines Maximum number of lines.
     * @return The multi-line stack-trace.
     */
    public static String getRestrictedStackTrace(Throwable t, String prefix, int max_lines)
    {
        StringWriter sw = new StringWriter(1000);
        t.printStackTrace(new PrintWriter(sw));

        String lines[] = sw.toString().split("\\r\\n|\\n|\\r");

        StringBuilder sb = new StringBuilder(2048);
        int l = 0;
        while (l < max_lines && l < lines.length)
        {
            sb.append(prefix).append(lines[l++]).append("\n");
        }
        if (l < lines.length)
        {
            sb.append(prefix).append("...\n");
        }
        return sb.toString();
    }

}
