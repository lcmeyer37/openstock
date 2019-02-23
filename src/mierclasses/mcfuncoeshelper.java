/*
 * Copyright (C) 2019 Lucas Meyer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mierclasses;

/**
 *
 * @author lucasmeyer
 */
public class mcfuncoeshelper
{
    public static void mostrarmensagem(String mensagem)
    {
        javax.swing.JOptionPane.showMessageDialog(null, mensagem, "Open Stock", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void setarclipboard(String mensagem)
    {
        java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(mensagem);
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
    
    public static String retornarpathbaseprograma()
    {
        try
        {
            java.security.CodeSource codeSource = main.OpenStock.class.getProtectionDomain().getCodeSource();
            java.io.File jarFile = new java.io.File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            
            return jarDir;
        }
        catch (Exception e) 
        {
            //ignorar
        }
        
        return "";
    }
    
    public static String retornarSubstringIndices(String original, int indexInicioInclusivo, int indexFimInclusivo)
    {
        //[0] a [3] -> 3+1 - 0 = 4 chars
        //[4] a [5] -> 5+1 - 4 = 2 chars
        int tamanhoarray = (indexFimInclusivo+1) - indexInicioInclusivo;
        char[] ch = new char[tamanhoarray];
        original.getChars(indexInicioInclusivo, (indexFimInclusivo+1), ch, 0);
        
        return String.valueOf(ch);
    }
    
    public static String retornarStringArquivo(String path) 
    {
        try
        {
            byte[] encoded = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
            return new String(encoded, java.nio.charset.StandardCharsets.UTF_8);   
        }
        catch (java.io.IOException ex)
        {
            return null;
        }
    }
}
