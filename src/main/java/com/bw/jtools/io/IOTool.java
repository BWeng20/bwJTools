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
import com.bw.jtools.ui.IconCache;
import com.bw.jtools.ui.WaitSplash;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Collection of tool methods for IO handling.
 *
 */
public final class IOTool
{
   // Have to be set by application to provide a base for resources.
   static public Class<?> main_class_ = IOTool.class;

   static private ExecutorService io_executor_;

   public static synchronized void executeIOTask( Runnable r )
   {
      if ( io_executor_ == null )
         io_executor_ = Executors.newCachedThreadPool();
      io_executor_.execute(r);
   }

    /**
     * Creates a text-writer for some path/URI.
     * @param file The path.
     * @return The Writer.
     * @throws IOException if something goes wrong.
     */
    public static Writer createTextWriter(String file) throws IOException
   {
       return createTextWriter(getPath(file));
   }

   public static Writer createTextWriter(Path file) throws IOException
   {
       ensureDirectoriesForFile( file );
       return Files.newBufferedWriter(file, StandardCharsets.UTF_8);
   }

    public static File getCanonicalFile(File file)
    {
        try
        {
            return file.getCanonicalFile();
        }
        catch (Exception e)
        {
            Log.error("Can't resolve path " + file.getPath(), e );
            return null;
        }
    }

    public static void setWorkingDir( File  path )
    {
       try
       {
          System.setProperty("user.dir", path.getAbsolutePath() );
       } catch ( Exception e)
       {
       }
    }

    public static File makeAbsolute( String relativePath )
    {
        return new File( relativePath ).getAbsoluteFile();
    }

    public static String makeRelative( Path path )
    {
      try
      {
         return makeRelative(path.toFile());
      }
      catch( Exception e)
      {
      }
      return path.toUri().toString();
    }

    public static String makeRelative( File path )
    {
       if ( path.isAbsolute() )
       {
         String absPath = path.getAbsolutePath();
         File common_dir = new File( System.getProperty("user.dir"));

         StringBuilder relPath = new StringBuilder();

         while ( !absPath.startsWith( common_dir.getAbsolutePath() ))
         {
            common_dir = common_dir.getParentFile();
            relPath.append("..");
            relPath.append(File.separator);
            if ( common_dir == null ) return absPath;
         }

         absPath = absPath.substring( common_dir.getAbsolutePath().length() );
         if ( absPath.startsWith( File.separator ))
            relPath.append( absPath.substring( File.separator.length() ));
         else
            relPath.append( absPath );

         return relPath.toString();
       }
       else
          return path.getPath();
    }

    public static void ensureDirectoriesForFile( Path file ) throws IOException
    {
       if ( file != null )
       {
            Path parent = file.getParent();
            if ( parent != null )
            {
                 if (!Files.exists(parent))
                 {
                     Files.createDirectories(parent);
                     Log.info("Created " + parent );
                 }
            }
       }
    }

