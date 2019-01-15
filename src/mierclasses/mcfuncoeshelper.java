/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
            java.security.CodeSource codeSource = mierstockfx.Mierstockfx.class.getProtectionDomain().getCodeSource();
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
