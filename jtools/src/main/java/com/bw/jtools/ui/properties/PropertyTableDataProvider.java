package com.bw.jtools.ui.properties;

import java.awt.Color;

import org.netbeans.swing.outline.RenderDataProvider;

/**
 * Implementation of a "RenderDataProvider".
 */
public class PropertyTableDataProvider implements RenderDataProvider
{
   @Override
   public java.awt.Color getBackground(Object o)
   {
      return null;
   }

   @Override
   public String getDisplayName(Object o)
   {
      if (o instanceof PropertyValue)
      {
         PropertyValue node = (PropertyValue)o;
         if ( node.displayName_ != null )
            return node.displayName_;
      }
      return "";
   }

   @Override
   public java.awt.Color getForeground(Object o)
   {
      return Color.BLACK;
   }

   @Override
   public javax.swing.Icon getIcon(Object o)
   {
      return null;
   }

   @Override
   public String getTooltipText(Object o)
   {
      return null;
   }

   @Override
   public boolean isHtmlDisplayName(Object o)
   {
      return false;
   }

}