    public static void setFileChooserDirectory(JFileChooser fileChooser, File dir)
    {
        try
        {
            // Reset the working directory to avoid exception on some platforms.
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
    private static Frame  fileChooserFrame_ = null;

    /** Predefined file filter for json files with extension json or js. */
    public static FileNameExtensionFilter filterJson   = new FileNameExtensionFilter( "Json File", "json", "js" );

    /** Predefined file filter for log files with extension log or txt. */
    public static FileNameExtensionFilter filterLog = new FileNameExtensionFilter( "Log File", "log", "txt" );

    /** Mode for selecting files or directories  to write or create.*/
    public static final int SAVE = 1;

    /** Mode for selecting files or directories to read.*/
    public static final int OPEN = 2;

    private static Component prepareFileChooser(Component comp)
    {
        if ( comp == null )
        {
            if ( null == fileChooserFrame_ )
            {
                fileChooserFrame_ = new Frame ();
                fileChooserFrame_.setIconImage( IconCache.getAppSmallImage() );
            }
            comp = fileChooserFrame_;
        }

        if (null == fileChooser_)
        {
            fileChooser_ = new JFileChooser();
            fileChooser_.addChoosableFileFilter(filterJson);
            fileChooser_.addChoosableFileFilter(filterLog);
        }
        return comp;
    }

    /**
     * Select a directory.
     * @param comp  The calling component. Used to give the dialog a parent.
     * @param prefPrefix Preference-prefix to store the last used directory and file.
     *                   E.g. "MyApp.OpenFile". Can be null.
     * @param dialogTitle Title of the dialog.
     * @param mode Mode of operation. Possible values are {@link OPEN} and {@link SAVE}.
     * @return Null or the selected directory.
     */
    public static File selectDirectory(Component comp, String prefPrefix, String dialogTitle, int mode )
    {
        comp = prepareFileChooser(comp);

        String dirPath = Store.getString(prefPrefix+".dir", null);
        File dir = (dirPath != null && !dirPath.isEmpty()) ? new File(dirPath) : null;
        fileChooser_.setDialogTitle(dialogTitle);
        fileChooser_.setMultiSelectionEnabled(false);
        fileChooser_.setFileHidingEnabled(true);
        fileChooser_.setFileFilter(null);
        fileChooser_.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        setFileChooserDirectory(fileChooser_, dir);
        int response = (mode == OPEN) ? fileChooser_.showOpenDialog(comp) : fileChooser_.showSaveDialog(comp);
        if (response == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser_.getSelectedFile().getAbsoluteFile();
            Store.setString(prefPrefix+".dir", file.getParent());
            return file;
        }
        return null;
    }

    /**
     * Select one file.
     * @param comp  The calling component. Used to give the dialog a parent.
     * @param prefPrefix Preference-prefix to store the last used directory and file.
     *                   E.g. "MyApp.OpenFile". Can be null.
     * @param dialogTitle Title of the dialog.
     * @param mode Mode of operation. Possible values are {@link OPEN} and {@link SAVE}.
     * @param filter File filer to use. Can be null.
     * @return Null or the selected file.
     */
    public static File selectFile(Component comp, String prefPrefix, String dialogTitle, int mode, FileFilter filter )
    {
        final File f[] = internal_selectFiles( comp, prefPrefix, dialogTitle, mode, filter, false );
        if( f != null && f.length > 0)
            return f[0];
        else
            return null;
    }

    /**
     * Select multiple files.
     * @param comp  The calling component. Used to give the dialog a parent.
     * @param prefPrefix Preference-prefix to store the last used directory.
     *                   E.g. "MyApp.OpenFile". Can be null.
     * @param dialogTitle Title of the dialog.
     * @param mode Mode of operation. Possible values are {@link OPEN} and {@link SAVE}.
     * @param filter File filer to use. Can be null.
     * @return Null or a none-empty array.
     */
    public static File[] selectFiles(Component comp, String prefPrefix, String dialogTitle, int mode, FileFilter filter )
    {
        return internal_selectFiles( comp, prefPrefix, dialogTitle, mode, filter, true );
    }

    private static File[] internal_selectFiles(Component comp, String prefPrefix, String dialogTitle, int mode, FileFilter filter, boolean multiSelectionAllowed )
    {
        comp = prepareFileChooser(comp);

        if ( mode == SAVE ) multiSelectionAllowed = false;


        fileChooser_.setDialogTitle(dialogTitle);
        fileChooser_.setMultiSelectionEnabled(multiSelectionAllowed);
        fileChooser_.setFileHidingEnabled(true);
        fileChooser_.setFileFilter( filter );

        String lastDir;
        String lastFile;
        if ( prefPrefix != null )
        {
            lastDir = Store.getString(prefPrefix+".dir", null);
            lastFile= multiSelectionAllowed ? null : Store.getString(prefPrefix+".file", null);
        }
        else
        {
            lastDir = null;
            lastFile= null;
        }

        if ( lastDir == null || lastFile == null)
        {
            fileChooser_.setSelectedFile(new File(""));
        }
        else
        {
            if ( !lastDir.endsWith(File.separator))
                lastDir = lastDir+File.separator;
            File f = new File( lastDir+lastFile );
            fileChooser_.setSelectedFile(f);
        }
        fileChooser_.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if ( lastDir != null )
        {
            setFileChooserDirectory(fileChooser_, new File(lastDir));
        }

        // Ensure Splash is off
        WaitSplash.showWait( false );

        int response = (mode == OPEN) ? fileChooser_.showOpenDialog(comp) : fileChooser_.showSaveDialog(comp);
        if (response == JFileChooser.APPROVE_OPTION)
        {
            File files[] = null;

            if ( mode == SAVE )
            {
               files = new File[1];
               files[0] = fileChooser_.getSelectedFile();
               String fileName = files[0].getName();
               int extIndex = fileName.lastIndexOf('.');
               if ( extIndex < 0 )
               {
                  // Try to construct a extention from a choosen FileFilter.
                  final FileFilter ff = fileChooser_.getFileFilter();
                  if ( ff instanceof FileNameExtensionFilter )
                  {
                     FileNameExtensionFilter fnef = (FileNameExtensionFilter)ff;
                     final String ext[] = fnef.getExtensions();
                     if ( ext != null && ext.length>0)
                        files[0] = new File( files[0].getParent(), fileName + "." + ext[0] );
                  }
               }
            }
            else if ( multiSelectionAllowed )
            {
                files = fileChooser_.getSelectedFiles();
                if ( files != null && files.length == 0 ) files = null;
            }
            else
            {
               files = new File[1];
               files[0] = fileChooser_.getSelectedFile();
            }

            if ( files != null )
            {
                Store.setString(prefPrefix+".dir", files[0].getParentFile().getAbsolutePath() );
                if ( !multiSelectionAllowed )
                    Store.setString(prefPrefix+".file", files[0].getName());
            }
            return files;
        }
        return null;
    }

    /**
     * Guess a path for some file/uri.<br>
     * Main reason is to enable access to JARs via Path-API.<br>
     * A URI inside a Jar looks as follows:
     *   jar:file:/app.jar!/com/mycompany/myapp/import.txt
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
                   return java.nio.file.Paths.get(uri);

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
                      }
                      catch (FileSystemNotFoundException fsnf)
                      {
                         fs = FileSystems.newFileSystem(fsuri, new HashMap<String, Object>());
                      }
                      return fs.getPath(file.substring(si + 1));
                   }
                   catch (Exception ex2)
                   {
                      Log.error("Can't decode Jar URI: " + ex2.getMessage(), ex2);
                   }
                }
             }
          }
          else
          {
             return null;
          }
       }
       catch (Throwable e)
       {
          //Log.log_error("URI Error:" + e.getMessage());
       }
       return FileSystems.getDefault().getPath(file);
    }

    /**
     * Creates an image from a swing component.The component have to be a valid layout (valid size).
     * @param comp The component from which we take a snapshot.
     * @return The generated image.
     */
    public static BufferedImage createImageFromComponent(JComponent comp)
    {
        return createImageFromComponent( comp, new Rectangle(comp.getSize()));
    }

    /**
     * Creates an image from a rectangle area of a swing component.The component have to be a valid layout (valid size).
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
     * Creates an image from buffer. Returns null if data contains no image or format is not supported by runtime.
     * @param data The raw data.
     * @return The generated image.
     */
    public static BufferedImage createImageFromBuffer(byte [] data)
    {
        try
        {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            return image;
        }
        catch ( Exception e)
        {
            Log.error("Failed to decode image: "+e.getMessage(), e);
            return null;
        }
    }

    // Get restricted strack trace
    public static String getRestrictedStackTrace(Throwable t, String prefix, int max_lines)
    {
        StringWriter sw = new StringWriter(1000);
        t.printStackTrace(new PrintWriter(sw));

        String lines[] = sw.toString().split("\\r\\n|\\n|\\r");

        StringBuilder sb = new StringBuilder(2048);
        int l=0;
        while ( l<max_lines && l<lines.length)
        {
            sb.append(prefix).append(lines[l++]).append("\n");
        }
        if ( l<lines.length )
            sb.append(prefix).append("...\n");
        return sb.toString();
    }

}
